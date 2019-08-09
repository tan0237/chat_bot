package com.espay.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.espay.dao.StatementDao;
import com.espay.dao.VisitorDao;
import com.espay.pojo.ChatbotMessage;
import com.espay.pojo.StatementEntity;
import com.espay.pojo.VisitorEntity;
import com.espay.service.RobotCustomService;
import com.espay.util.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * 调用机器客服引擎接口实现
 */
@Service
public class RobotCustomServiceImpl implements RobotCustomService {
    private static Logger logger = Logger.getLogger(RobotCustomServiceImpl.class);
    @Autowired
    @Qualifier(value = "visitorDaoImpl")
    private VisitorDao visitorDao;
    @Autowired
    @Qualifier(value = "statementDaoImpl")
    private StatementDao statementDao;
    /**
     * 拿到机器客服返回的信息
     *
     * @param message
     */
    @Override
    public JSONObject getRobotAnswer(JSONObject message) {
        Properties properties = new Properties();
        InputStream in = RobotCustomServiceImpl.class.getClassLoader().getResourceAsStream("vector_match.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error(e);
        }
        String url = properties.getProperty("submit_url", "");
        if(message==null||message.isEmpty()){
            return null;
        }
        JSONObject jsonObject=HttpUtil.doPost(url,message);
        return jsonObject;
    }

    /**
     * 记录用户与机器客服的对话消息
     *
     * @param session
     * @param questionJson
     * @param answerJson
     * @return
     */
    @Override
    public boolean insertMessage(HttpSession session, JSONObject questionJson, JSONObject answerJson) {
        String tmpId=  String.valueOf(session.getAttribute("_id"));
        String username = questionJson.getString("username");
        String question=questionJson.getString("info");
        String answer=answerJson.getString("answer");
        String ip=questionJson.getString("ip");
        String answerId=null;
        if(ip==null){
            ip=UUID.randomUUID().toString().replace("-","");
        }
        if(username==null||username.isEmpty()){
            username="游客"+ip.replace(".","");
        }
        if(!"该问题正在学习中".equals(answer)) {
          StatementEntity statement= statementDao.findStatementByText(answer);
          if(statement !=null){
            answerId=statement.getId();
          }
        }
        VisitorEntity visitor= visitorDao.findVisitorByUsername(username);
        if(visitor==null||(!visitor.getId().equals(tmpId))){
            visitor=new VisitorEntity();
            visitor.setUsername(username);
            visitor.setCreateTime(new Timestamp(System.currentTimeMillis()));
            visitor.setIp(ip);
            ChatbotMessage chatbotMessage=new ChatbotMessage();
            chatbotMessage.setAnswer(answer);
            chatbotMessage.setAnswerId(answerId);
            chatbotMessage.setCreateTime(new Timestamp(System.currentTimeMillis()));
            chatbotMessage.setProblem(question);
            List<ChatbotMessage> chatbotMessageList=new ArrayList<>();
            chatbotMessageList.add(chatbotMessage);
            visitor.setQuestion(chatbotMessageList);
            visitorDao.saveVisitor(visitor);
            visitor= visitorDao.findVisitorByUsername(username);
            String visitorId= visitor.getId();
            session.setAttribute("_id",visitorId);
            return true;
        }
        if ( visitor.getId().equals(tmpId)){
            List<ChatbotMessage> chatbotMessageList=new ArrayList<>();
            ChatbotMessage chatbotMessage=new ChatbotMessage();
            chatbotMessage.setAnswer(answer);
            chatbotMessage.setAnswerId(answerId);
            chatbotMessage.setCreateTime(new Timestamp(System.currentTimeMillis()));
            chatbotMessage.setProblem(question);
            chatbotMessageList.add(chatbotMessage);
            visitor.setQuestion(chatbotMessageList);
            visitorDao.updateVisitor(visitor);
            return true;
        }
        return false;
    }


}
