package com.xs.services.impl;

import com.xs.daos.ActiveCdkMapper;
import com.xs.beans.ActiveCdk;
import com.xs.services.ActiveCdkService;
import com.xs.core.sservice.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


/**
\* User: zhaoxin
\* Date: 2018/10/19
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("activecdkService")
@Transactional
public class ActiveCdkServiceImpl extends AbstractService<ActiveCdk> implements ActiveCdkService {
    @Autowired
    private ActiveCdkMapper activecdkMapper;

}
