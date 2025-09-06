package org.daemio.merch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.daemio.merch.config.UserContext;
import org.daemio.merch.dto.MerchPage;
import org.daemio.merch.dto.MerchResource;
import org.daemio.merch.error.MerchNotFoundException;
import org.daemio.merch.mapper.MerchMapper;
import org.daemio.merch.model.Merch;
import org.daemio.merch.repository.MerchRepository;

@Service
@Slf4j
public class MerchService {

  @Autowired private transient UserContext userContext;
  @Autowired private transient MerchRepository repo;
  @Autowired private transient MerchMapper mapper;

  public List<Merch> getMerchList() {
    log.info("Getting a merch list from data");

    List<Merch> list = new ArrayList<>();

    repo.findAll().forEach(list::add);

    return list;
  }

  public MerchPage getMerchPage(Pageable pageable) {
    log.info("Getting a page of merch from data");

    var results = repo.findAll(pageable);

    return mapper.pageToResponse(results);
  }

  // public MerchPage getMerchPage(Pageable pageable, List<MerchStatus> statusList) {
  //   log.info("Getting a page of merch from data");

  //   var results =
  //       repo.findAll((root, query, builder) -> root.get(Merch_.status).in(statusList), pageable);

  //   return mapper.pageToResponse(results);
  // }

  public MerchResource getMerch(String merchId) {
    return getMerch(UUID.fromString(merchId));
  }

  public MerchResource getMerch(UUID merchId) {
    log.info("Getting a piece of merch from data {}", merchId);

    return mapper.entityToModel(getMerchById(merchId));
  }

  private Merch getMerchById(UUID merchId) {
    var merch = repo.findById(merchId);

    return merch.orElseThrow(() -> new MerchNotFoundException());
  }

  private Merch getByMerchIdAndVendor(UUID merchId, UUID vendor) {
    var merch = repo.findByIdAndVendor(merchId, vendor);

    return merch.orElseThrow(() -> new MerchNotFoundException());
  }

  public MerchResource saveMerch(MerchResource merchRequest) {
    var merch = mapper.modelToEntity(merchRequest);

    merch.setVendor(userContext.getUserId());

    return mapper.entityToModel(save(merch));
  }

  public MerchResource updateMerch(MerchResource merchRequest) {
    var existingMerch =
        getByMerchIdAndVendor(UUID.fromString(merchRequest.getMerchId()), userContext.getUserId());

    var merch = mapper.modelToEntity(merchRequest);

    mapper.update(existingMerch, merch);

    return mapper.entityToModel(save(existingMerch));
  }

  public MerchResource mergeMerch(MerchResource merchRequest) {
    var existingMerch =
        getByMerchIdAndVendor(UUID.fromString(merchRequest.getMerchId()), userContext.getUserId());

    var merch = mapper.modelToEntity(merchRequest);

    mapper.delta(existingMerch, merch);

    return mapper.entityToModel(save(existingMerch));
  }

  private Merch save(Merch merch) {
    return repo.save(merch);
  }
}
