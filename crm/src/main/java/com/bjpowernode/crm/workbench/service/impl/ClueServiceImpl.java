package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements  ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    public boolean save(Clue clue) {

        boolean flag = true;
        int count = clueDao.save(clue);
        if (count!=1){
           flag = false;
        }
        return flag;
    }

    public PaginationVo<Clue> pageList(Map<String, Object> map) {

        //取得查询线索总记录数total
        int count = clueDao.getTotalByCondition(map);

        //取得查询线索List
        List<Clue> list = clueDao.getClueListByCondition(map);

        //封装vo
        PaginationVo<Clue> vo = new PaginationVo<Clue>();
        vo.setList(list);
        vo.setTotal(count);
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = true;

        //删除线索
        int count = clueDao.delete(ids);

        //比较数量是否相同
        if (count != ids.length){

            flag = false;
        }

        return flag;
    }

    public Clue detail(String id) {

        Clue clue = clueDao.detail(id);
        return clue;
    }

    public boolean convert(String clueId, Tran t, String createBy) {

        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue c = clueDao.getById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        if (cus == null){

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(c.getAddress());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setContactSummary(c.getContactSummary());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);

            int count1 = customerDao.save(cus);
            if (count1 != 1){
                flag =false;
            }
        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setAddress(c.getAddress());
        contacts.setSource(c.getSource());
        contacts.setOwner(c.getOwner());
        contacts.setNextContactTime(c.getNextContactTime());
        contacts.setMphone(c.getMphone());
        contacts.setJob(c.getJob());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setFullname(c.getFullname());
        contacts.setEmail(c.getEmail());
        contacts.setDescription(c.getDescription());
        contacts.setCustomerId(cus.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(c.getContactSummary());
        contacts.setAppellation(c.getAppellation());

        int count2 = contactsDao.save(contacts);
        if (count2 != 1){
            flag =false;
        }

        //(4) 线索备注转换到客户备注以及联系人备注
        //查询出与线索关联的线索备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListbyClueId(clueId);
        for (ClueRemark clueRemark : clueRemarkList) {
            //取出备注信息
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setNoteContent(noteContent);

            int count3 = customerRemarkDao.save(customerRemark);
            if (count3 != 1){
                flag =false;
            }

            //创建联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setNoteContent(noteContent);

            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4 != 1){
                flag =false;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //查询与线索关联的市场活动
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            String activityId = clueActivityRelation.getActivityId();
            //创建联系人与市场活动关联对象
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setId(UUIDUtil.getUUID());

            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5 != 1){
                flag =false;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if (t!=null){

            //完善交易t中的信息
            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(contacts.getId());
            //添加交易
            int count6 = tranDao.save(t);
            if(count6!=1){
                flag = false;
            }
            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setTranId(t.getId());
            tranHistory.setStage(t.getStage());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setCreateTime(createTime);
            tranHistory.setCreateBy(createBy);
            //添加交易历史
            int count7 = tranHistoryDao.save(tranHistory);
            if(count7!=1){
                flag = false;
            }
        }

        //(8) 删除线索备注
        int count8 = clueRemarkDao.deleteByClueId(clueId);
        if(count8==0){
            flag = false;
        }

        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList){
           int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9!=1){
                flag = false;
            }
        }

        //(10) 删除线索
        int count10 = clueDao.deleteByClueId(clueId);
        if(count10!=1){
            flag = false;
        }

        return flag;
    }


}
