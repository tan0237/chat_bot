package com.espay.service;


import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpSession;

/**
 * 调用机器客服引擎接口
 */
public interface RobotCustomService {
    /**
     *  拿到机器客服返回的信息
     */
    JSONObject getRobotAnswer(JSONObject message);

    /**
     * 记录用户与机器客服的对话消息
     * @param session
     * @param questionJson
     * @param answerJson
     * @return
     */
    boolean insertMessage(HttpSession session, JSONObject questionJson, JSONObject answerJson);
}
