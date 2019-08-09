package com.espay.service.impl;

import com.espay.dao.CustomServiceChatDao;
import com.espay.dao.VisitorDao;
import com.espay.pojo.ChatbotMessage;
import com.espay.pojo.CustomServiceChat;
import com.espay.pojo.VisitorEntity;
import com.espay.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService{
    @Autowired
    @Qualifier(value = "visitorDaoImpl")
    private VisitorDao visitorDao;
    @Autowired
    @Qualifier(value = "CustomServiceChatDaoImpl")
    private CustomServiceChatDao customServiceChatsDao;
    /**
     * 会话量:机器客服，人工客服，总量统计，消息量：机器客服，人工客服，消息总量统计
     *
     * @return 包含统计信息的map
     */
    @Override
    public Map<String, Object> getAllState() {
        List<List<Integer>> dataList=new ArrayList<>();
        for(int i=0;i<6;i++){
            dataList.add(0, new ArrayList<>());
        }
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date todayStart=calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        Date todayEnd=calendar.getTime();
        for(int i=0;i<7;i++){
            calendar.setTime(todayStart);
            calendar.add(Calendar.DAY_OF_MONTH,i-6);
            Date dateStart=calendar.getTime();
            calendar.setTime(todayEnd);
            calendar.add(Calendar.DAY_OF_MONTH,i-6);
            Date dataEnd=calendar.getTime();
            List<VisitorEntity> visitorEntityList=visitorDao.findVisitorsByCreateTime(dateStart,dataEnd);
            List<CustomServiceChat> customServiceChatList =
                    customServiceChatsDao.findCustomServiceChatByCreateTime(dateStart,dataEnd);
            int botSessionCount=visitorEntityList.size();
            int artificalSessionCount= customServiceChatList.size();
            int sessionCount=botSessionCount+artificalSessionCount;
            int botMessageCount=0;
            for (VisitorEntity visitorEntity:visitorEntityList){
               botMessageCount+= visitorEntity.getQuestion().size();
            }
            int artificalMessageCount=0;
            for(CustomServiceChat customServiceChat : customServiceChatList){
                artificalMessageCount+= customServiceChat.getChatRecordList().size();
            }
            int messageCount=botMessageCount+artificalMessageCount;
            dataList.get(0).add(i,botSessionCount);
            dataList.get(1).add(i,artificalSessionCount);
            dataList.get(2).add(i,sessionCount);
            dataList.get(3).add(i,botMessageCount);
            dataList.get(4).add(i,artificalMessageCount);
            dataList.get(5).add(i,messageCount);
        }
        Map<String,Object> allStateMap=new HashMap<>();
        Map<String,Object>  tmpMap=new HashMap<>();
        Map<String,Object>  tempMap=new HashMap<>();
        tmpMap.put("bot",dataList.get(0));
        tmpMap.put("artificial",dataList.get(1));
        tmpMap.put("total",dataList.get(2));
        allStateMap.put("sessionCount",tmpMap);
        tempMap.put("bot",dataList.get(3));
        tempMap.put("artificial",dataList.get(4));
        tempMap.put("total",dataList.get(5));
        allStateMap.put("messageCount",tempMap);
        return allStateMap;
    }

    /**
     * 命中率统计，统计机器客服能够给出答案的统计百分比
     *
     * @return 返回最近七天的命中率
     */
    @Override
    public Map<String, Object> getHitRate() {
        List<List<Double>> dataList=new ArrayList<>();
        for(int i=0;i<2;i++){
            dataList.add(0, new ArrayList<>());
        }
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date todayStart=calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        Date todayEnd=calendar.getTime();
        for(int i=0;i<7;i++){
            calendar.setTime(todayStart);
            calendar.add(Calendar.DAY_OF_MONTH,i-6);
            Date dateStart=calendar.getTime();
            calendar.setTime(todayEnd);
            calendar.add(Calendar.DAY_OF_MONTH,i-6);
            Date dataEnd=calendar.getTime();
            List<VisitorEntity> visitorEntityList=visitorDao.findVisitorsByCreateTime(dateStart,dataEnd);
            int botMessageCount=0;
            int botHitMessageCount=0;
            for (VisitorEntity visitorEntity:visitorEntityList){
                List<ChatbotMessage> chatbotMessagesList=visitorEntity.getQuestion();
                botMessageCount+= chatbotMessagesList.size();
                for(ChatbotMessage chatbotMessage:chatbotMessagesList){
                    if(chatbotMessage.getAnswerId()!=null){
                        botHitMessageCount++;
                    }
                }

            }
            double hitRate=0;
            if(botMessageCount!=0){
                hitRate=botHitMessageCount*1.0/botMessageCount;
            }
            hitRate=(double) Math.round(hitRate*10000)/100;
            dataList.get(0).add(i,hitRate);
            dataList.get(1).add(i,(double)botMessageCount);
        }
        Map<String,Object> allStateMap=new HashMap<>();
        Map<String,Object>  tmpMap=new HashMap<>();
        tmpMap.put("hitRate",dataList.get(0));
        tmpMap.put("responseCount",dataList.get(1));
        allStateMap.put("hitStatistics",tmpMap);
        return allStateMap;
    }

    /**
     * 分页查询未命中问题统计，统计机器客服不能回答的问题
     *
     * @param pageIndex 分页的起始页码
     * @param pageSize  一页的数据量
     * @return 分页的数据
     */
    @Override
    public Map<String, Object> getMissQuestions(int pageIndex, int pageSize) {
        List<VisitorEntity> visitorEntityList=visitorDao.findAllVisitors();
        Map<String,ChatbotMessage> notHitQuestionMap=new LinkedHashMap<>();
        for (VisitorEntity visitorEntity:visitorEntityList){
            List<ChatbotMessage> chatbotMessagesList=visitorEntity.getQuestion();
            for(ChatbotMessage chatbotMessage:chatbotMessagesList){
                if(chatbotMessage.getAnswerId()==null){
                   notHitQuestionMap.put(chatbotMessage.getProblem(),chatbotMessage);
                }
            }
        }
        int items=notHitQuestionMap.size();
        Iterator<Map.Entry<String,ChatbotMessage>> entryIterator=notHitQuestionMap.entrySet().iterator();
        int start= (pageIndex - 1) * pageSize;
        int i=0;
        List<ChatbotMessage> messagesList=new ArrayList<>();
        while (entryIterator.hasNext()){
          Map.Entry<String,ChatbotMessage> entry= entryIterator.next();
            if(i>=start&&i<start+pageSize){
                messagesList.add(entry.getValue());
            }
            i++;
        }
        Map<String,Object>  tmpMap=new HashMap<>();
        tmpMap.put("question",messagesList);
        tmpMap.put("items",items);
        return tmpMap;
    }
}
