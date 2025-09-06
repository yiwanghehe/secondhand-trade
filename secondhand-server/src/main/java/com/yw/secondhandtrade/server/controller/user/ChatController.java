package com.yw.secondhandtrade.server.controller.user;

import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.entity.ChatMessage;
import com.yw.secondhandtrade.server.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/chat")
@Tag(name = "【用户端】聊天接口")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history/{toUserId}")
    @Operation(summary = "获取聊天记录")
    public Result<List<ChatMessage>> getChatHistory(@PathVariable Long toUserId) {
        Long currentUserId = BaseContext.getId();
        log.info("用户 {} 正在获取与用户 {} 的聊天记录", currentUserId, toUserId);
        List<ChatMessage> history = chatService.getChatHistory(toUserId);
        return Result.success(history);
    }
}
