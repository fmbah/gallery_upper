package com.xs.services;
import com.xs.beans.BrandPic;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2019/01/02
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface BrandPicService extends Service<BrandPic> {

    Object queryWithPage(int page, int size, Byte status, String picName, Integer brandId);
}
