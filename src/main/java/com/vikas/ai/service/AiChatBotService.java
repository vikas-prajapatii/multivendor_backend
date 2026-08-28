package com.vikas.ai.service;

import com.vikas.exception.ProductException;
import com.vikas.response.ApiResponse;

public interface AiChatBotService {
    ApiResponse aiChatBot(String prompt, Long productId, Long userId) throws ProductException;

}
