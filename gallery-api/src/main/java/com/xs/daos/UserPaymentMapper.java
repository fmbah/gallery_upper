package com.xs.daos;

import com.xs.beans.UserPayment;
import com.xs.core.smapper.SMapper;

import java.util.List;

public interface UserPaymentMapper extends SMapper<UserPayment> {

    List<UserPayment> queryWithPage(UserPayment userPayment);
}