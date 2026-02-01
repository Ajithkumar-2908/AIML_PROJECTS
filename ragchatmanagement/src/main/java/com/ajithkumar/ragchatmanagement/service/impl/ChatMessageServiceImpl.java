package com.ajithkumar.ragchatmanagement.service.impl;

import com.ajithkumar.ragchatmanagement.dto.ChatMessageResponse;
import com.ajithkumar.ragchatmanagement.dto.CreateChatMessageRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatMessage;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.repository.ChatMessageRepository;
import com.ajithkumar.ragchatmanagement.repository.ChatSessionRepository;
import com.ajithkumar.ragchatmanagement.service.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of ChatMessageService.
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;


    /**
     * Create a new message in a chat session.
     * @param sessionId chat session ID
     * @param request create chat message request
     * @return created chat message response
     */
    @Transactional
    public ChatMessageResponse createMessage(UUID sessionId, CreateChatMessageRequest request) {

        // Get Chat Session.
        ChatSession session = getSession(sessionId);

        ChatMessage message = new ChatMessage();
        message.setChatSession(session);
        message.setContent(request.getContent());
        message.setContext(request.getContext());
        message.setAttachment(request.getAttachment());
        message.setCreatedDate(LocalDateTime.now());
        message.setUpdatedDate(LocalDateTime.now());

        log.info("Creating message in session: {}", sessionId);
        ChatMessage saved = chatMessageRepository.save(message);
        log.info("Message created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    /**
     * Delete a message from a chat session.
     * @param sessionId chat session ID
     * @param messageId chat message ID
     */
    @Transactional
    public void deleteMessage(UUID sessionId, UUID messageId) {

        // Verify session exists
        ChatSession session = getSession(sessionId);

        // Verify message exists and belongs to session
        ChatMessage message = getMessage(messageId);

        if (!message.getChatSession().getId().equals(sessionId)) {
            log.warn("Message {} does not belong to session {}", messageId, sessionId);
            throw new IllegalArgumentException("Message does not belong to the given session");
        }

        chatMessageRepository.delete(message);
        log.info("Deleted message {} from session {}", messageId, sessionId);

    }

    /**
     * Map ChatMessage entity to ChatMessageResponse DTO.
     * @param message chat message entity
     * @return chat message response DTO
     */
    private ChatMessageResponse mapToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSessionId(message.getChatSession().getId());
        response.setContent(message.getContent());
        response.setContext(message.getContext());
        response.setAttachment(message.getAttachment());
        response.setCreatedDate(message.getCreatedDate());
        return response;
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

    // Helper method to get ChatMessage by ID with error handling.
    private ChatMessage getMessage(UUID messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("Message not found: {}", messageId);
                    return new IllegalArgumentException(
                            "Message with ID " + messageId + " not found"
                    );
                });
    }



}
