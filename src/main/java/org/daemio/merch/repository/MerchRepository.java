package org.daemio.merch.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.daemio.merch.model.Merch;
import org.daemio.merch.model.MerchStatus;

public interface MerchRepository
    extends JpaRepository<Merch, UUID>, JpaSpecificationExecutor<Merch> {

  @Query("select m from Merch m where m.status in (?1)")
  Page<Merch> findAllWithStatus(Collection<MerchStatus> statuses, Pageable pageable);

  Optional<Merch> findByIdAndVendor(UUID merchId, UUID vendor);
}
