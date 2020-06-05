package com.offcn.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbContentExample.Criteria;
import com.offcn.content.service.ContentService;

import com.offcn.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 内容服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	private TbContentMapper contentMapper;

	@Override
	public List<TbContent> listContentByCategoryId(Long categoryId) {

		//先从redis缓存服务器中查询
		List<TbContent> list = (List<TbContent>)redisTemplate.boundHashOps("contentHash").get(categoryId);

		if(list!=null && list.size()>0){
			System.out.println("走redis");
			//缓存中有
			return list;
		}

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);//mapper接口层需要和数据库层做交换所以需要通过条件来进行查询

		list = contentMapper.selectByExample(example);//数据库
		System.out.println("走mysql");
		//存入redis
		redisTemplate.boundHashOps("contentHash").put(categoryId,list);

		return list;
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {


		int i = contentMapper.insert(content);
		if(i>0){
			//清理redis缓存数据
			redisTemplate.boundHashOps("contentHash").delete(content.getCategoryId());
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		int i = contentMapper.updateByPrimaryKey(content);
		if(i>0){
			redisTemplate.boundHashOps("contentHash").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			int i = contentMapper.deleteByPrimaryKey(id);
			if(i>0){
				Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
				//清理redis缓存数据
				redisTemplate.boundHashOps("contentHash").delete(categoryId);
			}
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
