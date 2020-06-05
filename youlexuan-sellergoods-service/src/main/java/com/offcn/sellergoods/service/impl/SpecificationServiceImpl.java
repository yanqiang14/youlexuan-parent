package com.offcn.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.offcn.entity.SpecificationVO;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.sellergoods.service.SpecificationService;

import com.offcn.entity.PageResult;

/**
 * 规格服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    @Override
    public List<Map> findSpecList() {
        return specificationMapper.findSpecList();
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(SpecificationVO vo) {

        //两张表

//		specificationMapper.insert(specification);
        TbSpecification specification = vo.getSpecification();//规格

        specificationMapper.insert(specification);//规格表添加一条数据,添加成功后，返回主键，给规格选项去用

        Long specId = vo.getSpecification().getId();//返回的规格id(主键)

        List<TbSpecificationOption> optionList = vo.getSpecificationOptionList();//多方表（规格选项表），每一个规格选项需要设置外键（spec_id）

        for (TbSpecificationOption option : optionList) {
            option.setSpecId(specId);
//            option.setId();
            optionMapper.insert(option);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(SpecificationVO vo) {
//        specificationMapper.updateByPrimaryKey(specification);

        TbSpecification specification = vo.getSpecification();
        List<TbSpecificationOption> list = vo.getSpecificationOptionList();

        specificationMapper.updateByPrimaryKey(specification);// update tb_spec set k1=v1,k2=v2.... where id = ?

        //修改规格选项
        /*for (TbSpecificationOption option : list) {
            optionMapper.updateByPrimaryKey(option);// update tb_spec_option set k1=v1,k2=v2.... where id = ?
        }*/

        //把原来的所有的规格选项删除，重新添加

        // 1、删除
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        optionMapper.deleteByExample(example);
        // 2、重新添加
        for (TbSpecificationOption option : list) {
            //id specid
            optionMapper.insert(option);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public SpecificationVO findOne(Long id) {
//        return specificationMapper.selectByPrimaryKey(id);
        SpecificationVO vo = new SpecificationVO();


        vo.setSpecification(specificationMapper.selectByPrimaryKey(id));

        //查询规格选项的列表
        //select * from tb_specification_option where spec_id = ?

        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);

        List<TbSpecificationOption> list = optionMapper.selectByExample(example);

        vo.setSpecificationOptionList(list);

        return vo;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) { // 规格的id数组
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);//删除规格

            //删除完规格，还要删除当前规格的所有规格选项
            // delete from tb_spec_option where spec_id = ?
            //删除规格选项
            // delete from tb_specification_option where spec_id = ?     规格id
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id); // spec_id = ?
//            criteria.andIdEqualTo()

            optionMapper.deleteByExample(example);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
