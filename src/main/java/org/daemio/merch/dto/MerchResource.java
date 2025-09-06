package org.daemio.merch.dto;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;

import org.daemio.merch.model.MerchStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(
    value = {"createdTime", "modifiedTime"},
    allowGetters = true,
    ignoreUnknown = true)
public class MerchResource {

  private String merchId;

  @Schema(description = "The state of the piece of merch")
  private MerchStatus status;

  @Schema(description = "Title of the piece of merch")
  @NotBlank private String title;

  @Schema(description = "Price of the piece of merch")
  @NotNull @Positive private BigDecimal price;

  @Schema(description = "Long decription of the piece of merch")
  @ToStringExclude
  private String description;

  @EqualsAndHashCode.Exclude private Instant createdTime;

  @EqualsAndHashCode.Exclude private Instant modifiedTime;
}
