package com.espay.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.espay.pojo.PageConfig;
import com.espay.pojo.QuestionAnswerVO;
import com.espay.pojo.RepositoryVO;
import com.espay.pojo.ResultInfo;
import com.espay.service.IRepositoryService;

@Controller
@RequestMapping(value = "/repository")
public class RepositoryController {

	@Autowired
	private IRepositoryService repositoryService;

	private static Logger logger = Logger.getLogger(RepositoryController.class);

	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public ResultInfo getRepositoryList(@RequestBody RepositoryVO repositoryVo) {
		String pageIndexStr = repositoryVo.getPageIndex();
		String pageSizeStr = repositoryVo.getPageSize();
		String answer = repositoryVo.getAnswer();
		String question = repositoryVo.getQuestion();

		ResultInfo resultInfo = new ResultInfo();
		int pageIndexInt = 1;
		int pageSizeInt = 10;
		try {
			if (StringUtils.hasText(pageIndexStr)) {
				pageIndexInt = Integer.parseInt(pageIndexStr);
				pageIndexInt = pageIndexInt > 0 ? pageIndexInt : 1;
			}
			if (StringUtils.hasText(pageSizeStr)) {
				pageSizeInt = Integer.parseInt(pageSizeStr);
				pageSizeInt = pageSizeInt > 0 ? pageSizeInt : 10;
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			logger.error("传入页面参数错误: " + e1);
		}
		PageConfig pageConfig = new PageConfig();
		pageConfig.setPageIndex(pageIndexInt);
		pageConfig.setPageSize(pageSizeInt);

		try {
			int rows = repositoryService.getRepositoryCount(question, answer);
			List<QuestionAnswerVO> questionAnswerVOList = repositoryService.getRepositoryList(pageConfig, question,
					answer);
			HashMap<Object, Object> map;
			map = new HashMap<Object, Object>();
			map.put("data", questionAnswerVOList);
			map.put("rows", rows);
			resultInfo.setData(map);
			resultInfo.setStatusCode(200);
			return resultInfo;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询知识库出错: " + e);
			resultInfo.setMessage("查询知识库出错，请稍候再试");
			resultInfo.setStatusCode(300);
			return resultInfo;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResultInfo saveRepository(@RequestBody RepositoryVO repositoryVo) {
		String answer = repositoryVo.getAnswer();
		String[] questions = repositoryVo.getQuestions();
		String repositoryID = repositoryVo.getRepositoryID();

		QuestionAnswerVO questionAnswerVO = new QuestionAnswerVO();
		questionAnswerVO.setQuestions(questions);
		questionAnswerVO.setAnswer(answer);
		questionAnswerVO.setId(repositoryID);

		try {
			return repositoryService.saveRepository(questionAnswerVO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存出错: " + e);
			ResultInfo resultInfo = new ResultInfo();
			resultInfo.setMessage("保存失败");
			resultInfo.setStatusCode(300);
			return resultInfo;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResultInfo updateRepository(@RequestBody RepositoryVO repositoryVo) {
		String answer = repositoryVo.getAnswer();
		String[] questions = repositoryVo.getQuestions();
		String repositoryID = repositoryVo.getRepositoryID();

		QuestionAnswerVO questionAnswerVO = new QuestionAnswerVO();
		questionAnswerVO.setQuestions(questions);
		questionAnswerVO.setAnswer(answer);
		questionAnswerVO.setId(repositoryID);

		try {
			return repositoryService.updateRepository(questionAnswerVO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新出错: " + e);
			ResultInfo resultInfo = new ResultInfo();
			resultInfo.setMessage("更新失败");
			resultInfo.setStatusCode(300);
			return resultInfo;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResultInfo deleteRepository(@RequestBody RepositoryVO repositoryVo) {
		String[] delList = repositoryVo.getDelList();
		ResultInfo resultInfo = new ResultInfo();
		try {
			repositoryService.deleteRepository(delList);
			resultInfo.setMessage("删除成功");
			resultInfo.setStatusCode(200);
			return resultInfo;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除知识库出错: " + e);
			resultInfo.setMessage("删除失败");
			resultInfo.setStatusCode(300);
			return resultInfo;
		}
	}
	
	@ResponseBody  
    @RequestMapping(value="/importExcel",method={RequestMethod.GET,RequestMethod.POST})  
    public ResultInfo insertExcel(HttpServletRequest request,HttpSession session) throws Exception {  
		ResultInfo resultInfo = new ResultInfo();
		
		MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        MultipartFile file = multipartRequest.getFile("file");  
        if(file.isEmpty()){  
            throw new Exception("文件不存在！");  
        }
        try {
        	repositoryService.importExcel(file);
			resultInfo.setMessage("导入excel成功");
			resultInfo.setStatusCode(300);
			return resultInfo;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入excel失败" + e);
			resultInfo.setMessage("导入excel失败" + e.getMessage());
			resultInfo.setStatusCode(300);
			return resultInfo;
		}
        
    }
}
