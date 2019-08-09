package com.espay.controller;

import com.alibaba.fastjson.JSONObject;
import com.espay.pojo.CustomServiceChat;
import com.espay.pojo.ResultInfo;
import com.espay.pojo.Staff;
import com.espay.service.CustomServiceChatService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Controller
@RequestMapping("/staff")
public class StaffController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CustomServiceChatService customServiceChatService;

    private static Logger logger = Logger.getLogger(StaffController.class);

    /**
     * 人工客服上线
     * @param paraMap
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "online", method = RequestMethod.POST)
    public ResultInfo online(@RequestBody Map<String, Object> paraMap){
       ResultInfo resultInfo = new ResultInfo();
        try{
            Set<String> collectionNames= mongoTemplate.getCollectionNames();
            if(!collectionNames.contains("staff")){
                Index   index=new Index();
                index.on("createAt",Sort.Direction.ASC);
                index.expire(30,TimeUnit.SECONDS);
                IndexOperations staffIndexOperations= mongoTemplate.indexOps("staff");
                staffIndexOperations.ensureIndex(index);
            }
            String username = (String) paraMap.get("username");
            Integer brandId = (Integer) paraMap.get("brandId");
            List<Staff> list = mongoTemplate.find(new Query(where("username").is(username)), Staff.class);
            if(list.size() == 0){
                mongoTemplate.insert(new Staff(username, brandId, new Date()));
            }
            resultInfo.setStatusCode(200);
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setStatusCode(500);
            logger.error(e.getMessage());
        }
        return resultInfo;
    }

    /**
     * 人工客服上线
     * @param paraMap
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "offline", method = RequestMethod.POST)
    public ResultInfo offline(@RequestBody Map<String, Object> paraMap){
        ResultInfo resultInfo = new ResultInfo();
        try{
            String username = (String) paraMap.get("username");
            mongoTemplate.remove(new Query(where("username").is(username)), Staff.class);
            resultInfo.setStatusCode(200);
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setStatusCode(500);
            logger.error(e.getMessage());
        }
        return resultInfo;
    }

    /**
     * 获取在线客服
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "onlineList", method = RequestMethod.GET)
    public ResultInfo onlineCount(){
        ResultInfo resultInfo = new ResultInfo();
        try {
            List<Staff> list = mongoTemplate.findAll(Staff.class);
            resultInfo.setData(list);
            resultInfo.setStatusCode(200);
        }catch (Exception e){
            resultInfo.setStatusCode(500);
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return resultInfo;
    }

    /**
     * 用户对客服的评价提交
     * @param paraJson  参数json
     * @return 返回值json
     */
    @RequestMapping(value = "submitEvaluate",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultInfo submitEvaluate(@RequestBody JSONObject paraJson){
        ResultInfo resultInfo=new ResultInfo();
        String customerId=null;
        String serviceId=null;
        Integer satisfaction=null;
        try{
            customerId=paraJson.getString("customerId");
            serviceId=paraJson.getString("serviceId");
            satisfaction=paraJson.getInteger("satisfaction");
            if(customerId==null||serviceId==null||satisfaction==null){
                throw new NullPointerException();
            }
        }catch (Exception e){
            logger.error("参数错误：",e);
            resultInfo.setStatusCode(500);
            resultInfo.setMessage("参数错误");
            return resultInfo;
        }
        CustomServiceChat customServiceChat=new CustomServiceChat();
        customServiceChat.setCustomerId(customerId);
        customServiceChat.setServiceId(serviceId);
        customServiceChat.setSatisfaction(satisfaction);
        boolean statusBool= customServiceChatService.insertCustomEvaluate(customServiceChat);
        if(statusBool){
            resultInfo.setMessage("评价保存成功");
            resultInfo.setStatusCode(200);
            return resultInfo;
        }
        resultInfo.setMessage("评价保存失败");
        resultInfo.setStatusCode(500);
        return resultInfo;
    }

    @ResponseBody
    @RequestMapping(value = "chatList", method = RequestMethod.POST)
    public ResultInfo getChatList(@RequestBody JSONObject paramJson){
        ResultInfo resultInfo = new ResultInfo();
        try{
            String username = paramJson.getString("username");
            Integer pageSize = paramJson.getInteger("pageSize");  //页数大小
            Integer pageIndex = paramJson.getInteger("pageIndex");  //页码
            Query query = new Query();

            //匹配客服名
            if(StringUtils.hasText(username)){
                query.addCriteria((Criteria.where("serviceName").is(username)));
            }

            //获取总数
            Integer count = mongoTemplate.find(query, CustomServiceChat.class).size();

            //分页
            int skip = (pageIndex -1) * pageSize;
            query.skip(skip);// skip相当于从那条记录开始
            query.limit(pageSize);// 从skip开始,取多少条记录

            //排序
            query.with(new Sort(Sort.Direction.DESC, "recordTime"));

            List<CustomServiceChat> list = mongoTemplate.find(query, CustomServiceChat.class);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("rows", count);
            resultMap.put("data", list);

            resultInfo.setMessage("获取会话列表成功");
            resultInfo.setStatusCode(200);
            resultInfo.setData(resultMap);
        }catch (Exception e){
            resultInfo.setMessage("获取数据失败");
            resultInfo.setStatusCode(500);
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return resultInfo;
    }
}
