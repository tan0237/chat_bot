package com.espay.controller;

import com.alibaba.fastjson.JSONObject;
import com.espay.service.RobotCustomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/robot")
public class RobotSubmitController {
    private Logger logger = LoggerFactory.getLogger(RobotSubmitController.class);
    @Autowired
    private RobotCustomService robotCustomService;

    /**
     * 智能客服引擎独立，建立接口从智能客服引擎拿到数据，然后处理后返回智能客服的应答
     *
     * @param questionJson 接收问题json
     * @param session      接收session
     * @return
     */
    @RequestMapping(value = "submit_form", produces = "text/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String getMessage(@RequestBody JSONObject questionJson, HttpSession session) {
        if (questionJson == null) {
            return "";
        }
        JSONObject answer = null;
        try {
           /*
              调用接口去调用智能客服引擎拿到应答数据
            */
            answer = robotCustomService.getRobotAnswer(questionJson);
        } catch (Exception e) {
            logger.error("机器客服引擎出错：", e);
            answer.put("answer", questionJson.getString("info"));
            answer.put("question", "网络异常，请稍后再试");
        }
        try {
           /*
              记录用户与机器客服的对话
            */
            robotCustomService.insertMessage(session, questionJson, answer);
        } catch (Exception e) {
            logger.error("mongo数据库存储出错", e);
        }
        return answer.toJSONString();
    }
}
