package com.espay.dao.impl;

import com.espay.dao.CustomServiceChatDao;
import com.espay.pojo.CustomServiceChat;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository(value = "CustomServiceChatDaoImpl")
public class CustomServiceChatDaoImpl implements CustomServiceChatDao {
    private Logger logger=LoggerFactory.getLogger(CustomServiceChatDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加一条用户与客服聊天记录
     *
     * @param customServiceChat 用户与客服聊天的记录
     * @return 返回是否操作成功的Boolean值
     */
    @Override
    public boolean insertCustomServiceChat(CustomServiceChat customServiceChat) {
        try {
            mongoTemplate.insert(customServiceChat);
        }catch (Exception e){
           logger.error("用户聊天信息插入失败：",e);
           return false;
        }
        return true;
    }

    /**
     * 客服和用户聊天互相发送的消息添加到用户与客服的聊天记录中
     *
     * @param customServiceChat 用户与客服聊天的记录
     * @return 返回是否操作成功的boolean值
     */
    @Override
    public boolean upSetCustomServiceChat(CustomServiceChat customServiceChat) {
        Query query=new Query(Criteria.where("id").is(customServiceChat.getId()));
        Update update=new Update().addToSet("chatRecordList", customServiceChat.getChatRecordList().get(0));
        UpdateResult updateResult = mongoTemplate.upsert(query,update,CustomServiceChat.class);
        Long count= updateResult.getModifiedCount();
        if(count>0){
            return true;
        }
        return false;
    }

    /**
     * 通过用户名和客服名找到原来的记录
     *
     * @param customServiceChat 用户与客服聊天的记录
     * @return 返回找到聊天记录的id
     */
    @Override
    public String findCustomServiceChatByCSId(CustomServiceChat customServiceChat) {
        Query query=new Query(Criteria.where("customerId").is(customServiceChat.getCustomerId())
                .and("serviceId").is(customServiceChat.getServiceId()));
        CustomServiceChat csChats= mongoTemplate.findOne(query,CustomServiceChat.class);
        if(csChats==null){
            return null;
        }
        return csChats.getId();
    }

    /**
     * 找到该时间段的人工客服的聊天记录
     *
     * @param createTimeStart 开始时间
     * @param createTimeEnd   结束时间
     * @return 返回找到的聊天记录列表
     */
    @Override
    public List<CustomServiceChat> findCustomServiceChatByCreateTime(Date createTimeStart, Date createTimeEnd) {
        Query query=new Query(Criteria.where("recordTime").gte(createTimeStart).lt(createTimeEnd));
        List<CustomServiceChat> customServiceChatList =mongoTemplate.find(query,CustomServiceChat.class);
        return customServiceChatList;
    }

    /**
     * 通过id保存用户对人工客服的评价
     *
     * @param customServiceChat 包含记录id，用户评价的记录对象
     * @return 操作是否成功
     */
    @Override
    public boolean insertCustomEvaluateByCSID(CustomServiceChat customServiceChat) {
        Query query=new Query();
        query.addCriteria(Criteria.where("_id").is(customServiceChat.getId()));
        Update update=new Update();
        update.set("satisfaction",customServiceChat.getSatisfaction());
        try{
            mongoTemplate.upsert(query,update,CustomServiceChat.class);
        }catch (Exception e){
            logger.error("保存用户评价失败：",e);
            return false;
        }
        return true;
    }
}
