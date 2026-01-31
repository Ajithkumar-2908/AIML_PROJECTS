package com.ajithkumar.ragchatmanagement.service.impl;

import com.ajithkumar.ragchatmanagement.dto.ChatSessionResponse;
import com.ajithkumar.ragchatmanagement.dto.CreateChatSessionRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.repository.ChatSessionRepository;
import com.ajithkumar.ragchatmanagement.repository.UserRepository;
import com.ajithkumar.ragchatmanagement.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of ChatSessionService with blocking (non-reactive) operations.
 */
//@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService {

    Logger log = LoggerFactory.getLogger(ChatSessionServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

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
            ChatSession chatSession = chatSessionRepository.findById(sessionId)
                    .orElseThrow(() -> {
                        log.warn("Chat session not found: {}", sessionId);
                        return new IllegalArgumentException(
                                "Chat session with ID " + sessionId + " not found"
                        );
                    });

            log.info("Chat session retrieved: {}", chatSession.getId());
            return chatSession;
        } catch (Exception e) {
            log.error("Error fetching chat session", e);
            throw e;
        }
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

    public ChatSession renameSession(UUID sessionId, String newName) {
        log.info("Renaming chat session: {}", sessionId);

        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Chat session not found: {}", sessionId);
                    return new IllegalArgumentException(
                            "Chat session with ID " + sessionId + " not found"
                    );
                });

        chatSession.setSessionName(newName);
        chatSession.setUpdatedDate(LocalDateTime.now());

        ChatSession updatedSession = chatSessionRepository.save(chatSession);

        log.info("Chat session renamed successfully: {}", updatedSession.getId());
        return updatedSession;
    }

    public ChatSession setFavourite(UUID sessionId, boolean favourite) {
        log.info("Setting favourite={} for chat session: {}", favourite, sessionId);

        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Chat session not found: {}", sessionId);
                    return new IllegalArgumentException(
                            "Chat session with ID " + sessionId + " not found"
                    );
                });

        chatSession.setIsFavourite(favourite);
        chatSession.setUpdatedDate(LocalDateTime.now());

        ChatSession updatedSession = chatSessionRepository.save(chatSession);

        log.info("Chat session favourite status updated successfully: {}", updatedSession.getId());
        return updatedSession;
    }

    /**
     * Convert ChatSession entity to ChatSessionResponse DTO.
     *
     * @param chatSession the chat session entity
     * @return the chat session response DTO
     */
//    private ChatSessionResponse toResponse(ChatSession chatSession) {
//        return ChatSessionResponse.builder()
//                .id(chatSession.getId())
//                .userId(chatSession.getUserId())
//                .sessionName(chatSession.getSessionName())
//                .isFavourite(chatSession.getIsFavourite())
//                .createdDate(chatSession.getCreatedDate())
//                .updatedDate(chatSession.getUpdatedDate())
//                .build();
//    }

}
