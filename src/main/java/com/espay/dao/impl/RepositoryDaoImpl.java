package com.espay.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.espay.dao.IRepositoryDao;
import com.espay.pojo.PageConfig;
import com.espay.pojo.StatementEntity;

@Repository(value = "repositoryDao")
public class RepositoryDaoImpl implements IRepositoryDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public int getRepositoryCount(String question, String answer) {
		Query query = new Query();
		if (StringUtils.hasText(question)) {
			query.addCriteria(Criteria.where("in_response_to.text").regex(".*?\\" + question.trim() + ".*"));
		}
		if (StringUtils.hasText(answer)) {
			query.addCriteria(Criteria.where("text").regex(".*?\\" + answer.trim() + ".*"));
		}
		//只查询有问答对的记录
		query.addCriteria(Criteria.where("occurrence").exists(false));
		
		Long count = mongoTemplate.count(query, StatementEntity.class);
		
		return count.intValue();
	}
	
	@Override
	public List<StatementEntity> getRepositoryList(PageConfig pageConfig, String question, String answer) {
		Query query = new Query();
		if (StringUtils.hasText(question)) {
			query.addCriteria(Criteria.where("in_response_to.text").regex(".*?\\" + question.trim() + ".*"));
		}
		if (StringUtils.hasText(answer)) {
			query.addCriteria(Criteria.where("text").regex(".*?\\" + answer.trim() + ".*"));
		}
		//只查询有问答对的记录
		query.addCriteria(Criteria.where("occurrence").exists(false));
		
		//分页
		int skip = (pageConfig.getPageIndex() -1) * pageConfig.getPageSize();
		query.skip(skip);// skip相当于从那条记录开始
		query.limit(pageConfig.getPageSize());// 从skip开始,取多少条记录
		
		//排序
		query.with(new Sort(Direction.DESC, "created_at"));
		
		List<StatementEntity> statementEntityList = mongoTemplate.find(query, StatementEntity.class);
		return statementEntityList;
	}

	@Override
	public List<StatementEntity> getRepositoryByIds(List<String> repositoryIdList) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(repositoryIdList));
		List<StatementEntity> statementEntityList = mongoTemplate.find(query, StatementEntity.class);
		return statementEntityList;
	}


	@Override
	public void saveRepositorys(List<StatementEntity> statements) {
		mongoTemplate.insertAll(statements);

	}

	@Override
	public void deleteRepositoryByIds(List<String> repositoryIdList) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(repositoryIdList));
		mongoTemplate.remove(query, StatementEntity.class);

	}

	@Override
	public void deleteRepositoryByQuestions(List<String> questionList) {
		Query query = new Query();
		query.addCriteria(Criteria.where("text").in(questionList));
		//只删除单独的问题记录
		query.addCriteria(Criteria.where("occurrence").exists(true));
		mongoTemplate.remove(query, StatementEntity.class);

	}

	@Override
	public void updateRepository(StatementEntity statement) {
		mongoTemplate.save(statement);
	}

	@Override
	public Boolean isExistText(String[] texts) {
		Query query = new Query();
		if(texts != null && texts.length > 0){
			query.addCriteria(Criteria.where("text").in(Arrays.asList(texts)));
		}
		Long count = mongoTemplate.count(query, StatementEntity.class);
		if(count > 0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void deleteRepositoryById(String repositoryId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(repositoryId));
		mongoTemplate.remove(query, StatementEntity.class);
		
	}

	@Override
	public StatementEntity getRepositoryById(String repositoryId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(repositoryId));
		StatementEntity statementEntity = mongoTemplate.findOne(query, StatementEntity.class);
		return statementEntity;
	}

	@Override
	public List<StatementEntity> getAllQuestions() {
		/*Query query = new Query();
		query.addCriteria(Criteria.where("occurrence").exists(true));
		String queryStr = query.toString();
		BasicQuery basicQuery = new BasicQuery(queryStr,"text");
		List<StatementEntity> find = mongoTemplate.find(query, StatementEntity.class);
		return find;*/
		
		Query query = new Query();
		//只查询有问答对的记录
		query.addCriteria(Criteria.where("occurrence").exists(true));
		
		List<StatementEntity> statementEntityList = mongoTemplate.find(query, StatementEntity.class);
		return statementEntityList;
	}

}
