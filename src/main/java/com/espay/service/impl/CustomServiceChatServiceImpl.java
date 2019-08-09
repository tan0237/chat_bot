package com.espay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.espay.dao.CustomServiceChatDao;
import com.espay.pojo.ChatRecord;
import com.espay.pojo.CustomServiceChat;
import com.espay.service.CustomServiceChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class CustomServiceChatServiceImpl implements CustomServiceChatService {
    @Autowired
    @Qualifier(value = "CustomServiceChatDaoImpl")
    private CustomServiceChatDao customServiceChatsDao;
    /**
     * 保存用户和客服互发的消息
     *
     * @param customServiceMessage 用户与客服交流的消息
     * @return
     */
    @Override
    public boolean saveCustomServiceChat(JSONObject customServiceMessage) {
        /*
         * 0代表用户
         * 1代表客服
         * false 代表用户
         * true 代表客服
         */
        Map<String, Object> msgMap = new HashMap<>();
        CustomServiceChat customServiceChat =new CustomServiceChat();
        boolean message=false;
        try {
            if ("0".equals(customServiceMessage.get("type").toString())) {
                message=false;
                msgMap.put("customerId", customServiceMessage.get("from"));
                msgMap.put("serviceId", customServiceMessage.get("to"));
            } else {
                message=true;
                msgMap.put("customerId", customServiceMessage.get("to"));
                msgMap.put("serviceId", customServiceMessage.get("from"));
            }
            customServiceChat.setCustomerId(msgMap.get("customerId").toString());
            customServiceChat.setServiceId(msgMap.get("serviceId").toString());
        } catch (Exception e) {
            return false;
        }
       String customServiceChatsId= customServiceChatsDao.findCustomServiceChatByCSId(customServiceChat);
        ChatRecord chatRecord=new ChatRecord();
        if(message) {
            chatRecord.setFrom(customServiceMessage.getString("serviceName"));
            chatRecord.setTo(customServiceMessage.getString("customName"));
        }else {
            chatRecord.setFrom(customServiceMessage.getString("customName"));
            chatRecord.setTo(customServiceMessage.getString("serviceName"));
        }
        chatRecord.setMessage(customServiceMessage.getString("message"));
        chatRecord.setSendTime(new Date());
        List<ChatRecord> chatRecordList=new ArrayList<>();
        chatRecordList.add(chatRecord);
        customServiceChat.setChatRecordList(chatRecordList);
        if (customServiceChatsId!=null){
            customServiceChat.setId(customServiceChatsId);
            return  customServiceChatsDao.upSetCustomServiceChat(customServiceChat);
        }
        customServiceChat.setServiceName(customServiceMessage.getString("serviceName"));
        customServiceChat.setCustomerName(customServiceMessage.getString("customName"));
        customServiceChat.setCustomIp(customServiceMessage.getString("customIp"));
        customServiceChat.setRecordTime(new Date());
        return customServiceChatsDao.insertCustomServiceChat(customServiceChat);
    }

    /**
     * 保存用户对客服的评价
     *
     * @param customServiceChat
     * @return
     */
    @Override
    public boolean insertCustomEvaluate(CustomServiceChat customServiceChat) {
        String customServiceChatsId= customServiceChatsDao.findCustomServiceChatByCSId(customServiceChat);
        if(!StringUtils.hasText(customServiceChatsId)){
            return false;
        }
        customServiceChat.setId(customServiceChatsId);
       return   customServiceChatsDao.insertCustomEvaluateByCSID(customServiceChat);
    }
}
