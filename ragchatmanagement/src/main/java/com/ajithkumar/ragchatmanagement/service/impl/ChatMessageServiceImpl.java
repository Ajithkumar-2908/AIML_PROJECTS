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

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;


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

        ChatMessage saved = chatMessageRepository.save(message);

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteMessage(UUID sessionId, UUID messageId) {

        // Verify session exists
        ChatSession session = getSession(sessionId);

        // Verify message exists and belongs to session
        ChatMessage message = getMessage(messageId);

        if (!message.getChatSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("Message does not belong to the given session");
        }

        chatMessageRepository.delete(message);
    }

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

    private ChatSession getSession(UUID sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Chat session not found: {}", sessionId);
                    return new IllegalArgumentException(
                            "Chat session with ID " + sessionId + " not found"
                    );
                });
    }

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
