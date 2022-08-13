package org.daemio.merch.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners({ AuditingEntityListener.class })
public class Merch {
    
    @Id
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private MerchStatus status;
    
    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @PastOrPresent
    @CreatedDate
    private LocalDateTime createdTime;

    @PastOrPresent
    @LastModifiedDate
    private LocalDateTime modifiedTime;
}
