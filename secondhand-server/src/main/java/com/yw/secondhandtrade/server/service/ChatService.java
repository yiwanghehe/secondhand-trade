package com.yw.secondhandtrade.server.service;

import com.yw.secondhandtrade.pojo.entity.ChatMessage;
import java.util.List;

public interface ChatService {
    /**
     * 保存聊天消息
     * @param chatMessage
     */
    void saveMessage(ChatMessage chatMessage);

    /**
     * 获取与指定用户的聊天记录
     * @param toUserId
     * @return
     */
    List<ChatMessage> getChatHistory(Long toUserId);
}
