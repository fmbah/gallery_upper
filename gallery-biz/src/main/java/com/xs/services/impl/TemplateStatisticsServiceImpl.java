package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.TemplateStatistics;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.core.sservice.AbstractService;
import com.xs.daos.TemplateStatisticsMapper;
import com.xs.services.TemplateStatisticsService;
import com.xs.services.UpLoadService;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service("templatestatisticsService")
@Transactional
public class TemplateStatisticsServiceImpl extends AbstractService<TemplateStatistics> implements TemplateStatisticsService {
    @Autowired
    private TemplateStatisticsMapper templatestatisticsMapper;
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private UpLoadService upLoadService;

    @Override
    public Object queryWithPage(int page, int size, Integer categoryId, String name,
                                Integer brandId, Boolean isBrand, String sTime, String eTime, Boolean isExport) {

        PageHelper.startPage(page, size);

        TemplateStatistics templateStatistics = new TemplateStatistics();
        templateStatistics.setCategoryId(categoryId);
        templateStatistics.setName(name);
        templateStatistics.setBrandId(brandId);
        templateStatistics.setsTime(sTime);
        templateStatistics.seteTime(eTime);
        List<HashMap> list = null;
        if (isBrand) {
            list = templatestatisticsMapper.queryBrandTemplateCensusDatas(templateStatistics);
        } else {
            list = templatestatisticsMapper.queryTemplateCensusDatas(templateStatistics);
        }
        PageInfo pageInfo = new PageInfo(list);

        if (isExport) {
            File exportFile = null;
            try{
                Map<String,Object> model = new HashMap<>();
                model.put("users", list);
                if (isBrand) {
                    exportFile = File.createTempFile("模板统计数据",".xlsx");
                    JxlsExportUtil.exportExcel("static/template_file/brandTemplateCensus.xlsx","static/template_file/brandTemplateCensus.xml",exportFile,model);
                } else {
                    exportFile = File.createTempFile("模板统计数据",".xlsx");
                    JxlsExportUtil.exportExcel("static/template_file/templateCensus.xlsx","static/template_file/templateCensus.xml",exportFile,model);
                }


//                OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
//                try {
//                    ossClient.putObject(ossConfig.getBucket(), exportFile.getName(), new FileInputStream(exportFile));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), exportFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//                return ResultGenerator.genSuccessResult(url.toString().replaceAll("http", "https"));
                return ResultGenerator.genSuccessResult(upLoadService.upFile(exportFile));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(exportFile != null) {
                    exportFile.delete();
                }
            }
        }


        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    public List<HashMap> queryCategoryDatas(String sTime, String eTime) {
        TemplateStatistics templateStatistics = new TemplateStatistics();
        templateStatistics.setsTime(sTime);
        templateStatistics.seteTime(eTime);
        return templatestatisticsMapper.queryCategoryDatas(templateStatistics);
    }
}
