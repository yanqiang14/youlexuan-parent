package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.entity.GoodsVO;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItem;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.util.List;

/**
 * 商品controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 30000)
	private GoodsService goodsService;

	/*@Reference(timeout = 30000)
	private ItemSearchService searchService;*/

	/*@Reference(timeout = 30000)
	private ItemPageService pageService;*/

	@Autowired
    private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDeleteDestination;//用户在索引库中删除记录

	@Autowired
	Destination topicPageDestination;//生成页面
	@Autowired
	Destination topicPageDeleteDestination;//删除页面

    @Autowired
    private JmsTemplate jmsTemplate;

	/*@RequestMapping("/genItemHtml")
	public void genItemHtml(Long id){
		pageService.genItemHtml(id);
	}*/

	@RequestMapping("/updateIsMarketable")
	public Result updateIsMarketable(Long[] ids,String isMarketable){
		try {
			goodsService.updateIsMarketable(ids,isMarketable);
			return  new Result(true,"操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return  new Result(true,"操作失败");
		}
	}

	/*商品审核*/
	@RequestMapping("/updateAuditStatus")
	public Result updateAuditStatus(Long[] ids,String auditStatus){
		try {
			goodsService.updateAuditStatus(ids,auditStatus);

			//审核通过  solr
			if (auditStatus.equals("1")){
				//将skulist导入到solr
				List<TbItem> list = goodsService.getItemListToSolr(ids);

                jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        String string = JSON.toJSONString(list);// [{},{},{}]
                        TextMessage textMessage = session.createTextMessage(string);
                        return textMessage;
                    }
                });

				jmsTemplate.send(topicPageDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						ObjectMessage objectMessage = session.createObjectMessage(ids);
						return objectMessage;
					}
				});


				//searchService.importItemList(list);//运营商审核的时候就将它存入solr中；

				//要生成详情页
				/*for (Long id : ids) {
					pageService.genItemHtml(id);
				}*/
			}

			return  new Result(true,"操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return  new Result(false,"操作失败");
		}
	}
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody GoodsVO vo){
		try {

			//商家id
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			vo.getGoods().setSellerId(sellerId);

			vo.getGoods().setAuditStatus(0+"");
			goodsService.add(vo);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	

	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);

			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage(ids);
					return objectMessage;
				}
			});

			//删除页面
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage(ids);
					return objectMessage;
				}
			});

			//solr中移除skulist
			/*searchService.removeSkuList(ids);*/

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

		//查询当前商家的商品根据sellerid
		/*String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);*/

		return goodsService.findPage(goods, page, rows);		
	}
	
}
