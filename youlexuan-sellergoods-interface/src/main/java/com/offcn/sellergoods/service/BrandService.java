package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    List<Map> findBrandList();

    /**
     * 查询所有品牌
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum 当前第几页
     * @param pageSize 每页显示多少条
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);


    /**
     * 带有条件的分页查询
     * @param pageNum
     * @param pageSize
     * @param searchEntity
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize,TbBrand searchEntity);

    /**
     * 添加品牌
     * @param brand
     * @return
     */
    public Result add(TbBrand brand);

    /**
     * 根据主键修改品牌
     * @param brand
     * @return
     */
    public Result update(TbBrand brand);

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    public TbBrand findById(Long id);

    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long[] ids);
}
