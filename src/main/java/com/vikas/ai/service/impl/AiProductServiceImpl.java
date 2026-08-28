package com.vikas.ai.service.impl;

import com.vikas.ai.service.AiProductService;
import com.vikas.response.ApiResponse;
import com.vikas.response.FunctionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiProductServiceImpl implements AiProductService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public String simpleChat(String prompt) {
        return "";
    }
}
