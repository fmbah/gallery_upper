package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.TemplateLabels;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.LabelMapper;
import com.xs.beans.Label;
import com.xs.daos.TemplateLabelsMapper;
import com.xs.services.LabelService;
import com.xs.core.sservice.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("labelService")
@Transactional
public class LabelServiceImpl extends AbstractService<Label> implements LabelService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private TemplateLabelsMapper templateLabelsMapper;


    @Override
    public void save(Label model) {
        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        super.save(model);
    }

    @Override
    public void update(Label model) {

        Label label = this.findById(model.getId());
        if (label == null) {
            throw new ServiceException("标签数据不存在或已删除");
        }

        BeanUtils.copyProperties(model, label);
        label.setGmtModified(new Date());
        super.update(label);
    }

    @Override
    public Label findById(Integer id) {
        return super.findById(id);
    }

    @Override
    public Object queryWithPage(int page, int size, String name) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(Label.class);
        Example.Criteria criteria = condition.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }

        List<Label> list = super.findByCondition(condition);
        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public void deleteById(Integer id) {

        Condition condition = new Condition(TemplateLabels.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("labelId", id);
        List<TemplateLabels> templateLabels = templateLabelsMapper.selectByCondition(condition);
        if (templateLabels != null && !templateLabels.isEmpty()) {
            throw new ServiceException("标签数据已绑定模板");
        }

        super.deleteById(id);
    }
}
