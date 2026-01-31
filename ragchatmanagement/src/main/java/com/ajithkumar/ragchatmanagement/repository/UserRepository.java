package com.ajithkumar.ragchatmanagement.repository;

import com.ajithkumar.ragchatmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity using Spring Data JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email address.
     *
     * @param email the email address
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email exists.
     *
     * @param email the email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

}
