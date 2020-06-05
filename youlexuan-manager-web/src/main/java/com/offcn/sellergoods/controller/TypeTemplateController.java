package com.offcn.sellergoods.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.offcn.sellergoods.service.BrandService;
import com.offcn.sellergoods.service.SpecificationService;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbTypeTemplate;
import com.offcn.sellergoods.service.TypeTemplateService;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
/**
 * 类型模板controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

	@Reference(timeout = 5000)
	private TypeTemplateService typeTemplateService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbTypeTemplate> findAll(){			
		return typeTemplateService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return typeTemplateService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param typeTemplate
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbTypeTemplate typeTemplate){
		try {
			typeTemplateService.add(typeTemplate);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param typeTemplate
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbTypeTemplate typeTemplate){
		try {
			typeTemplateService.update(typeTemplate);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
//	@RequestMapping("/findOne/{tid}")         //   /user/1   user?id=1
	@RequestMapping("/findOne")         //   /user/1   user?id=1
	public TbTypeTemplate findOne( Long id){
		return typeTemplateService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			typeTemplateService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbTypeTemplate typeTemplate, int page, int rows  ){
		return typeTemplateService.findPage(typeTemplate, page, rows);		
	}

	@Reference(timeout = 5000)
	private SpecificationService specificationService;

	@Reference(timeout = 5000)
	BrandService brandService;


	//查询两个下拉框 初始化显示的数据
	@RequestMapping("/findDataList")
	public Map findDataList(){

		List<Map> specList = specificationService.findSpecList();
		List<Map> brandList = brandService.findBrandList();

		Map map = new HashMap<>();
		map.put("specList",specList);
		map.put("brandList",brandList);
		return map;
	}
	
}
