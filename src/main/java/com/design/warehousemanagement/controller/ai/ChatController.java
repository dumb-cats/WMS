//package com.design.warehousemanagement.controller.ai;
//
//import com.design.warehousemanagement.common.Result;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.chat.messages.Message;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/ai")
//@RequiredArgsConstructor
//@Tag(name = "AI 对话")
//public class ChatController {
//
//    private final ChatModel chatModel;
//    private final ChatSession chatSession;
//
//    @GetMapping("/stream")
//    @Operation(summary = "流式输出对话接口")
//    public SseEmitter stream(
//            @RequestParam("userId") String userId,
//            @RequestParam("message") String message) throws IOException {
//        SseEmitter emitter = new SseEmitter(60_000L);
//
//        try {
//            Prompt prompt = chatSession.getPrompt(userId,message);
//            chatModel.stream(prompt).subscribe(response -> {
//                try {
//                    emitter.send(response.getResult().getOutput().getContent());
//                } catch (IOException e) {
//                    emitter.complete();
//                }
//            });
//        } catch (Exception e) {
//            emitter.completeWithError(e);
//        }
//
//        return emitter;
//    }
//
//    @GetMapping("/clear")
//    @Operation(summary = "清空聊天历史")
//    public Result clearHistory(@RequestParam("userId") String userId) {
//        chatSession.clearHistory(userId);
//        return Result.success("聊天历史已清空");
//    }
//
//    @GetMapping("/chat")
//    @Operation(summary = "同步返回聊天内容并记录历史")
//    public Result chat(
//            @RequestParam("message") String message,
//            @RequestParam("userId") String userId) {
//
//        try {
//            Prompt prompt = chatSession.getPrompt(userId, message);
//            String response = chatModel.call(prompt).getResult().getOutput().getContent();
//
//            // 将 AI 回答加入历史
//            chatSession.addResponse(userId, response);
//
//            // 返回成功结果
//            return Result.success(Map.of("content", response));
//        } catch (Exception e) {
//            return Result.error("AI 调用失败：" + e.getMessage());
//        }
//    }
//
//    @GetMapping("/history")
//    @Operation(summary = "获取指定用户的聊天历史")
//    public Result getHistory(@RequestParam("userId") String userId) {
//        List<Message> history = chatSession.getHistory(userId);
//
//        // 格式化为前端友好的结构
//        List<Map<String, String>> formattedHistory = new ArrayList<>();
//        for (Message msg : history) {
//            formattedHistory.add(Map.of(
//                    "role", msg.getMessageType().name(),
//                    "content", msg.getContent()
//            ));
//        }
//
//        return Result.success(Map.of("history", formattedHistory));
//    }
//}