package com.espay.service;

public interface WebsocketService {
    /**
     * 广播
     * 发给所有在线用户
     *
     * @param msg
     */
    public void sendMsg(String msg);

    /**
     * 发送给指定用户
     *
     * @param user
     * @param msg
     */
    public void sendToUser(String user, String msg);
}
