package com.espay.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "visitors")
public class VisitorEntity implements Serializable {
    @Id
    private String id;
    @Field(value = "username")
    private String username;
    @Field(value = "ip")
    private String ip;
    @Field(value = "question")
    private List<ChatbotMessage> question;
    @Field(value = "create_time")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<ChatbotMessage> getQuestion() {
        return question;
    }

    public void setQuestion(List<ChatbotMessage> question) {
        this.question = question;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
