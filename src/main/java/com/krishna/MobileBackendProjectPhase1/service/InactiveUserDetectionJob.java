package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class InactiveUserDetectionJob {

    private static final Logger log =
            LoggerFactory.getLogger(InactiveUserDetectionJob.class);

    private final UserRepository userRepository;

    public InactiveUserDetectionJob(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void detectInactiveUsers() {

        LocalDateTime cutoffDate =
                LocalDateTime.now().minusDays(30);

        List<User> inactiveUsers =
                userRepository.findInactiveUsers(cutoffDate);

        log.info(
                "Inactive user detection completed. Inactive users: {}",
                inactiveUsers.size()
        );

        for (User user : inactiveUsers) {
            log.info(
                    "Inactive user detected. UserId: {}, Email: {}, LastUpdated: {}",
                    user.getId(),
                    user.getEmail(),
                    user.getUpdatedAt()
            );
        }
    }
}