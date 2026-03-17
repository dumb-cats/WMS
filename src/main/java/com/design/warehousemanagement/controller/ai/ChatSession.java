package com.design.warehousemanagement.controller.ai;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSession {

    /**
     * 使用 Map 存储每个用户的聊天历史
      */
    private final Map<String, List<Message>> userHistoryMap = new ConcurrentHashMap<>();

    /**
     * 获取当前用户的 Prompt（包含历史）
     */
    public Prompt getPrompt(String userId, String message) {
        List<Message> history = userHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>());
        history.add(new UserMessage(message));
        return new Prompt(history);
    }

    /**
     * 保存模型的回答到历史中
     */
    public void addResponse(String userId, String responseContent) {
        List<Message> history = userHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>());
        history.add(new AssistantMessage(responseContent));
    }

    /**
     * 获取完整的历史对话记录
     */
    public List<Message> getHistory(String userId) {
        return userHistoryMap.getOrDefault(userId, new ArrayList<>());
    }

    /**
     * 清空某个用户的聊天历史
     */
    public void clearHistory(String userId) {
        List<Message> history = userHistoryMap.get(userId);
        if (history != null) {
            history.clear();
        }
    }
}