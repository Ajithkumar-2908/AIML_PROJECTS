package com.ajithkumar.ragchatmanagement.servicetest;

import com.ajithkumar.ragchatmanagement.service.ChatMessageService;
import com.ajithkumar.ragchatmanagement.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import com.ajithkumar.ragchatmanagement.dto.ChatMessageResponse;
import com.ajithkumar.ragchatmanagement.dto.CreateChatMessageRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatMessage;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.repository.ChatMessageRepository;
import com.ajithkumar.ragchatmanagement.repository.ChatSessionRepository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceImplTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private UUID sessionId;
    private UUID messageId;
    private ChatSession session;
    private ChatMessage message;
    private CreateChatMessageRequest request;
    private Map<String, Object> context;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        session = new ChatSession();
        session.setId(sessionId);

        message = new ChatMessage();
        message.setId(messageId);
        message.setChatSession(session);

        request = new CreateChatMessageRequest();
        request.setContent("Hello");

        context = new HashMap<>();
        context.put("source", "rag");
        request.setContext(context);
        request.setAttachment("file.pdf");
    }

    @Test
    @DisplayName("createMessage - success path")
    void createMessage_Success() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));
        given(chatMessageRepository.save(any(ChatMessage.class))).willAnswer(invocation -> {
            ChatMessage m = invocation.getArgument(0);
            m.setId(messageId);
            m.setCreatedDate(LocalDateTime.now());
            m.setUpdatedDate(LocalDateTime.now());
            return m;
        });

        // Invoke the method
        ChatMessageResponse response = chatMessageService.createMessage(sessionId, request);

        // Assert
        assertThat(response.getContent()).isEqualTo("Hello");
        assertThat(response.getSessionId()).isEqualTo(sessionId);

    }

    @Test
    @DisplayName("createMessage - throws when session not found")
    void createMessage_SessionNotFound() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.empty());

        // Invoke & Assert
        assertThatThrownBy(() -> chatMessageService.createMessage(sessionId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chat session with ID " + sessionId + " not found");
    }

    @Test
    @DisplayName("deleteMessage - success path")
    void deleteMessage_Success() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));
        given(chatMessageRepository.findById(messageId)).willReturn(Optional.of(message));

        // Invoke
        chatMessageService.deleteMessage(sessionId, messageId);

        // Assert
        verify(chatMessageRepository).delete(message);
    }

    @Test
    @DisplayName("deleteMessage - throws when session not found")
    void deleteMessage_SessionNotFound() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.empty());

        // Invoke & Assert
        assertThatThrownBy(() -> chatMessageService.deleteMessage(sessionId, messageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chat session with ID " + sessionId + " not found");
    }

    @Test
    @DisplayName("deleteMessage - throws when message not found")
    void deleteMessage_MessageNotFound() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));
        given(chatMessageRepository.findById(messageId)).willReturn(Optional.empty());

        // Invoke & Assert
        assertThatThrownBy(() -> chatMessageService.deleteMessage(sessionId, messageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message with ID " + messageId + " not found");
    }

    @Test
    @DisplayName("deleteMessage - throws when message belongs to wrong session")
    void deleteMessage_WrongSession() {
        // Pre-requisites
        ChatSession wrongSession = new ChatSession();
        wrongSession.setId(UUID.randomUUID());
        message.setChatSession(wrongSession);

        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));
        given(chatMessageRepository.findById(messageId)).willReturn(Optional.of(message));

        // Invoke & Assert
        assertThatThrownBy(() -> chatMessageService.deleteMessage(sessionId, messageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message does not belong to the given session");
    }
}

