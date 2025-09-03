package com.yw.secondhandtrade.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.pojo.dto.ChatMessageDTO;
import com.yw.secondhandtrade.pojo.entity.ChatMessage;
import com.yw.secondhandtrade.service.ChatService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ServerEndpoint("/ws/chat/{userId}")
public class WebSocketServer {

    // 使用线程安全的Map来存储每个客户端对应的Session对象
    private static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();
    // Jackson的ObjectMapper，用于JSON序列化和反序列化
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // 关键改动：注入ChatService
    private static ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        WebSocketServer.chatService = chatService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        sessionMap.put(userId, session);
        log.info("用户ID: {} 的WebSocket连接已建立, 当前在线人数: {}", userId, sessionMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        sessionMap.remove(userId);
        log.info("用户ID: {} 的WebSocket连接已关闭, 当前在线人数: {}", userId, sessionMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long fromUserId) {
        log.info("收到来自用户ID: {} 的消息: {}", fromUserId, message);
        try {
            // 将JSON字符串消息解析为ChatMessageDTO对象
            ChatMessageDTO chatMessageDTO = objectMapper.readValue(message, ChatMessageDTO.class);
            ChatMessage chatMessage = new ChatMessage();
            BeanUtils.copyProperties(chatMessageDTO, chatMessage);

            Session toSession = sessionMap.get(chatMessage.getToUserId());
            chatMessage.setFromUserId(fromUserId);
            chatMessage.setSendTime(LocalDateTime.now());

            Integer readStatus = StatusConstant.UNREAD;
            if(toSession != null && toSession.isOpen()) readStatus = StatusConstant.READ;
            chatMessage.setReadStatus(readStatus);

            chatService.saveMessage(chatMessage);

            // 如果接收方在线，则转发消息
            if (toSession != null && toSession.isOpen()) {
                String messageToSend = objectMapper.writeValueAsString(chatMessageDTO);
                toSession.getBasicRemote().sendText(messageToSend);
                log.info("消息已成功转发给用户ID: {}", chatMessage.getToUserId());
            } else {
                log.warn("消息转发失败，用户ID: {} 不在线。消息已作为离线消息保存。", chatMessage.getToUserId());
            }
        } catch (IOException e) {
            log.error("处理WebSocket消息时发生错误: ", e);
        }
    }
}

