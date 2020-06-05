package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference(timeout = 5000)
    BrandService brandService;

    @RequestMapping("/findAll")   // brand/findAll.do
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int pageNum,int pageSize){
        return brandService.findPage(pageNum,pageSize);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){ // 前台传过来的json格式： {id:1,name:'aaaaa'} ==>RequestBody ==> TbBrand brand
        Result result = brandService.add(brand);
        return  result;
    }

    @RequestMapping("/update")
    public  Result update(@RequestBody TbBrand brand){
        Result update = brandService.update(brand);
        return update;
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findById(id);
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return  new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }


    /*带条件的分页查询*/
    @RequestMapping("/search")
    public PageResult search(int pageNum,int pageSize,@RequestBody TbBrand searchEntity){
//        aaa();
        return brandService.findPage(pageNum,pageSize,searchEntity);
    }

    public List<Map> getBrandList(){
//        [{id:1,text:''},{},{}]  // select id, name as 'text' from tb_brand;
        List<TbBrand> list = brandService.findAll();

        List<Map> result = new ArrayList<>();

        for (TbBrand brand : list) {
            Map map = new HashMap();
            map.put("id",brand.getId());
            map.put("text",brand.getName());
            result.add(map);
        }
        return result;
    }

}
