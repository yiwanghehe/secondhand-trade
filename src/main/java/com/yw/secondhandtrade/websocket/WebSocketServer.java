package com.yw.secondhandtrade.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.config.GetHttpSessionConfigurator;
import com.yw.secondhandtrade.pojo.dto.ChatMessageDTO;
import com.yw.secondhandtrade.pojo.entity.ChatMessage;
import com.yw.secondhandtrade.service.ChatService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
// 指定Configurator, 拦截握手阶段时的BaseContext得到userId并存入会话域session，用于鉴权当前用户是否能进入/ws/chat/{userId}
@ServerEndpoint(value = "/ws/chat/{userId}", configurator = GetHttpSessionConfigurator.class)
public class WebSocketServer {

    // 使用线程安全的Map来存储每个客户端对应的Session对象
    private static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();
    // Jackson的ObjectMapper，用于JSON序列化和反序列化
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
        // 从握手阶段传递过来的 userProperties 中获取登录用户的ID
        Long loggedInUserId = (Long) session.getUserProperties().get(GetHttpSessionConfigurator.LOGGED_IN_USER_ID);

        // 核心验证逻辑
        if (loggedInUserId == null || !loggedInUserId.equals(userId)) {
            log.warn("WebSocket 连接验证失败：路径用户ID ({}) 与登录用户ID ({}) 不匹配。即将关闭连接。", userId, loggedInUserId);
            try {
                // 创建一个关闭原因
                CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "未经授权的访问");
                session.close(closeReason);
            } catch (IOException e) {
                log.error("关闭未授权的WebSocket连接时发生错误: ", e);
            }
            return;
        }
        sessionMap.put(loggedInUserId, session);
        log.info("用户ID: {} 的WebSocket连接已建立, 当前在线人数: {}", loggedInUserId, sessionMap.size());
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

    /**
     * 向指定用户发送消息
     * @param userId  目标用户的ID
     * @param message 要发送的消息内容
     */
    public static void sendMessageToUser(Long userId, String message) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("通过WebSocket向用户ID: {} 发送了一条消息: {}", userId, message);
            } catch (IOException e) {
                log.error("通过WebSocket向用户ID: {} 发送消息失败", userId, e);
            }
        } else {
            log.warn("无法向用户ID: {} 发送WebSocket消息，因为用户不在线。", userId);
            // TODO 在此可以加入离线消息推送逻辑，例如使用第三方推送服务
        }
    }
}

