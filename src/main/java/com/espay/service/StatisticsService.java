package com.espay.service;

import java.util.Map;

/**
 * 统计用户与机器客服和人工客服的沟通信息
 */
public interface StatisticsService {
    /**
     * 会话量:机器客服，人工客服，总量统计，消息量：机器客服，人工客服，消息总量统计
     * @return 包含统计信息的map
     */
    Map<String,Object> getAllState();

    /**
     * 命中率统计，统计机器客服能够给出答案的统计百分比
     * @return 返回最近七天的命中率
     */
    Map<String,Object> getHitRate();

    /**
     * 分页查询未命中问题统计，统计机器客服不能回答的问题
     * @param pageIndex 分页的起始页码
     * @param pageSize   一页的数据量
     * @return 分页的数据
     */
    Map<String,Object> getMissQuestions(int pageIndex,int pageSize);
}
