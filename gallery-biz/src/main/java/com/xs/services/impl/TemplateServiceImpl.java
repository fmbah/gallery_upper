package com.xs.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.*;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ProjectConstant;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.*;
import com.xs.services.TemplateCategoryService;
import com.xs.services.TemplateService;
import com.xs.core.sservice.AbstractService;
import com.xs.services.UpLoadService;
import com.xs.utils.ImageBase64Utils;
import com.xs.utils.OssUpLoadUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("templateService")
@Transactional
public class TemplateServiceImpl extends AbstractService<Template> implements TemplateService {
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TemplateCategoryMapper templateCategoryMapper;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;
    @Autowired
    private TemplateLabelsMapper templateLabelsMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private OssConfig ossConfig;

    @Override
    public Object queryWithPage(int page, int size, Boolean enabled, Byte ratio, Integer categoryId, String name, Integer brandId, Boolean isBrand) {

        PageHelper.startPage(page, size);

        Condition condition = new Condition(Template.class);
        Example.Criteria criteria = condition.createCriteria();

        if (enabled != null) {
            criteria.andEqualTo("enabled", enabled);
        }
        if (ratio != null) {
            criteria.andEqualTo("ratio", ratio);
        }
        if (categoryId != null) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%"+ name +"%");
        }
        if (isBrand) {
            if (brandId != null) {
                criteria.andEqualTo("brandId", brandId);
            } else {
                criteria.andNotEqualTo("brandId", 0);
            }
        } else {
            criteria.andEqualTo("brandId", 0);
        }

        condition.setOrderByClause(" id desc");
        List<Template> list = super.findByCondition(condition);

        for (int i = 0, j = list.size(); i < j; i++) {

            TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(list.get(i).getCategoryId());
            if (templateCategory != null) {
                list.get(i).setCategoryName(templateCategory.getTitle());
            }

            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(list.get(i).getBrandId());
            if (companyBrand != null) {
                list.get(i).setBrandName(companyBrand.getName());
            }

        }

        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    public void save(Template model) {

        TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(model.getCategoryId());
        if (templateCategory == null) {
            throw new ServiceException("模块分类数据不存在或已删除");
        }

        if (model.getBrandId() != null && model.getBrandId().intValue() != 0) {
            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(model.getBrandId());
            if (companyBrand == null) {
                throw new ServiceException("品牌数据不存在或已删除");
            }
        }

        if (StringUtils.isEmpty(model.getLabelIds())) {
            throw new ServiceException("标签数据不可为空");
        } else {
            for (String labelId : model.getLabelIds().split(",")) {
                Label label = labelMapper.selectByPrimaryKey(Integer.valueOf(labelId));
                if (label == null) {
                    throw new ServiceException("标签数据不存在或已删除");
                }
            }
        }

        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());

//        BASE64Decoder decoder = new BASE64Decoder();
//        File template = null;
//        try {
//            String s1 = model.getPreviewImageUrl().split("data:image/")[1];
//            template = File.createTempFile("template", ".".concat(s1.substring(0, s1.indexOf(";"))));
//            FileOutputStream write = new FileOutputStream(template);
//            byte[] decoderBytes = decoder.decodeBuffer(model.getPreviewImageUrl().split(",")[1]);
//            write.write(decoderBytes);
//            write.close();
//
//            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
//            try {
//                ossClient.putObject(ossConfig.getBucket(), template.getName(), new FileInputStream(template));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), template.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//            if(url != null) {
//                model.setPreviewImageUrl(ProjectConstant.ALIYUN_OSS_IMG_ADDRESS + template.getName());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (template != null) {
//                template.delete();
//            }
//        }

        super.save(model);

