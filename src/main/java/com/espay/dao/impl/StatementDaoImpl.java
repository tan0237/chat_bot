package com.espay.dao.impl;

import com.espay.dao.StatementDao;
import com.espay.pojo.StatementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository(value = "statementDaoImpl")
public class StatementDaoImpl implements StatementDao {
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 保存知识到知识库
     *
     * @param statement
     */
    @Override
    public void saveStatement(StatementEntity statement) {

    }

    /**
     * 根据知识库中存的知识的答案找到该知识
     *
     * @param text
     */
    @Override
    public StatementEntity findStatementByText(String text) {
        Query query= new Query(Criteria.where("text").is(text));
        StatementEntity statementEntity= mongoTemplate.findOne(query,StatementEntity.class,"statements");
        return statementEntity;
    }
}
