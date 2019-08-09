package com.espay.dao;

import java.util.List;

import com.espay.pojo.PageConfig;
import com.espay.pojo.StatementEntity;

public interface IRepositoryDao {
	
	/**
	 * 获取符合查询条件的知识库记录数
	 * @author tancheng
	 */
	int getRepositoryCount(String question, String answer);
	
	/**
	 * 获取分页后的知识库列表
	 * @author tancheng
	 */
	List<StatementEntity> getRepositoryList(PageConfig pageConfig, String question, String answer);
	
	/**
	 * 根据知识库Document Id 批量查询
	 * @author tancheng
	 */
	List<StatementEntity> getRepositoryByIds(List<String> repositoryIdList);
	
	/**
	 * 查询数据库里是否已存在相同的答案或问题
	 */
	Boolean isExistText(String[] texts);
	
	/**
	 * 批量新增知识库知识
	 * @author tancheng
	 */
	void saveRepositorys(List<StatementEntity> statements);
	
	/**
	 * 批量删除知识库知识
	 * @author tancheng
	 */
	void deleteRepositoryByIds(List<String> repositoryIdList);
	
	/**
	 * 批量删除知识库里单独的问题(含occurrence字段的)
	 * @author tancheng
	 */
	void deleteRepositoryByQuestions(List<String> questionList);
	
	/**
	 * 更新知识库知识
	 * @author tancheng
	 */
	void updateRepository(StatementEntity statement);
	
	/**
	 * 根据id删除知识库知识
	 * @author tancheng
	 */
	void deleteRepositoryById(String repositoryId);
	
	/**
	 * 根据id查询知识库
	 * @author tancheng
	 */
	StatementEntity getRepositoryById(String repositoryId);
	
	/**
	 * 查询知识库里的所有问题
	 */
	
	List<StatementEntity> getAllQuestions();
}
