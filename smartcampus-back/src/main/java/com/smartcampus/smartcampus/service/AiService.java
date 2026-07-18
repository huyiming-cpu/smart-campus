package com.smartcampus.smartcampus.service;

import com.alibaba.fastjson.JSONObject;
import com.smartcampus.smartcampus.entity.AiConversationHistory;
import com.smartcampus.smartcampus.entity.AiKnowledgeBase;
import com.smartcampus.smartcampus.repository.AiConversationHistoryRepository;
import com.smartcampus.smartcampus.repository.AiKnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiKnowledgeBaseRepository kbRepo;
    private final AiConversationHistoryRepository historyRepo;

    // DeepSeek API
    @org.springframework.beans.factory.annotation.Value("${deepseek.api-key:sk-your-key-here}")
    private String deepseekApiKey;
    private static final String DEEPSEEK_URL = "https://api.deepseek.com/v1/chat/completions";

    private static final String SYSTEM_PROMPT = """
        你是「智慧校园服务平台」的AI智能助手，名字叫"小智"。
        你的主要职责是帮助师生解答校园服务相关问题（如功能入口、操作流程等）。
        如果用户问校园以外的生活问题，也可以简单友好地回答，但要简短，并引导回校园服务话题。
        回答风格：简洁、口语化、有温度，适当使用emoji。

        当前用户提问：""";

    /**
     * AI对话 - 调用 DeepSeek 大模型 (OpenAI兼容格式)
     */
    public String chat(String question, Long userId, String sessionId) {
        String aiReply;
        try {
            RestTemplate restTemplate = new RestTemplate();

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + deepseekApiKey);
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
            messages.add(Map.of("role", "user", "content", question));

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("messages", messages);
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);

            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(body, headers);
            org.springframework.http.ResponseEntity<JSONObject> response =
                    restTemplate.postForEntity(DEEPSEEK_URL, request, JSONObject.class);

            JSONObject resp = response.getBody();
            if (resp == null) {
                aiReply = "小智暂时无法回复，请稍后再试~ 😅";
            } else {
                // OpenAI format: choices[0].message.content
                var choices = resp.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    aiReply = choices.getJSONObject(0).getJSONObject("message").getString("content");
                } else {
                    aiReply = "小智正在学习中，请换个方式问我吧~ 📚";
                }
                if (aiReply == null || aiReply.isEmpty()) {
                    aiReply = "小智正在学习中，请换个方式问我吧~ 📚";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            aiReply = "小智的网络有点问题，请稍后再试~ 🔄";
        }

        // 保存对话历史
        AiConversationHistory history = new AiConversationHistory();
        history.setUserId(userId);
        history.setSessionId(sessionId);
        history.setQuestion(question);
        history.setAnswer(aiReply);
        historyRepo.save(history);

        return aiReply;
    }

    /** 获取对话历史 */
    public List<AiConversationHistory> getChatHistory(Long userId, String sessionId) {
        List<AiConversationHistory> list = historyRepo.findByUserIdOrderByCreatedTimeDesc(userId);
        java.util.Collections.reverse(list);
        return list;
    }

    /** 管理员管理知识库（保留，可用于手动补充FAQ） */
    public List<AiKnowledgeBase> listKnowledgeBase() {
        return kbRepo.findAll();
    }

    @Transactional
    public AiKnowledgeBase saveKnowledge(AiKnowledgeBase kb) {
        return kbRepo.save(kb);
    }

    @Transactional
    public void deleteKnowledge(Long id) {
        kbRepo.deleteById(id);
    }
}
