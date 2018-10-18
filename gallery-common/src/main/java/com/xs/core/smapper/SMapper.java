package com.xs.core.smapper;

import tk.mybatis.mapper.common.*;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:26
 * @Description: 定制版MyBatis Mapper插件接口，如需其他接口参考官方文档自行添加。
 */
public interface SMapper<T>
        extends
        BaseMapper<T>,
        ConditionMapper<T>,
        IdsMapper<T>,
        InsertListMapper<T>,
        Mapper<T>,
        MySqlMapper<T> {
}
