package org.daemio.merch.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.daemio.merch.config.HandlerConfig;
import org.daemio.merch.config.WebSecurityConfig;
import org.daemio.merch.dto.MerchPage;
import org.daemio.merch.dto.MerchResource;
import org.daemio.merch.error.MerchNotFoundException;
import org.daemio.merch.mapper.MerchMapperImpl;
import org.daemio.merch.service.MerchService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchController.class)
@Import({WebSecurityConfig.class, MerchMapperImpl.class, HandlerConfig.class})
@DisplayName("Merch controller tests")
@ActiveProfiles("unit-test")
@WithMockUser(roles = {"FAN"})
public class MerchControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;

  @MockitoBean private MerchService merchService;

  @DisplayName(
      "when calling for a list of merch then the service should return "
          + "succussfully and the response should be an array of merch")
  @Test
  public void whenGetMerch_thenReturnSuccessfulList() throws Exception {
    var merch = new MerchResource();
    var expectedResponse = new MerchPage();
    expectedResponse.setMerch(Arrays.asList(merch));

    when(merchService.getMerchPage(any())).thenReturn(expectedResponse);

    mvc.perform(get("/api/v1/merch"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.merch").isArray())
        .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));
  }

  @DisplayName(
      "given some page number when calling for a list of merch then the "
          + "service should return succesfully and the response should indicate the page "
          + "and have a list of merch")
  @Test
  public void givenPageNo_whenGetMerch_thenReturnSuccessfulList() throws Exception {
    var page = 2;
    var merch = new MerchResource();
    var expectedResponse = new MerchPage();
    expectedResponse.setMerch(Arrays.asList(merch));
    expectedResponse.setPage(page);
    expectedResponse.setSize(25);
    expectedResponse.setTotalPages(2);

    when(merchService.getMerchPage(any())).thenReturn(expectedResponse);

    mvc.perform(get("/api/v1/merch").queryParam("page", Integer.toString(page)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.merch").isArray())
        .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));
  }

  @DisplayName(
      "given a merch id and the merch item exists, when calling for merch "
          + "by this id then the service should return successfully and the response should be "
          + "the specified merch item")
  @Test
  public void whenGetMerchItem_thenReturnSuccessfulItem() throws Exception {
    var merchId = UUID.randomUUID();
    var merch = new MerchResource();
    merch.setTitle("Some dumb title");
    merch.setCreatedTime(Instant.now());
    merch.setModifiedTime(Instant.now());

    when(merchService.getMerch(merchId)).thenReturn(merch);

    mvc.perform(get("/api/v1/merch/{merchId}", merchId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(merch.getTitle()));
  }

  @DisplayName(
      "given a merch id where the item does not exist, when calling for merch "
          + "by this id then the service sould return a 404 Not Found")
  @Test
  public void givenMerchNotThere_whenGetMerchItem_thenGetNotFoundResponse() throws Exception {
    var merchId = UUID.randomUUID();

    when(merchService.getMerch(merchId)).thenThrow(new MerchNotFoundException());

    mvc.perform(get("/api/v1/merch/{merchId}", merchId)).andExpect(status().isNotFound());
  }

  @WithMockUser(roles = {"VENDOR"})
  @Test
  public void whenPostMerch_thenGetBackLocation() throws Exception {
    var merchId = UUID.randomUUID();
    when(merchService.saveMerch(any()))
        .thenReturn(MerchResource.builder().merchId(merchId.toString()).build());

    var merchRequest =
        MerchResource.builder().title("some merch").price(BigDecimal.valueOf(10.00)).build();

    mvc.perform(
            post("/api/v1/merch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(merchRequest)))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(
            header().string(HttpHeaders.LOCATION, String.format("/api/v1/merch/%s", merchId)));
  }

  @DisplayName("Controller validates the request object")
  @WithMockUser(roles = {"VENDOR"})
  @ParameterizedTest(name = "{index} => Missing {1}")
  @MethodSource("argumentsForValidation")
  public void merchController_validates_request(String requestJson, String invalidParameter)
      throws JsonProcessingException, Exception {
    mvc.perform(post("/api/v1/merch").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.errors").exists())
        .andExpect(jsonPath("$.errors.%s", invalidParameter).exists());
  }

  private static Stream<Arguments> argumentsForValidation() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    return Stream.of(
        Arguments.of(
            mapper.writeValueAsString(MerchResource.builder().title("title").build()), "price"),
        Arguments.of(
            mapper.writeValueAsString(
                MerchResource.builder().price(BigDecimal.valueOf(10.00)).build()),
            "title"));
  }
}
