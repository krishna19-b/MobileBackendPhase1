package com.krishna.MobileBackendProjectPhase1.specification;

import com.krishna.MobileBackendProjectPhase1.entity.User;

import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> nameContains(String name) {

        return (root, query, criteriaBuilder) -> {

            if (name == null || name.trim().isEmpty()) {

                return criteriaBuilder.conjunction();
            }

            String searchValue =
                    "%" + name.trim().toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("firstName")
                            ),
                            searchValue
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("lastName")
                            ),
                            searchValue
                    )
            );
        };
    }
}