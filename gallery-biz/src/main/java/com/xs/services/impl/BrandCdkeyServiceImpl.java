package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.BrandCdkCodePrice;
import com.xs.beans.CompanyBrand;
import com.xs.beans.User;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.daos.BrandCdkeyMapper;
import com.xs.beans.BrandCdkey;
import com.xs.daos.CompanyBrandMapper;
import com.xs.daos.UserMapper;
import com.xs.services.BrandCdkeyService;
import com.xs.core.sservice.AbstractService;
import com.xs.services.UpLoadService;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.xs.core.ProjectConstant.BRAND_CODE_PRICE;
import static com.xs.core.ProjectConstant.CODE_PRICE;


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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;
    @Autowired
    private UpLoadService upLoadService;


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
        for (int i = 0; i < list.size(); i++) {

            list.get(i).setIsUsedStr(list.get(i).getIsUsed().byteValue() == 0 ? "未激活" : "已激活");
            User user = userMapper.selectByPrimaryKey(list.get(i).getUsedUserId());
            if (user != null) {
                list.get(i).setUsedUserName(user.getNickname());
            } else {
                list.get(i).setUsedUserName(null);
            }

            if (list.get(i).getIsUsed().equals(new Byte("0"))) {
                list.get(i).setUsedTime(null);
                list.get(i).setGmtModified(null);
            }
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
        for (int i = 0; i < list.size(); i++) {

            list.get(i).setIsUsedStr(list.get(i).getIsUsed().byteValue() == 0 ? "未激活" : "已激活");

            User user = userMapper.selectByPrimaryKey(list.get(i).getUsedUserId());
            if (user != null) {
                list.get(i).setUsedUserName(user.getNickname());
            } else {
                list.get(i).setUsedUserName(null);
            }
        }
        File exportFile = null;
        try{
            Map<String,Object> model = new HashMap<>();
            model.put("cdks", list);
            exportFile = File.createTempFile("cdks",".xlsx");
            JxlsExportUtil.exportExcel("static/template_file/cdks.xlsx","static/template_file/cdks.xml",exportFile,model);

//            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
//            try {
//                ossClient.putObject(ossConfig.getBucket(), exportFile.getName(), new FileInputStream(exportFile));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), exportFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//            return url.toString().replaceAll("http", "https");
            return upLoadService.upFile(exportFile).toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(exportFile != null) {
                exportFile.delete();
            }
        }
        return null;
    }

    @Override
    public Object settingCodePrice(BigDecimal price) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(CODE_PRICE, price.toString());
        }
        return ResultGenerator.genSuccessResult();
    }

    @Override
    public Object getCodePrice() {
        try (Jedis jedis = jedisPool.getResource()) {
            return ResultGenerator.genSuccessResult(jedis.get(CODE_PRICE));
        }
    }

    @Override
    public Object getAllBrandCodePrice() {
        try (Jedis jedis = jedisPool.getResource()) {


            List<CompanyBrand> companyBrands = companyBrandMapper.selectAll();
            for (int i = 0; i < companyBrands.size(); i++) {
                CompanyBrand companyBrand = companyBrands.get(i);
                String value = jedis.get(String.format(BRAND_CODE_PRICE, companyBrand.getId().toString()));
                if (StringUtils.isEmpty(value)) {
                    jedis.set(String.format(BRAND_CODE_PRICE, companyBrand.getId().toString()), "100");
                }
            }

            Set<String> keys = jedis.keys(String.format(BRAND_CODE_PRICE, "*"));
            List<BrandCdkCodePrice> result = new ArrayList<>();
            for (String key: keys) {
                String brandId =  key.split(":")[2];
                CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(Integer.valueOf(brandId));
                if (companyBrand != null) {
                    BrandCdkCodePrice brandCdkCodePrice = new BrandCdkCodePrice();
                    brandCdkCodePrice.setBrandId(companyBrand.getId());
                    brandCdkCodePrice.setBrandName(companyBrand.getName());
                    brandCdkCodePrice.setPayName("品牌会员");
                    brandCdkCodePrice.setPrice(new BigDecimal(jedis.get(key)));
                    result.add(brandCdkCodePrice);
                } else {
                    jedis.del(key);
                }
            }

            return ResultGenerator.genSuccessResult(result);
        }
    }

    @Override
    public Object settingOneCodePrice(BigDecimal price, String brandId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(String.format(BRAND_CODE_PRICE, brandId));
            if (StringUtils.isEmpty(value)) {
                return ResultGenerator.genFailResult("未找到相应品牌激活码支付数据");
            }
            jedis.set(String.format(BRAND_CODE_PRICE, brandId), price.toString());
        }
        return ResultGenerator.genSuccessResult();
    }
}
