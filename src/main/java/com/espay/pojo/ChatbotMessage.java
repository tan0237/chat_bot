package com.espay.pojo;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;

@Document
public class ChatbotMessage {
    @Field(value = "problem")
    private String problem;
    @Field(value = "answer")
    private String answer;
    @Field(value = "answertime")
    private Date createTime;
    @Field(value = "answerId")
    private String answerId;

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}
