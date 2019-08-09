package com.espay.dao;

import com.espay.pojo.CustomServiceChat;

import java.util.Date;
import java.util.List;

public interface CustomServiceChatDao {
    /**
     * 添加一条用户与客服聊天记录
     * @param customServiceChat 用户与客服聊天的记录
     * @return 操作是否成功
     */
    boolean insertCustomServiceChat(CustomServiceChat customServiceChat);

    /**
     * 客服和用户聊天互相发送的消息添加到用户与客服的聊天记录中
     * @param customServiceChat 用户与客服聊天的记录
     * @return 操作是否成功
     */
    boolean upSetCustomServiceChat(CustomServiceChat customServiceChat);

    /**
     * 通过用户Id和客服Id找到原来的记录Id
     * @param customServiceChat 用户与客服聊天的记录
     * @return 记录Id
     */
    String findCustomServiceChatByCSId(CustomServiceChat customServiceChat);

    /**
     * 找到该时间段的人工客服的聊天记录
     * @param createTimeStart 开始时间
     * @param createTimeEnd 结束时间
     * @return 聊天记录列表
     */
    List<CustomServiceChat> findCustomServiceChatByCreateTime(Date createTimeStart, Date createTimeEnd);

    /**
     * 通过id保存用户对人工客服的评价
     * @param customServiceChat 包含记录id，用户评价的记录对象
     * @return 操作是否成功
     */
    boolean insertCustomEvaluateByCSID(CustomServiceChat customServiceChat);
}
