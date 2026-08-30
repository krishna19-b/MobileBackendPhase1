package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);
}