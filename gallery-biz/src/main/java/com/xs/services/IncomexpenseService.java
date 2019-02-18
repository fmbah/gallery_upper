package com.xs.services;
import com.xs.beans.Incomexpense;
import com.xs.core.sservice.Service;

import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface IncomexpenseService extends Service<Incomexpense> {


    public Object list(Integer page, Integer size
            , String type
            , Byte subType
            , Integer userId);
}
