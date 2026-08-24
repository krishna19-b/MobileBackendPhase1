package com.krishna.MobileBackendProjectPhase1.repository;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByMobileNumberAndIdNot(String mobileNumber, Long id);
}