package com.espay.service.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.espay.dao.IRepositoryDao;
import com.espay.pojo.ChatbotQuestion;
import com.espay.pojo.ExtraData;
import com.espay.pojo.PageConfig;
import com.espay.pojo.QuestionAnswerVO;
import com.espay.pojo.ResultInfo;
import com.espay.pojo.StatementEntity;
import com.espay.service.IRepositoryService;
import com.espay.util.ExcelUtils;

@Service
public class RepositoryServiceImpl implements IRepositoryService {

	private static Logger logger = Logger.getLogger(RepositoryServiceImpl.class);

	@Autowired
	private IRepositoryDao repositoryDao;

	@Override
	public int getRepositoryCount(String question, String answer) {
		int rows = repositoryDao.getRepositoryCount(question, answer);
		return rows;
	}

	@Override
	public List<QuestionAnswerVO> getRepositoryList(PageConfig pageConfig, String question, String answer) {
		List<QuestionAnswerVO> questionAnswerVOList = new ArrayList<QuestionAnswerVO>();
		List<StatementEntity> repositoryList = repositoryDao.getRepositoryList(pageConfig, question, answer);
		Iterator<StatementEntity> iterator = repositoryList.iterator();
		while (iterator.hasNext()) {
			QuestionAnswerVO questionAnswerVO = new QuestionAnswerVO();
			List<String> questionList = new ArrayList<String>();
			StatementEntity next = iterator.next();
			List<ChatbotQuestion> inResponseTo = next.getInResponseTo();
			Iterator<ChatbotQuestion> iterator2 = inResponseTo.iterator();
			while (iterator2.hasNext()) {
				ChatbotQuestion next2 = iterator2.next();
				questionList.add(next2.getText());
			}
			int size = questionList.size();
			String[] questions = (String[]) questionList.toArray(new String[size]);
			questionAnswerVO.setQuestions(questions);
			questionAnswerVO.setAnswer(next.getText());
			questionAnswerVO.setId(next.getId());
			questionAnswerVOList.add(questionAnswerVO);
		}
		return questionAnswerVOList;
	}

	@Override
	public ResultInfo saveRepository(QuestionAnswerVO questionAnswerVO) {
		String[] questions = questionAnswerVO.getQuestions();
		String answer = questionAnswerVO.getAnswer();
		ResultInfo resultInfo = new ResultInfo();

		// 校验数据格式
		String errorInfo = CheckQusetionAnswer(questions, answer, resultInfo);
		if (!"200".equals(errorInfo)) {
			resultInfo.setStatusCode(300);
			resultInfo.setMessage("保存失败" + errorInfo);
			return resultInfo;
		}
		// 检查是否有已存在的问题或答案
		String[] texts = Arrays.copyOf(questions, questions.length + 1);
		texts[texts.length - 1] = answer;
		Boolean existText = repositoryDao.isExistText(texts);
		if (existText) {
			resultInfo.setStatusCode(300);
			resultInfo.setMessage("保存失败(问题或答案已经存在)");
			return resultInfo;
		}
		
		List<StatementEntity> questionEntities = getStatementEntitys(questions, answer);
		repositoryDao.saveRepositorys(questionEntities);
		resultInfo.setStatusCode(200);
		resultInfo.setMessage("保存成功！");
		return resultInfo;
	}

	

	@Override
	public void deleteRepository(String[] repositoryIds) {
		List<String> repositoryIdList = new ArrayList<String>(Arrays.asList(repositoryIds));
		List<StatementEntity> statementEntityList = repositoryDao.getRepositoryByIds(repositoryIdList);
		List<String> repositoryList = new ArrayList<String>();
		List<String> questionList = new ArrayList<String>();
		for (int i = 0; i < statementEntityList.size(); i++) {
			StatementEntity statementEntity = statementEntityList.get(i);
			repositoryList.add(statementEntity.getId());
			List<ChatbotQuestion> inResponseTo = statementEntity.getInResponseTo();
			for (int j = 0; j < inResponseTo.size(); j++) {
				questionList.add(inResponseTo.get(j).getText());
			}
		}
		// 删除问答对
		repositoryDao.deleteRepositoryByIds(repositoryList);
		// 删除问题
		repositoryDao.deleteRepositoryByQuestions(questionList);
	}

