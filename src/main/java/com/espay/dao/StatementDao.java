package com.espay.dao;

import com.espay.pojo.StatementEntity;

public interface StatementDao {
    /**
     * 保存知识到知识库
     * @param statement
     */
    void saveStatement(StatementEntity statement);

    /**
     * 根据知识库中存的知识的答案找到该知识
     * @param text
     */
    StatementEntity findStatementByText(String text);

}
