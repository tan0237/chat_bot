package com.espay.controller;

import com.alibaba.fastjson.JSONObject;
import com.espay.pojo.ResultInfo;
import com.espay.service.IRepositoryService;
import com.espay.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/statistic")
public class StatisticController {
    private Logger logger=LoggerFactory.getLogger(StatisticController.class);
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private IRepositoryService repositoryService;


    /**
     * 会话量:机器客服，人工客服，总量统计，消息量：机器客服，人工客服，消息总量统计
     * @return 返回json数据e
     */
    @RequestMapping(value = "allstate",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String,Object> getAllState(){
       Map<String,Object> allStateMap= statisticsService.getAllState();
       return allStateMap;
    }

    /**
     * 命中率统计，统计机器客服能够给出答案的统计百分比
     * @return 返回json数据
     */
    @RequestMapping(value = "hitrate",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String,Object> getHitRate(){
        Map<String,Object> hitRateMap=statisticsService.getHitRate();
        return hitRateMap;
    }

    /**
     * 分页查询未命中问题统计，统计机器客服不能回答的问题
     * @param jsonObject 参数接收json对象
     * @return 未命中问题分页数据
     */
    @RequestMapping(value = "missQuestions",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String,Object> getMissQuestions(@RequestBody JSONObject jsonObject){

        int pageIndex=1;
        int pageSize=0;
        try {
           pageIndex= jsonObject.getInteger("pageIndex");
           pageSize = jsonObject.getInteger("pageSize");
        }catch (NullPointerException e){
            logger.error("参数错误:",e);
        }
        Map<String,Object> missQuestionMap=statisticsService.getMissQuestions(pageIndex,pageSize);
        return missQuestionMap;
    }

    /**
     * 知识库知识总量统计
     * @return 结果JSON
     */
    @RequestMapping(value = "repositoryCount",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultInfo getRepositoryCount(){
        int repositoryCount=0;
        repositoryCount= repositoryService.getRepositoryCount(null,null);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("repositoryCount",repositoryCount);
        ResultInfo resultInfo=new ResultInfo();
        resultInfo.setMessage("请求成功");
        resultInfo.setStatusCode(200);
        resultInfo.setData(resultMap);
        return resultInfo;
    }
}
