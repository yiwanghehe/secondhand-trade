package com.yw.secondhandtrade.mapper;

import com.yw.secondhandtrade.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    /**
     * 插入聊天消息
     * @param chatMessage
     */
    void insert(ChatMessage chatMessage);

    /**
     * 查询两个用户之间的聊天记录
     * @param userId1
     * @param userId2
     * @return
     */
    List<ChatMessage> getChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 更新消息的读取状态
     * @param fromUserId 消息的发送方
     * @param toUserId 消息的接收方 (即当前登录用户)
     */
    void updateReadStatus(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);
}
