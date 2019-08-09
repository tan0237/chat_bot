package com.espay.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.espay.pojo.PageConfig;
import com.espay.pojo.QuestionAnswerVO;
import com.espay.pojo.ResultInfo;

public interface IRepositoryService {
	
	/**
	 * 获取符合查询条件的知识库记录数
	 * @author tancheng
	 */
	int getRepositoryCount(String question, String answer);
	
	/**
	 * 获取知识库列表
	 * @author tancheng
	 */
	List<QuestionAnswerVO> getRepositoryList(PageConfig pageConfig, String question, String answer);
	
	/**
	 * 新增知识库知识
	 * @author tancheng
	 */
	ResultInfo saveRepository(QuestionAnswerVO questionAnswerVO);
	
	/**
	 * 删除知识库知识
	 * @author tancheng
	 */
	void deleteRepository(String[] repositoryIds);
	
	/**
	 * 更新知识库知识
	 * @author tancheng
	 */
	ResultInfo updateRepository(QuestionAnswerVO questionAnswerVO);
	
	/**
	 * 获取符合查询条件的知识库记录数
	 * @author tancheng
	 * @throws Exception 
	 */
	void importExcel(MultipartFile file) throws Exception;
}
