package com.xs.services.impl;

import com.xs.daos.IncomexpenseMapper;
import com.xs.beans.Incomexpense;
import com.xs.services.IncomexpenseService;
import com.xs.core.sservice.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("incomexpenseService")
@Transactional
public class IncomexpenseServiceImpl extends AbstractService<Incomexpense> implements IncomexpenseService {
    @Autowired
    private IncomexpenseMapper incomexpenseMapper;

}
