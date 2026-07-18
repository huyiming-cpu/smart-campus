package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.entity.AiConversationHistory;
import com.smartcampus.smartcampus.entity.AiKnowledgeBase;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final JwtUtil jwtUtil;

    /** AI智能对话 - 百度千帆大模型驱动 */
    @PostMapping("/ai/chat")
    public Result<String> chat(@RequestHeader("Authorization") String token,
                                @RequestBody Map<String, String> body) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        String reply = aiService.chat(
                body.get("question"), userId,
                body.getOrDefault("sessionId", "default"));
        return Result.success(reply);
    }

    /** 获取对话历史 */
    @GetMapping("/ai/history")
    public Result<List<AiConversationHistory>> getChatHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String sessionId) {
        return Result.success(aiService.getChatHistory(
                jwtUtil.getUserIdFromToken(token), sessionId));
    }

    /** 管理员管理知识库（补充FAQ） */
    @GetMapping("/admin/ai/knowledge")
    public Result<List<AiKnowledgeBase>> listKnowledgeBase() {
        return Result.success(aiService.listKnowledgeBase());
    }

    @PostMapping("/admin/ai/knowledge")
    public Result<AiKnowledgeBase> saveKnowledge(@RequestBody AiKnowledgeBase kb) {
        return Result.success(aiService.saveKnowledge(kb));
    }

    @DeleteMapping("/admin/ai/knowledge/{id}")
    public Result<Void> deleteKnowledge(@PathVariable Long id) {
        aiService.deleteKnowledge(id);
        return Result.success();
    }
}
