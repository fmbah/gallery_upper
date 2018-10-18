package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.SlideMapper;
import com.xs.beans.Slide;
import com.xs.services.SlideService;
import com.xs.core.sservice.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("slideService")
@Transactional
public class SlideServiceImpl extends AbstractService<Slide> implements SlideService {
    @Autowired
    private SlideMapper slideMapper;

    @Override
    public Object queryWithPage(int page, int size, Integer type) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(Slide.class);
        Example.Criteria criteria = condition.createCriteria();
        if (type != null) {
            criteria.andEqualTo("type", type);
        }
        List<Slide> slideList = super.findByCondition(condition);
        PageInfo pageInfo = new PageInfo(slideList);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public void save(Slide model) {
        if (model.getId() != null) {
            super.deleteById(model.getId());
        }

        Condition condition = new Condition(Slide.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("type", model.getType());
        List<Slide> slideList = super.findByCondition(condition);
        if (model.getType().compareTo(new Byte("1")) == 0) {
            if (slideList.size() >= 3) {
                throw new ServiceException("首页轮播图最多三张");
            }
        } else {
            if (slideList.size() >= 1) {
                throw new ServiceException((model.getType().equals(new Byte("2")) ? "分享获益" : "会员权益") + "轮播图最多一张");
            }
        }

        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        super.save(model);
    }

    @Override
    public Slide findById(Integer id) {
        return super.findById(id);
    }
}
