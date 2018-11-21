package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.User;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.daos.BrandCdkeyMapper;
import com.xs.beans.BrandCdkey;
import com.xs.daos.UserMapper;
import com.xs.services.BrandCdkeyService;
import com.xs.core.sservice.AbstractService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
