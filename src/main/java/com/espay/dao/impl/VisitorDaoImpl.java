package com.espay.dao.impl;

import com.espay.dao.VisitorDao;
import com.espay.pojo.VisitorEntity;
import com.mongodb.MongoException;
import com.mongodb.MongoExecutionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository(value = "visitorDaoImpl")
public class VisitorDaoImpl implements VisitorDao {
    private Logger logger=LoggerFactory.getLogger(VisitorDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 创建对象
     * @param visitor
     */
    @Override
    public void saveVisitor(VisitorEntity visitor) {
        mongoTemplate.save(visitor);
    }

    /**
     * 根据用户名查询对象
     * @param username
     * @return 返回查找到的对象
     */
    @Override
    public VisitorEntity findVisitorByUsername(String username) {
        Query query=new Query(Criteria.where("username").is(username));
        query.with(Sort.by(Sort.Direction.DESC,"create_time"));
        List<VisitorEntity> visitors =  mongoTemplate.find(query , VisitorEntity.class);
        VisitorEntity visitor=null;
        if(visitors.size()>0){
            visitor=visitors.get(0);
        }
        return visitor;
    }

    /**
     * 更新对象
     * @param visitor
     */
    @Override
    public void updateVisitor(VisitorEntity visitor) {
        Query query=new Query(Criteria.where("id").is(visitor.getId()));
        Update update= new Update();
        update.addToSet("question",visitor.getQuestion().get(0));
        mongoTemplate.upsert(query,update,VisitorEntity.class);
    }

    /**
     * 找到在该时间段创建的智能引擎的对话
     *
     * @param createTimeStart 开始时间
     * @param createTimeEnd   结束时间
     * @return 返回找到记录列表
     */
    @Override
    public List<VisitorEntity> findVisitorsByCreateTime(Date createTimeStart, Date createTimeEnd) {
        List<VisitorEntity> visitorEntityList=null;
        try {
            Query query = new Query(Criteria.where("create_time").gte(createTimeStart).lt(createTimeEnd));
            visitorEntityList = mongoTemplate.find(query, VisitorEntity.class);
        }catch (Exception e){
            logger.error("mongo数据库出错：",e);
        }
        return visitorEntityList;
    }

    /**
     * 查询所有机器客服和用户的记录
     *
     * @return 记录列表
     */
    @Override
    public List<VisitorEntity> findAllVisitors() {
        List<VisitorEntity> visitorEntityList=null;
        try {
            visitorEntityList = mongoTemplate.findAll(VisitorEntity.class);
        }catch (Exception e ){
           logger.error("Mongo数据库出错:",e);
        }
        return visitorEntityList;
    }


}
