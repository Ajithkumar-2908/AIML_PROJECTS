package com.ajithkumar.ragchatmanagement.service.impl;

import com.ajithkumar.ragchatmanagement.dto.ChatSessionResponse;
import com.ajithkumar.ragchatmanagement.dto.CreateChatSessionRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatMessage;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.repository.ChatMessageRepository;
import com.ajithkumar.ragchatmanagement.repository.ChatSessionRepository;
import com.ajithkumar.ragchatmanagement.repository.UserRepository;
import com.ajithkumar.ragchatmanagement.service.ChatSessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of ChatSessionService.
 */

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final Logger log = LoggerFactory.getLogger(ChatSessionServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new chat session for a user.
     *
     * @param request the create chat session request
     * @return the created chat session response
     * @throws IllegalArgumentException if user does not exist
     */
    @Override
    @Transactional
    public ChatSession createChatSession(CreateChatSessionRequest request) {
        log.info("Creating chat session for user: {}", request.getUserId());

        // Verify user exists
        if (!userExists(request.getUserId())) {
            log.warn("User not found: {}", request.getUserId());
            throw new IllegalArgumentException(
                    "User with ID " + request.getUserId() + " does not exist"
            );
        }

        try {
            // Create new chat session
            ChatSession chatSession = new ChatSession(request.getUserId(),
                    (request.getIsFavourite() != null ? request.getIsFavourite() : false),  request.getSessionName(),LocalDateTime.now(), LocalDateTime.now());


            log.debug("Saving chat session: {}", chatSession.getId());

            // Save to database
            ChatSession savedSession = chatSessionRepository.save(chatSession);

            log.info("Chat session created successfully: {}", savedSession.getId());
            return savedSession;
        } catch (Exception e) {
            log.error("Error creating chat session", e);
            throw e;
        }
    }

    /**
     * Retrieve a chat session by ID.
     *
     * @param sessionId the ID of the chat session
     * @return the chat session response if found
     * @throws IllegalArgumentException if session is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ChatSession getChatSessionById(UUID sessionId) {
        log.info("Fetching chat session: {}", sessionId);

        try {
            ChatSession chatSession = getSession(sessionId);

            log.info("Chat session retrieved: {}", chatSession.getId());
            return chatSession;
        } catch (Exception e) {
            log.error("Error fetching chat session", e);
            throw e;
        }
    }

    /**
     * Delete a chat session by ID.
     *
     * @param sessionId the ID of the chat session to delete
     * @throws IllegalArgumentException if session is not found
     */
    @Transactional
    public void deleteSession(UUID sessionId) {

        log.info("Deleting chat session: {}", sessionId);
        ChatSession session = getSession(sessionId);
        // Delete the messages of a chat session.
        chatMessageRepository.deleteByChatSessionId(sessionId);
        // Delete the chat session
        chatSessionRepository.deleteById(sessionId);
        log.info("Chat session deleted successfully: {}", sessionId);

    }

    /**
     * Verify if a user exists.
     *
     * @param userId the ID of the user
     * @return true if user exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean userExists(UUID userId) {
        log.debug("Checking if user exists: {}", userId);
        boolean exists = userRepository.existsById(userId);
        log.debug("User {} exists: {}", userId, exists);
        return exists;
    }

    /**
     * Rename a chat session.
     *
     * @param sessionId the ID of the chat session to rename
     * @param newName   the new name for the chat session
     * @return the updated chat session
     * @throws IllegalArgumentException if session is not found
     */
    public ChatSession renameSession(UUID sessionId, String newName) {
        log.info("Renaming chat session: {}", sessionId);

        ChatSession chatSession = getSession(sessionId);

        chatSession.setSessionName(newName);
        chatSession.setUpdatedDate(LocalDateTime.now());

        ChatSession updatedSession = chatSessionRepository.save(chatSession);

        log.info("Chat session renamed successfully: {}", updatedSession.getId());
        return updatedSession;
    }

    /**
     * Mark or unmark a chat session as favourite.
     *
     * @param sessionId the ID of the chat session
     * @param favourite true to mark as favourite, false to unmark
     * @return the updated chat session
     * @throws IllegalArgumentException if session is not found
     */
    public ChatSession setFavourite(UUID sessionId, boolean favourite) {
        log.info("Setting favourite={} for chat session: {}", favourite, sessionId);

        ChatSession chatSession  = getSession(sessionId);

        chatSession.setIsFavourite(favourite);
        chatSession.setUpdatedDate(LocalDateTime.now());

        ChatSession updatedSession = chatSessionRepository.save(chatSession);

        log.info("Chat session favourite status updated successfully: {}", updatedSession.getId());
        return updatedSession;
    }

    /**
     * Retrieve paginated chat messages for a session.
     *
     * @param sessionId the ID of the chat session
     * @param pageable  pagination information
     * @return paginated chat messages
     * @throws IllegalArgumentException if session is not found
     */
    public Page<ChatMessage> getMessages(UUID sessionId, Pageable pageable) {

        log.info("Fetching messages for chat session: {}", sessionId);
        return chatMessageRepository.findByChatSessionIdOrderByCreatedDateDesc(sessionId, pageable);

    }

    // Helper method to get ChatSession by ID with error handling.
    private ChatSession getSession(UUID sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Chat session not found: {}", sessionId);
                    return new IllegalArgumentException(
                            "Chat session with ID " + sessionId + " not found"
                    );
                });
    }


}
