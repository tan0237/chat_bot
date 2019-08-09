package com.espay.pojo;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Document(collection = "statements")
public class StatementEntity {
    @Id
    private String id;
    @Field(value = "text")
    private String text;
    @Field(value = "in_response_to")
    private List<ChatbotQuestion> inResponseTo;
    @Field(value = "created_at")
    private Date createAt;
    @Field(value = "occurrence")
    private Integer occurrence;
    @Field(value = "extra_data")
    private ExtraData extraData;
    @Field(value = "knowledgeClass")
    private String knowledgeClass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ChatbotQuestion> getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(List<ChatbotQuestion> inResponseTo) {
        this.inResponseTo = inResponseTo;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public ExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraData extraData) {
        this.extraData = extraData;
    }

    public String getKnowledgeClass() {
        return knowledgeClass;
    }

    public void setKnowledgeClass(String knowledgeClass) {
        this.knowledgeClass = knowledgeClass;
    }
}
