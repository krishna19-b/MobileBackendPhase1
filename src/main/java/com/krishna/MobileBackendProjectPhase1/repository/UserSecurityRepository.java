package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.entity.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {
    Optional<UserSecurity> findByEmail(String gmail);
}
