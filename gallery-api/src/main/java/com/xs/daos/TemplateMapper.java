package com.xs.daos;

import com.xs.beans.SearchTemplates;
import com.xs.beans.Template;
import com.xs.core.smapper.SMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.HashMap;
import java.util.List;

public interface TemplateMapper extends SMapper<Template> {


    /**
     *
     * 功能描述: 检索功能
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午7:41
     */
    List<HashMap> searchTemplates(SearchTemplates searchTemplates);

}