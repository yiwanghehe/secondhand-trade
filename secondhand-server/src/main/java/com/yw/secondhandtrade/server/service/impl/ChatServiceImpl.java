package com.yw.secondhandtrade.server.service.impl;

import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.pojo.entity.ChatMessage;
import com.yw.secondhandtrade.server.mapper.ChatMessageMapper;
import com.yw.secondhandtrade.server.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public void saveMessage(ChatMessage chatMessage) {
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    @Transactional
    public List<ChatMessage> getChatHistory(Long toUserId) {
        Long fromUserId = BaseContext.getId();

        // 先获取两人之间的历史记录
        List<ChatMessage> chatHistory = chatMessageMapper.getChatHistory(fromUserId, toUserId);

        // 将对方发给我的未读消息标记为已读
        chatMessageMapper.updateReadStatus(toUserId, fromUserId);

        return chatHistory;
    }
}
