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
 */

@RestController
@RequestMapping("/api/v1/chat-sessions")
public class ChatSessionController {

    private static final Logger log = LoggerFactory.getLogger(ChatSessionController.class);

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;


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

    /**
     * Rename a chat session.
     *
     * @param sessionId the ID of the chat session
     * @param request   the rename request containing the new name
     * @return response entity with chat session details
     */
    @PatchMapping("/{sessionId}/rename")
    public ResponseEntity<ChatSession> renameSession(
            @PathVariable UUID sessionId,
            @RequestBody Map<String, String> request) {

        if (request.containsKey("name") == false || request.get("name").isEmpty()) {
            log.error("Invalid session name provided for renaming session: {}", sessionId);
            throw new IllegalArgumentException("Session name cannot be empty");
        }

        ChatSession chatSession = chatSessionService.renameSession(sessionId, request.get("name"));
        log.info("Chat session renamed successfully: {}", chatSession.getId());
        return ResponseEntity.status(HttpStatus.OK).body(chatSession);

    }

    /**
     * Mark or unmark a chat session as favourite.
     *
     * @param sessionId the ID of the chat session
     * @param request   the request containing the favourite status
     * @return response entity with chat session details
     */
    @PatchMapping("/{sessionId}/updateFavourite")
    public ResponseEntity<ChatSession> markFavourite(
            @PathVariable UUID sessionId,
            @RequestBody Map<String, Boolean> request) {

        if (!request.containsKey("favourite") || request.get("favourite") == null) {
            log.error("Favorite status not provided for session: {}", sessionId);
            throw new IllegalArgumentException("Favorite status must be provided");
        }

        ChatSession chatSession = chatSessionService.setFavourite(sessionId, request.get("favourite"));
        log.info("Chat session favourite status updated successfully: {}", chatSession.getId());
        return ResponseEntity.status(HttpStatus.OK).body(chatSession);


    }

    /**
     * Delete a chat session.
     *
     * @param sessionId the ID of the chat session
     */
    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable UUID sessionId) {
        chatSessionService.deleteSession(sessionId);
    }

    /**
     * Create a new message in a chat session.
     *
     * @param sessionId the ID of the chat session
     * @param request   the create chat message request
     * @return response entity with created chat message
     */
    @PostMapping("/{sessionId}/messages")
    public ResponseEntity<ChatMessageResponse> createMessage(
            @PathVariable UUID sessionId,
            @Valid @RequestBody CreateChatMessageRequest request) {

        log.info("Received request to create message in session: {}", sessionId);
        ChatMessageResponse chatMessageResponse =  chatMessageService.createMessage(sessionId, request);

        log.info("Chat message created successfully in session: {}", sessionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessageResponse);

    }

    /**
     * Get messages in a chat session with pagination.
     *
     * @param sessionId the ID of the chat session
     * @param page      the page number (default is 0)
     * @return response entity with paginated chat messages
     */
    @GetMapping("/{sessionId}/messages")
    public Page<ChatMessage> getMessages(
            @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request to fetch messages for session: {}", sessionId);
        return chatSessionService.getMessages(sessionId, PageRequest.of(page, size));

    }

    /**
     * Delete a chat message in a session.
     *
     * @param sessionId the ID of the chat session
     * @param messageId the ID of the chat message
     */
    @DeleteMapping("/{sessionId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(
            @PathVariable UUID sessionId,
            @PathVariable UUID messageId) {

        log.info("Received request to delete message: {} in session: {}", messageId, sessionId);
        chatMessageService.deleteMessage(sessionId, messageId);
        log.info("Chat message deleted successfully: {}", messageId);

    }

}