        for (String labelId : model.getLabelIds().split(",")) {
            TemplateLabels templateLabels = new TemplateLabels();
            templateLabels.setGmtCreate(new Date());
            templateLabels.setLabelId(Integer.valueOf(labelId));
            templateLabels.setTemplateId(model.getId());
            templateLabelsMapper.insert(templateLabels);
        }
    }

    @Override
    public void update(Template model) {
        TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(model.getCategoryId());
        if (templateCategory == null) {
            throw new ServiceException("模块分类数据不存在或已删除");
        }

        Template template = this.findById(model.getId());
        if (template == null) {
            throw new ServiceException("模块数据不存在或已删除");
        }
        if (model.getBrandId() != null && model.getBrandId().intValue() != 0) {
            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(model.getBrandId());
            if (companyBrand == null) {
                throw new ServiceException("品牌数据不存在或已删除");
            }
        }

        if (StringUtils.isEmpty(model.getLabelIds())) {
            throw new ServiceException("标签数据不可为空");
        } else {
            for (String labelId : model.getLabelIds().split(",")) {
                Label label = labelMapper.selectByPrimaryKey(Integer.valueOf(labelId));
                if (label == null) {
                    throw new ServiceException("标签数据不存在或已删除");
                }
            }
        }

        BeanUtils.copyProperties(model, template);

        template.setGmtModified(new Date());

//        BASE64Decoder decoder = new BASE64Decoder();
//        File templateFile = null;
//        try {
//            String s1 = model.getPreviewImageUrl().split("data:image/")[1];
//            templateFile = File.createTempFile("template", ".".concat(s1.substring(0, s1.indexOf(";"))));
//            FileOutputStream write = new FileOutputStream(templateFile);
//            byte[] decoderBytes = decoder.decodeBuffer(model.getPreviewImageUrl().split(",")[1]);
//            write.write(decoderBytes);
//            write.close();
//
//            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
//            try {
//                ossClient.putObject(ossConfig.getBucket(), templateFile.getName(), new FileInputStream(templateFile));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), templateFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//
//            if(url != null) {
//                template.setPreviewImageUrl(ProjectConstant.ALIYUN_OSS_IMG_ADDRESS + templateFile.getName());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (templateFile != null) {
//                templateFile.delete();
//            }
//        }

        super.update(template);

        Condition templateLabelsCondition = new Condition(TemplateLabels.class);
        Example.Criteria templateLabelsConditionCriteria = templateLabelsCondition.createCriteria();
        templateLabelsConditionCriteria.andEqualTo("templateId", template.getId());
        templateLabelsMapper.deleteByCondition(templateLabelsCondition);

        for (String labelId : model.getLabelIds().split(",")) {
            TemplateLabels templateLabels = new TemplateLabels();
            templateLabels.setGmtCreate(new Date());
            templateLabels.setLabelId(Integer.valueOf(labelId));
            templateLabels.setTemplateId(template.getId());
            templateLabelsMapper.insert(templateLabels);
        }
    }

    @Override
    public Template findById(Integer id) {
        Template template = super.findById(id);
        if (template == null) {
            throw new ServiceException("模板数据不存在或已删除");
        }
        if (!StringUtils.isEmpty(template.getDescri())) {
            JSONObject jsonObject = JSON.parseObject(template.getDescri());
            if (jsonObject != null) {
                Object layerMain = jsonObject.get("layerMain");
                if (layerMain != null) {
                    JSONArray jsonArray = JSON.parseArray(layerMain.toString());
                    if (jsonArray != null) {
                        Iterator<Object> iterator = jsonArray.iterator();
                        while (iterator.hasNext()) {
                            Object next = iterator.next();
                            if (next != null) {
                                JSONObject layerMainJson = JSON.parseObject(next.toString());
                                if (layerMainJson != null) {
                                    Object imgInfo = layerMainJson.get("imgInfo");
                                    if (imgInfo != null) {
                                        JSONObject imgInfoJson = JSON.parseObject(imgInfo.toString());
                                        Object url = imgInfoJson.get("url");
                                        if (url != null) {
                                            String s = url.toString();
                                            if (s.indexOf("data:image/") < 0) {
                                                String subfix = s.substring(s.lastIndexOf(".")+1, s.length());

                                                String prefix = "data:image/" + subfix + ";base64,";
                                                String allUrl = prefix;
                                                allUrl += ImageBase64Utils.imgBase64(s);

                                                template.setDescri(template.getDescri().replaceAll(s, allUrl));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Condition templateLabelsCondition = new Condition(TemplateLabels.class);
        Example.Criteria templateLabelsConditionCriteria = templateLabelsCondition.createCriteria();
        templateLabelsConditionCriteria.andEqualTo("templateId", template.getId());
        List<TemplateLabels> templateLabels = templateLabelsMapper.selectByCondition(templateLabelsCondition);
        if (templateLabels != null) {
            List<HashMap> lidsList = new ArrayList<>();
            for(TemplateLabels tl : templateLabels) {
                Label label = labelMapper.selectByPrimaryKey(tl.getLabelId());
                if (label != null) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("name", label.getName());
                    hashMap.put("id", label.getId());
                    lidsList.add(hashMap);
                }
            }
            if (!lidsList.isEmpty()) {
                template.setLabelIds(JSONObject.toJSONString(lidsList));
            }
        }
        return template;
    }
}
