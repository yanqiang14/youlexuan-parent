package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    TbBrandMapper brandMapper;

    @Override
    public List<Map> findBrandList() {
        return brandMapper.findBrandList();
    }

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        long total = page.getTotal();
        List<TbBrand> list = page.getResult();

        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize, TbBrand searchEntity) {

        PageHelper.startPage(pageNum,pageSize);//设置分页参数

        //构造查询条件
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        // select * from tb_brand where name like ? and firstChar = ?
        if(searchEntity!=null){
            if(searchEntity.getName()!=null && searchEntity.getName().length()>0){
                criteria.andNameLike("%"+searchEntity.getName()+"%"); //name like ?
            }
            if(searchEntity.getFirstChar()!=null && searchEntity.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(searchEntity.getFirstChar());//  firstChar = ?
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);


        long total = page.getTotal();
        List<TbBrand> list = page.getResult();

        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }

    @Override
    public Result add(TbBrand brand) {
        //brand 校验brand中的数据格式..
       /* try {
            if(brand!=null){
                // name不能为空  firstChar长度等于1
                if(brand.getName()!=null && brand.getName().length()>0 && brand.getFirstChar()!=null && brand.getFirstChar().length()==1){
                    int i = brandMapper.insert(brand);//i > 0 添加成功
                    if(i>0){
                        //添加成功
                        return  new Result(true,"添加成功");
                    }else{
                        return  new Result(false,"添加失败");
                    }
                }else{
                    return new Result(false,"参数格式不正确");
                }
            }else{
                return  new Result(false,"brand不能为null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"程序出现异常");
        }*/
       if(brand==null){
           return new Result(false,"参数不正确");
       }

       if(brand.getName()==null || brand.getName().length()==0 || brand.getFirstChar()==null || brand.getFirstChar().length()==0){
           return new Result(false,"name或者firstChar不能为空");
       }

        try {
            int insert = brandMapper.insert(brand);
            if(insert<=0){
                return new Result(false,"添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
        return new Result(true,"添加成功");
    }

    @Override
    public Result update(TbBrand brand) {
        if(brand!=null){
//            brandMapper.updateByPrimaryKey(brand);
            int i = brandMapper.updateByPrimaryKeySelective(brand);
            if(i>0){
                return new Result(true,"修改成功");
            }else {
                return new Result(false,"修改失败");
            }
        }
        return new Result(false,"参数不能为null");
    }

    @Override
    public TbBrand findById(Long id) {
        if(id!=null){
            return brandMapper.selectByPrimaryKey(id);
        }
        return null;
    }

    @Override
    public void delete(Long[] ids) {

        //delete from tb_brand where in in (1,5,7);

       /* if(ids.length==0){

        }*/

        /*TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        List<Long> list = Arrays.asList(ids);
        criteria.andIdIn(list);
        brandMapper.deleteByExample(example);*/

        if(ids!=null && ids.length>0){
            for (Long id : ids) {
                int i = brandMapper.deleteByPrimaryKey(id);
            }
        }
    }
}
