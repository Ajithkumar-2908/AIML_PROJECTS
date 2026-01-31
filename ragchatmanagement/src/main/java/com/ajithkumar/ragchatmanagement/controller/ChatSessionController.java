package com.ajithkumar.ragchatmanagement.controller;

import com.ajithkumar.ragchatmanagement.dto.*;
import com.ajithkumar.ragchatmanagement.entity.ChatMessage;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.service.ChatMessageService;
import com.ajithkumar.ragchatmanagement.service.ChatSessionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for chat session operations.
 * Provides blocking (non-reactive) endpoints using Spring MVC.
 */
//@Slf4j
@RestController
@RequestMapping("/api/v1/chat-sessions")
//@RequiredArgsConstructor
public class ChatSessionController {

    private static final Logger log = LoggerFactory.getLogger(ChatSessionController.class);

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;

//    public ChatSessionController(ChatSessionService chatSessionService) {
//        this.chatSessionService = chatSessionService;
//    }

    /**
     * Create a new chat session for a user.
     *
     * @param request the create chat session request
     * @return response entity with created chat session
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChatSession> createChatSession(
            @Valid @RequestBody CreateChatSessionRequest request) {

        log.info("Received request to create chat session for user: {}", request.getUserId());
        log.debug("Session name: {}", request.getSessionName());

        ChatSession response = chatSessionService.createChatSession(request);

        log.info("Chat session created successfully: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve a chat session by ID.
     *
     * @param sessionId the ID of the chat session
     * @return response entity with chat session details
     */
    @GetMapping(
            value = "/{sessionId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChatSession> getChatSession(
            @PathVariable UUID sessionId) {

        log.info("Received request to fetch chat session: {}", sessionId);

        ChatSession response = chatSessionService.getChatSessionById(sessionId);

        log.info("Chat session retrieved: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    // Rename session
    @PatchMapping("/{sessionId}/rename")
    public ResponseEntity<ChatSession> renameSession(
            @PathVariable UUID sessionId,
            @RequestBody Map<String, String> request) {

        if (request.containsKey("name") == false || request.get("name").isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be empty");
        }

        ChatSession chatSession = chatSessionService.renameSession(sessionId, request.get("name"));
        return ResponseEntity.status(HttpStatus.OK).body(chatSession);

    }

    // Mark / Unmark favourite
    @PatchMapping("/{sessionId}/updateFavourite")
    public ResponseEntity<ChatSession> markFavourite(
            @PathVariable UUID sessionId,
            @RequestBody Map<String, Boolean> request) {

        if (!request.containsKey("favourite") || request.get("favourite") == null) {
            throw new IllegalArgumentException("Favorite status must be provided");
        }

        ChatSession chatSession = chatSessionService.setFavourite(sessionId, request.get("favourite"));

        return ResponseEntity.status(HttpStatus.OK).body(chatSession);


    }

    // Delete session and messages
    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable UUID sessionId) {
        chatSessionService.deleteSession(sessionId);
    }

    // Create a message in a session
    @PostMapping("/{sessionId}/messages")
    public ResponseEntity<ChatMessageResponse> createMessage(
            @PathVariable UUID sessionId,
            @Valid @RequestBody CreateChatMessageRequest request) {

        ChatMessageResponse chatMessageResponse =  chatMessageService.createMessage(sessionId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessageResponse);

    }

    // Retrieve message history
    @GetMapping("/{sessionId}/messages")
    public Page<ChatMessage> getMessages(
            @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return chatSessionService.getMessages(sessionId, PageRequest.of(page, size));
    }

    // Detele a message in a session.
    @DeleteMapping("/{sessionId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(
            @PathVariable UUID sessionId,
            @PathVariable UUID messageId) {

        chatMessageService.deleteMessage(sessionId, messageId);
    }

}
