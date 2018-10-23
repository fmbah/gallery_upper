package com.xs.daos;

import com.xs.beans.DrawcashLog;
import com.xs.core.smapper.SMapper;

import java.util.List;

public interface DrawcashLogMapper extends SMapper<DrawcashLog> {

    List<DrawcashLog> queryWithPage(DrawcashLog drawcashLog);

}