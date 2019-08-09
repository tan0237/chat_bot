package com.espay.controller;


import com.alibaba.fastjson.JSONObject;
import com.espay.config.Constant;
import com.espay.service.CustomServiceChatService;
import com.espay.service.impl.WebSocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


import javax.annotation.Resource;

@Controller
public class WebsocketController {

    @Resource
    WebSocketServiceImpl webSocketService;

    @Autowired
    CustomServiceChatService customServiceChatService;
    /**
     * 用户转人工，用户列表增加用户信息
     * @param customerJson
     * @return
     */
    @MessageMapping(Constant.FORETOSERVERPATH)//@MessageMapping和@RequestMapping功能类似，用于设置URL映射地址，浏览器向服务器发起请求，需要通过该地址。
    @SendTo(Constant.PRODUCERPATH)            //如果服务器接受到了消息，就会对订阅了@SendTo括号中的地址传送消息。
    public String addCustomer(String customerJson){
        return customerJson;
    }
    /**
     * 用户转人工，用户列表移除用户信息
     * @param customerJson
     * @return
     */
    @MessageMapping(Constant.REMOVESERVERPATH)//@MessageMapping和@RequestMapping功能类似，用于设置URL映射地址，浏览器向服务器发起请求，需要通过该地址。
    @SendTo(Constant.REMOVEPATH)            //如果服务器接受到了消息，就会对订阅了@SendTo括号中的地址传送消息。
    public String removeCustomer(String customerJson){
        return customerJson;
    }
    @MessageMapping("/message")
    public void sendToUser(String messageJson){
        JSONObject jsonObject = JSONObject.parseObject(messageJson);
        if(jsonObject.containsKey("to")&&jsonObject.containsKey("message")){
            webSocketService.sendToUser(jsonObject.get("to").toString(),messageJson);
            customServiceChatService.saveCustomServiceChat(jsonObject);
        }
    }
}