	@Override
	public ResultInfo updateRepository(QuestionAnswerVO questionAnswerVO) {
		String[] questions = questionAnswerVO.getQuestions();
		String answer = questionAnswerVO.getAnswer();
		String repositoryId = questionAnswerVO.getId();
		ResultInfo resultInfo = new ResultInfo();

		// 校验数据
		String errorInfo = CheckQusetionAnswer(questions, answer, resultInfo);
		if (!"200".equals(errorInfo)) {
			resultInfo.setStatusCode(300);
			resultInfo.setMessage("修改失败" + errorInfo);
			return resultInfo;
		}
		StatementEntity statementEntity = repositoryDao.getRepositoryById(repositoryId);
		// 删除问题记录
		List<String> questionList = new ArrayList<String>();
		List<ChatbotQuestion> inResponseToList = statementEntity.getInResponseTo();
		for (int i = 0; i < inResponseToList.size(); i++) {
			questionList.add(inResponseToList.get(i).getText());
		}
		repositoryDao.deleteRepositoryByQuestions(questionList);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// 问答对里的inResponseTo
		List<ChatbotQuestion> inResponseTos = new ArrayList<ChatbotQuestion>();
		// 单独的问题记录
		List<StatementEntity> questionEntities = new ArrayList<StatementEntity>();
		for (int i = 0; i < questions.length; i++) {
			if (!Objects.equals(answer, questions[i])) {
				StatementEntity entity = new StatementEntity();
				entity.setText(questions[i]);
				entity.setOccurrence(1);
				entity.setInResponseTo(new ArrayList<ChatbotQuestion>());
				entity.setCreateAt(timestamp);
				entity.setKnowledgeClass("");
				entity.setExtraData(new ExtraData());
				questionEntities.add(entity);
			}
			ChatbotQuestion inResponseTo = new ChatbotQuestion();
			inResponseTo.setCreateAt(timestamp);
			inResponseTo.setOccurrence(1);
			inResponseTo.setText(questions[i]);
			inResponseTos.add(inResponseTo);
		}
		// 填充问答对对象
		statementEntity.setText(answer);
		statementEntity.setCreateAt(timestamp);
		statementEntity.setInResponseTo(inResponseTos);

		// 插入问题记录
		repositoryDao.saveRepositorys(questionEntities);

		// 修改问答对记录
		repositoryDao.updateRepository(statementEntity);

		resultInfo.setStatusCode(200);
		resultInfo.setMessage("修改成功！");
		return resultInfo;

	}
	
	/**
	 * excle导入知识库
	 */
	@Override
	public void importExcel(MultipartFile file) throws Exception {
		InputStream in = file.getInputStream();
		List<List<Object>> listob = ExcelUtils.getBankListByExcel(in,file.getOriginalFilename());
		Iterator<List<Object>> iterator = listob.iterator();
		List<StatementEntity> statementEntityList = new ArrayList<StatementEntity>();
		
		List<StatementEntity> allEntity = repositoryDao.getAllQuestions();
		List<String> allText = new ArrayList<String>();
		for(int i = 0; i < allEntity.size(); i++){
			allText.add(allEntity.get(i).getText());
		}
		while(iterator.hasNext()){
			List<Object> next = iterator.next();
			List<StatementEntity> entitys = this.getExcelEntity(next, allText);
			if(entitys != null && entitys.size() > 0){
				statementEntityList.addAll(entitys);
			}
		}
		
		repositoryDao.saveRepositorys(statementEntityList);
		
	}

	/**
	 * 校验问题和回答的格式，去除字段为空的问题
	 * 
	 * @param question
	 * @param answer
	 * @param resultInfo
	 * @return
	 */
	private String CheckQusetionAnswer(String[] questions, String answer, ResultInfo resultInfo) {
		int maxTextLength = 300;

		if (StringUtils.isEmpty(answer)) {
			return "（答案不能为空）";
		}
		if (answer.length() > maxTextLength) {
			return "（答案长度不能大于300）";
		}
		if (questions.length < 1) {
			return "至少需要插入一个问题";
		}

		for (int i = 0; i < questions.length; i++) {
			if (StringUtils.isEmpty(questions[i])) {
				return "（问题不能为空）";
			}
			if (questions[i].length() > maxTextLength) {
				return "（问题长度不能大于300）";
			}
			if (Objects.equals(answer, questions[i])){
				return "（问题不能与答案相同）";
			}
		}

		return "200";
	}
	
	
	
