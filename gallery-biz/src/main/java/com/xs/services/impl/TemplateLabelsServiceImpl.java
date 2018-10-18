package com.xs.services.impl;

import com.xs.daos.TemplateLabelsMapper;
import com.xs.beans.TemplateLabels;
import com.xs.services.TemplateLabelsService;
import com.xs.core.sservice.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("templatelabelsService")
@Transactional
public class TemplateLabelsServiceImpl extends AbstractService<TemplateLabels> implements TemplateLabelsService {
    @Autowired
    private TemplateLabelsMapper templatelabelsMapper;

}
