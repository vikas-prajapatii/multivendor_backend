package com.vikas.controller;

import com.vikas.ai.service.AiChatBotService;
import com.vikas.model.User;
import com.vikas.request.Prompt;
import com.vikas.response.ApiResponse;
import com.vikas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/ai", "/api/ai"})
@RequiredArgsConstructor
public class AiChatBotController {

    private final AiChatBotService aiChatBotService;
    private final UserService userService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse> chat(
            @RequestBody(required = false) Prompt prompt,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String jwt
    ) {
        Long resolvedUserId = userId;
        if (resolvedUserId == null && jwt != null && !jwt.trim().isEmpty() && !jwt.equals("Bearer null") && !jwt.equals("Bearer undefined")) {
            try {
                User user = userService.findUserByJwtToken(jwt);
                if (user != null) {
                    resolvedUserId = user.getId();
                }
            } catch (Exception ignored) {
            }
        }

        String promptText = (prompt != null && prompt.getPrompt() != null) ? prompt.getPrompt() : "";

        try {
            ApiResponse response = aiChatBotService.aiChatBot(promptText, productId, resolvedUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse fallback = new ApiResponse();
            fallback.setMessage("I am here to assist you! Feel free to ask about available sarees, product stock, your cart, or order updates.");
            return ResponseEntity.ok(fallback);
        }
    }
}
