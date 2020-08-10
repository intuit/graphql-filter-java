package com.intuit.graphql.demo.persistence.repository;

import com.intuit.graphql.demo.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddressRepository extends JpaRepository<AddressEntity, String>, JpaSpecificationExecutor<AddressEntity> {
}
