package org.daemio.merch.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(
    name = "merch",
    indexes = {@Index(name = "status_idx", columnList = "status")})
@Getter
@Setter
@Accessors(chain = true)
public class Merch extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "merch_id")
  private UUID id;

  @NotNull @Enumerated(EnumType.ORDINAL)
  private MerchStatus status;

  @NotNull private UUID vendor;

  @NotBlank @Column(nullable = false)
  private String title;

  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "merch", fetch = FetchType.EAGER)
  private Collection<Image> images = new ArrayList<>();

  @NotNull @Positive @Column(nullable = false)
  private BigDecimal price;
}
