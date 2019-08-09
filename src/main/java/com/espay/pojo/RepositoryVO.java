package com.espay.pojo;

public class RepositoryVO {

	private String repositoryID;
	private String knowledgeClass;
	private String pageIndex;
	private String pageSize;
	private String[] delList;
	private String[] questions;
	private String question;
	private String answer;
	
	public String getRepositoryID() {
		return repositoryID;
	}
	public void setRepositoryID(String repositoryID) {
		this.repositoryID = repositoryID;
	}
	public String getKnowledgeClass() {
		return knowledgeClass;
	}
	public void setKnowledgeClass(String knowledgeClass) {
		this.knowledgeClass = knowledgeClass;
	}
	public String getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String[] getDelList() {
		return delList;
	}
	public void setDelList(String[] delList) {
		this.delList = delList;
	}
	public String[] getQuestions() {
		return questions;
	}
	public void setQuestions(String[] questions) {
		this.questions = questions;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	

}
