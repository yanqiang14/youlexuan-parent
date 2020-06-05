package com.offcn.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.TbTypeTemplate;
import com.offcn.pojo.TbTypeTemplateExample;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.sellergoods.service.TypeTemplateService;

import com.offcn.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 类型模板服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    @Override
    public List<Map> findSpecAndOption(Long typeTemplateId) {

        TbTypeTemplate typeTemplate = findOne(typeTemplateId);

        if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
            String specIds = typeTemplate.getSpecIds();//[{"id":27,"text":"网络",options:[]},{"id":32,"text":"机身内存"}]
            List<Map> specList = JSON.parseArray(specIds, Map.class);

            for (Map map : specList) {
                // {"id":27,"text":"网络"}
                long specId = Long.parseLong(map.get("id") + "");//规格id
                //根据规格id，查询规格选项
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                // select * from tb_spec_option where spec_id = ?
                criteria.andSpecIdEqualTo(specId);
                List<TbSpecificationOption> optionList = optionMapper.selectByExample(example);
                map.put("options", optionList);//options
            }
            return specList;
        }

        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }

    @Autowired
    RedisTemplate redisTemplate;

    private void saveHash() {
        List<TbTypeTemplate> list = findAll();
        for (TbTypeTemplate typeTemplate : list) {
            Long id = typeTemplate.getId();//模板id
            if(typeTemplate.getBrandIds()!=null&&typeTemplate.getBrandIds().length()>0){
                List<Map> brandIds = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
                redisTemplate.boundHashOps("brandHash").put(id, brandIds);
            }

            List<Map> specAndOption = findSpecAndOption(id);
            redisTemplate.boundHashOps("specHash").put(id, specAndOption);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();



        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);


        saveHash();

        return new PageResult(page.getTotal(), page.getResult());
    }

}
