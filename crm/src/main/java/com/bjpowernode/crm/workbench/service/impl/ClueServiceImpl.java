package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ClueDao;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);


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
}
