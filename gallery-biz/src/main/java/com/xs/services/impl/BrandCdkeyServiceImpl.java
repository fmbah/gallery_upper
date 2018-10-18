package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.daos.BrandCdkeyMapper;
import com.xs.beans.BrandCdkey;
import com.xs.services.BrandCdkeyService;
import com.xs.core.sservice.AbstractService;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("brandcdkeyService")
@Transactional
public class BrandCdkeyServiceImpl extends AbstractService<BrandCdkey> implements BrandCdkeyService {
    @Autowired
    private BrandCdkeyMapper brandcdkeyMapper;
    @Autowired
    private OssConfig ossConfig;


    @Override
    public Object queryWithPage(int page, int size, String code, String isUsed, Integer brandId) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(BrandCdkey.class);
        Example.Criteria criteria = condition.createCriteria();
        if (!StringUtils.isEmpty(code)) {
            criteria.andLike("code", "%"+ code +"%");
        }
        if (!StringUtils.isEmpty(isUsed)) {
            criteria.andEqualTo("isUsed", new Byte(isUsed));
        }
        if (brandId != null) {
            criteria.andEqualTo("brandId", brandId);
        }

        condition.setOrderByClause(" gmt_modified desc");

        List<BrandCdkey> list = super.findByCondition(condition);
        //TODO 使用用户名称
        for (int i = 0; i < list.size(); i++) {

            list.get(i).setIsUsedStr(list.get(i).getIsUsed().byteValue() == 0 ? "未激活" : "已激活");

        }

        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public String cdkExport(int page, int size, Integer brandId) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(BrandCdkey.class);
        Example.Criteria criteria = condition.createCriteria();
        if (brandId != null) {
            criteria.andEqualTo("brandId", brandId);
        }

        condition.setOrderByClause(" gmt_modified desc");
        List<BrandCdkey> list = super.findByCondition(condition);

        PageInfo pageInfo = new PageInfo(list);

        list = pageInfo.getList();
        //TODO 使用用户名称
        for (int i = 0; i < list.size(); i++) {

            list.get(i).setIsUsedStr(list.get(i).getIsUsed().byteValue() == 0 ? "未激活" : "已激活");
        }
        File exportFile = null;
        try{
            Map<String,Object> model = new HashMap<>();
            model.put("cdks", list);
            exportFile = File.createTempFile("cdks",".xlsx");
            JxlsExportUtil.exportExcel("static/template_file/cdks.xlsx","static/template_file/cdks.xml",exportFile,model);

            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            try {
                ossClient.putObject(ossConfig.getBucket(), exportFile.getName(), new FileInputStream(exportFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), exportFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
            return url.toString().replaceAll("http", "https");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(exportFile != null) {
                exportFile.delete();
            }
        }
        return null;
    }
}