	/**
	 * 通过excel读取的数据构建语料实体类
	 * @param next
	 * @param allQuestionList 
	 * @return
	 */
	private List<StatementEntity> getExcelEntity (List<Object> next, List<String> allText){
		if(next.size() < 2){
			logger.error("导入excel时有问答对信息不全：" + next);
			return null;
		}
		List<String> questionList = new ArrayList<String>();
		int count = 0;
		String answer = "";
		Iterator<Object> iterator = next.iterator();
		while(iterator.hasNext()){
			String text = (String)iterator.next();
			text = text.trim();
			//分离答案和问题数组
			if(count == 1){
				answer = text;
				iterator.remove();
			}else{
				questionList.add(text);
			}
			count++;
		}
		
		// 判断问答对格式是否正确
		String codeStatus = this.checkExcelText(answer, questionList, next);
		if(!"200".equals(codeStatus)){
			return null;
		}
		
		String[] questions = new String[questionList.size()];
		questionList.toArray(questions);
		
		
		//去除数据库里和要导入的excel里已经存在的重复语料,
		if(allText.contains(answer)){
			return null;
		}else{
			allText.add(answer);
		}
		if(questions != null){
			for(int i = 0 ; i < questions.length; i++ ){
				if(allText.contains(questions[i])){
					return null;
				}else{
					allText.add(questions[i]);
				}
			}
		}
		return this.getStatementEntitys(questions, answer);
		
		
	}
	
	/**
	 * 检查一行语料是否符合规范
	 * @param answer
	 * @param question
	 * @return
	 */
	private String checkExcelText(String answer, List<String> questionList, List<Object> next) {
		// 判断问答对格式是否正确
		int maxTextLength = 300;
		if (StringUtils.isEmpty(answer)) {
			logger.error("导入excel时有语料答案为空：" + next);
			return "300";
		}
		if (answer.length() > maxTextLength) {
			logger.error("导入excel时有答案超出长度：" + next);
			return "300";
		}
		if (questionList.size() < 1) {
			logger.error("导入excel时有语料问题为空 ：" + next);
			return "300";
		}
		// 若不存在符合规范的问题，则返回null
		Boolean exitsQuestion = false;
		Iterator<String> iterator = questionList.iterator();
		while(iterator.hasNext()){
			String question = iterator.next();
			if (StringUtils.isEmpty(question)) {
				iterator.remove();
				continue;
			}
			if (Objects.equals(answer, question)){
				logger.error("导入excel时有有问题和答案相同：" + next);
				iterator.remove();
				continue;
			}
			if (question.length() > maxTextLength) {
				logger.error("导入excel时有问题超出长度：" + next);
				iterator.remove();
				continue;
			}
			
			exitsQuestion = true;
			
		}
		
		if (!exitsQuestion) {
			return "300";
		}
		return "200";
	}
	
	private List<StatementEntity> getStatementEntitys(String[] questions, String answer) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// 问答对
		StatementEntity statementEntity = new StatementEntity();
		// 问答对里的inResponseTo
		List<ChatbotQuestion> inResponseTos = new ArrayList<ChatbotQuestion>();
		// 单独的问题记录
		List<StatementEntity> questionEntities = new ArrayList<StatementEntity>();
		for (int i = 0; i < questions.length; i++) {
			if (!Objects.equals(answer, questions[i])) {
				// 填充单独的问题
				StatementEntity entity = new StatementEntity();
				entity.setText(questions[i]);
				entity.setOccurrence(1);
				entity.setInResponseTo(new ArrayList<ChatbotQuestion>());
				entity.setCreateAt(timestamp);
				entity.setKnowledgeClass("");
				entity.setExtraData(new ExtraData());
				questionEntities.add(entity);
			}
			// 填充in_response_to
			ChatbotQuestion inResponseTo = new ChatbotQuestion();
			inResponseTo.setCreateAt(timestamp);
			inResponseTo.setOccurrence(1);
			inResponseTo.setText(questions[i]);
			inResponseTos.add(inResponseTo);
		}
		// 填充问答对对象
		statementEntity.setText(answer);
		statementEntity.setCreateAt(timestamp);
		statementEntity.setInResponseTo(inResponseTos);
		statementEntity.setKnowledgeClass("");
		ExtraData extraData = new ExtraData();
		statementEntity.setExtraData(extraData);

		// 批量保存
		questionEntities.add(statementEntity);
		return questionEntities;
	}
}
