package com.espay.dao;

import com.espay.pojo.VisitorEntity;

import java.util.Date;
import java.util.List;


public interface VisitorDao {

    /**
     * 创建对象
     *
     * @param visitor
     */

    void saveVisitor(VisitorEntity visitor);

    /**
     * 根据用户名查询对象
     *
     * @param username
     * @return
     */

    VisitorEntity findVisitorByUsername(String username);

    /**
     * 更新对象
     *
     * @param visitor
     */

    void updateVisitor(VisitorEntity visitor);

    /**
     * 找到在该时间段创建的智能引擎的对话
     * @param createTimeStart 开始时间
     * @param createTimeEnd   结束时间
     * @return
     */
    List<VisitorEntity> findVisitorsByCreateTime(Date createTimeStart,Date createTimeEnd);

    /**
     * 查询所有机器客服和用户的记录
     * @return 记录列表
     */
    List<VisitorEntity> findAllVisitors();

}
