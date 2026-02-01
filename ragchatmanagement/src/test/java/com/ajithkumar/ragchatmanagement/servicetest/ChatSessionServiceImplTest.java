package com.ajithkumar.ragchatmanagement.servicetest;

import com.ajithkumar.ragchatmanagement.dto.CreateChatSessionRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatMessage;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.repository.ChatMessageRepository;
import com.ajithkumar.ragchatmanagement.repository.ChatSessionRepository;
import com.ajithkumar.ragchatmanagement.repository.UserRepository;
import com.ajithkumar.ragchatmanagement.service.impl.ChatSessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatSessionServiceImpl Tests")
class ChatSessionServiceImplTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatSessionServiceImpl chatSessionService;

    private UUID userId;
    private UUID sessionId;
    private ChatSession session;
    private CreateChatSessionRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();

        session = new ChatSession();
        session.setId(sessionId);
        session.setUserId(userId);
        session.setSessionName("Test Session");

        request = new CreateChatSessionRequest();
        request.setUserId(userId);
        request.setSessionName("Test Session");
        request.setIsFavourite(false);
    }

    @Test
    @DisplayName("createChatSession - success path")
    void createChatSession_Success() {
        // Pre-requisites
        given(userRepository.existsById(userId)).willReturn(true);
        given(chatSessionRepository.save(any(ChatSession.class))).willAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(sessionId);
            s.setCreatedDate(LocalDateTime.now());
            s.setUpdatedDate(LocalDateTime.now());
            return s;
        });

        // Invoke
        ChatSession created = chatSessionService.createChatSession(request);

        // Assert
        assertThat(created.getId()).isEqualTo(sessionId);
        assertThat(created.getUserId()).isEqualTo(userId);
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("createChatSession - throws when user not found")
    void createChatSession_UserNotFound() {
        // Pre-requisites
        given(userRepository.existsById(userId)).willReturn(false);

        // Invoke & Assert
        assertThatThrownBy(() -> chatSessionService.createChatSession(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with ID " + userId + " does not exist");
    }

    @Test
    @DisplayName("getChatSessionById - success")
    void getChatSessionById_Success() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));

        // Invoke
        ChatSession result = chatSessionService.getChatSessionById(sessionId);

        // Assert
        assertThat(result).isEqualTo(session);
        verify(chatSessionRepository).findById(sessionId);
    }

    @Test
    @DisplayName("getChatSessionById - throws when session not found")
    void getChatSessionById_NotFound() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.empty());

        // Invoke & Assert
        assertThatThrownBy(() -> chatSessionService.getChatSessionById(sessionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Chat session with ID " + sessionId + " not found");
    }

    @Test
    @DisplayName("deleteSession - success")
    void deleteSession_Success() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));

        // Invoke
        chatSessionService.deleteSession(sessionId);

        // Assert
        verify(chatMessageRepository).deleteByChatSessionId(sessionId);
        verify(chatSessionRepository).deleteById(sessionId);
    }

    @Test
    @DisplayName("userExists - returns true")
    void userExists_ReturnsTrue() {
        // Pre-requisites
        given(userRepository.existsById(userId)).willReturn(true);

        // Invoke
        boolean exists = chatSessionService.userExists(userId);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("userExists - returns false")
    void userExists_ReturnsFalse() {
        // Pre-requisites
        given(userRepository.existsById(userId)).willReturn(false);

        // Invoke
        boolean exists = chatSessionService.userExists(userId);

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("renameSession - success")
    void renameSession_Success() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));
        given(chatSessionRepository.save(any(ChatSession.class))).willReturn(session);

        // Invoke
        ChatSession updated = chatSessionService.renameSession(sessionId, "New Name");

        // Assert
        assertThat(updated.getSessionName()).isEqualTo("New Name");
        verify(chatSessionRepository).save(session);
    }

    @Test
    @DisplayName("setFavourite - success")
    void setFavourite_Success() {
        // Pre-requisites
        given(chatSessionRepository.findById(sessionId)).willReturn(Optional.of(session));
        given(chatSessionRepository.save(any(ChatSession.class))).willReturn(session);

        // Invoke
        ChatSession updated = chatSessionService.setFavourite(sessionId, true);

        // Assert
        assertThat(updated.getIsFavourite()).isTrue();
        verify(chatSessionRepository).save(session);
    }

    @Test
    @DisplayName("getMessages - success")
    void getMessages_Success() {
        // Pre-requisites
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatMessage> mockPage = new PageImpl<>(List.of(new ChatMessage()));
        given(chatMessageRepository.findByChatSessionIdOrderByCreatedDateDesc(eq(sessionId), eq(pageable)))
                .willReturn(mockPage);

        // Act
        Page<ChatMessage> result = chatSessionService.getMessages(sessionId, pageable);

        // Assert
        assertThat(result).isEqualTo(mockPage);
    }
}

