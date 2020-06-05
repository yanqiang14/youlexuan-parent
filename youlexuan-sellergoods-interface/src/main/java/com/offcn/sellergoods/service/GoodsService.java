package com.offcn.sellergoods.service;
import java.util.List;

import com.offcn.entity.GoodsVO;
import com.offcn.entity.Result;
import com.offcn.pojo.TbGoods;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbItem;

/**
 * 商品服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 *
	 * @param ids  spu的id数组
	 * @return
	 */
	List<TbItem> getItemListToSolr(Long[] ids);

	void updateIsMarketable(Long[] ids,String isMarketable);


	void updateAuditStatus(Long[] ids, String auditStatus);


	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(GoodsVO vo);
	
	
	/**
	 * 修改
	 */
	public void update(GoodsVO vo);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public GoodsVO findOne(Long id);
	
	
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
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);
	
}
