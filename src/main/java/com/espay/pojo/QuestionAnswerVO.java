package com.espay.pojo;

public class QuestionAnswerVO {
	private String id;
	private String[] questions;
	private String answer;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
}
