package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {

    boolean save(Clue clue);

    PaginationVo<Clue> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Clue detail(String id);


    boolean convert(String clueId, Tran t, String createBy);
}
