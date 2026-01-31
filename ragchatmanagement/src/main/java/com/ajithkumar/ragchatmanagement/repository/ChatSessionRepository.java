package com.ajithkumar.ragchatmanagement.repository;

import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ChatSession entity using Spring Data JPA.
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    /**
     * Find all chat sessions for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of chat sessions
     */
    List<ChatSession> findByUserId(UUID userId);

    /**
     * Find favorite chat sessions for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of favorite chat sessions
     */
    @Query("SELECT cs FROM ChatSession cs WHERE cs.userId = :userId AND cs.isFavourite = true")
    List<ChatSession> findFavoriteSessionsByUserId(UUID userId);

    /**
     * Find a chat session by name and user ID.
     *
     * @param sessionName the name of the session
     * @param userId the ID of the user
     * @return an Optional containing the chat session if found
     */
    Optional<ChatSession> findBySessionNameAndUserId(String sessionName, UUID userId);

    /**
     * Count chat sessions for a specific user.
     *
     * @param userId the ID of the user
     * @return the count of sessions
     */
    long countByUserId(UUID userId);

}
