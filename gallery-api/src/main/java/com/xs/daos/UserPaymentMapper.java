package com.xs.daos;

import com.xs.beans.UserPayment;
import com.xs.core.smapper.SMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface UserPaymentMapper extends SMapper<UserPayment> {

    List<UserPayment> queryWithPage(UserPayment userPayment);

    Integer getPayBrandUserCount(@Param("brandIds") List<Integer> brandIds);
}