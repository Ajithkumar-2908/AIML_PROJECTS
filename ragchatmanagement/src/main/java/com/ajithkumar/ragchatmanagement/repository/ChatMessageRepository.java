package com.ajithkumar.ragchatmanagement.repository;

import com.ajithkumar.ragchatmanagement.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for ChatMessage entity using Spring Data JPA.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * Find all messages in a specific chat session.
     *
     * @param chatSessionId the ID of the chat session
     * @return a list of chat messages
     */
    List<ChatMessage> findByChatSessionId(UUID chatSessionId);

    /**
     * Delete all messages in a specific chat session.
     *
     * @param sessionId the ID of the chat session
     */
    void deleteByChatSessionId(UUID sessionId);

    /**
     * Fetch all messages in a specific chat session.
     *
     * @param sessionId the ID of the chat session
     * @param pageable  pagination information
     * @return a page of chat messages
     */
    Page<ChatMessage> findByChatSessionId(UUID sessionId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.id = :chatSessionId ORDER BY cm.createdDate DESC")
    Page<ChatMessage> findByChatSessionIdOrderByCreatedDateDesc(UUID chatSessionId, Pageable pageable);

    /**
     * Find all messages in a specific chat session ordered by creation date.
     *
     * @param chatSessionId the ID of the chat session
     * @return a list of chat messages ordered by creation date
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.id = :chatSessionId ORDER BY cm.createdDate ASC")
    List<ChatMessage> findByChatSessionIdOrderByCreatedDateAsc(UUID chatSessionId);

    /**
     * Find all messages in a specific chat session ordered by creation date in descending order.
     *
     * @param chatSessionId the ID of the chat session
     * @return a list of chat messages ordered by creation date descending
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.id = :chatSessionId ORDER BY cm.createdDate DESC")
    List<ChatMessage> findByChatSessionIdOrderByCreatedDateDesc(UUID chatSessionId);

    /**
     * Count messages in a specific chat session.
     *
     * @param chatSessionId the ID of the chat session
     * @return the count of messages
     */
    long countByChatSessionId(UUID chatSessionId);

    /**
     * Find messages created after a specific date in a chat session.
     *
     * @param chatSessionId the ID of the chat session
     * @param createdAfter the date after which messages were created
     * @return a list of chat messages
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.id = :chatSessionId AND cm.createdDate > :createdAfter ORDER BY cm.createdDate ASC")
    List<ChatMessage> findMessagesCreatedAfter(UUID chatSessionId, LocalDateTime createdAfter);

    /**
     * Find messages with attachments in a specific chat session.
     *
     * @param chatSessionId the ID of the chat session
     * @return a list of chat messages with attachments
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.id = :chatSessionId AND cm.attachment IS NOT NULL")
    List<ChatMessage> findMessagesWithAttachments(UUID chatSessionId);

}
