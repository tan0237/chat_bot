package com.espay.service;

import com.alibaba.fastjson.JSONObject;
import com.espay.pojo.CustomServiceChat;

public interface CustomServiceChatService {
    /**
     * 保存用户和客服互发的消息
     * @param customServiceMessage 用户与客服交流的消息
     * @return
     */
    boolean saveCustomServiceChat(JSONObject customServiceMessage);

    /**
     * 保存用户对客服的评价
     * @param customServiceChat
     * @return 操作是否成功
     */
    boolean insertCustomEvaluate(CustomServiceChat customServiceChat);
}
