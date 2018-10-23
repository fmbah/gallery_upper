package com.xs.services.impl;

import com.xs.daos.ShareProfitMapper;
import com.xs.beans.ShareProfit;
import com.xs.services.ShareProfitService;
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

@Service("shareprofitService")
@Transactional
public class ShareProfitServiceImpl extends AbstractService<ShareProfit> implements ShareProfitService {
    @Autowired
    private ShareProfitMapper shareprofitMapper;

}
