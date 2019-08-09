package com.espay.service.impl;

import com.espay.service.WebsocketService;
import com.espay.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class WebSocketServiceImpl implements WebsocketService {

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 广播
     * 发给所有在线用户
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        template.convertAndSend(Constant.PRODUCERPATH, msg);
    }

    /**
     * 发送给指定用户
     *
     * @param user
     * @param msg
     */
    public void sendToUser(String user, String msg) {
        template.convertAndSendToUser(user, Constant.P2PPUSHPATH, msg);
    }

}