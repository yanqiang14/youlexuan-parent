package com.offcn.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.offcn.entity.SpecificationVO;
import com.offcn.pojo.TbSpecification;

import com.offcn.entity.PageResult;
/**
 * 规格服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	public List<Map> findSpecList();

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(SpecificationVO specification);
	
	
	/**
	 * 修改
	 */
	public void update(SpecificationVO vo);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public SpecificationVO findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize);
	
}
