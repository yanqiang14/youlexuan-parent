package com.offcn.sellergoods.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offcn.entity.GoodsVO;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;

import com.offcn.entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    private boolean checkGoodsStatus(Long id){
        String auditStatus = goodsMapper.selectByPrimaryKey(id).getAuditStatus();
        if(auditStatus.equals("1")){
            return true;
        }
        return  false;
    }

    @Override
    public List<TbItem> getItemListToSolr(Long[] ids) {
        // select * from tb_item  where goodsId in ( ids )

        if(ids==null || ids.length == 0){
            return new ArrayList<>();
        }

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));

        return itemMapper.selectByExample(example);

    }

    @Override
    public void updateIsMarketable(Long[] ids, String isMarketable) {
        for (Long id : ids) {
            //当前商品的状态是审核通过
            boolean b = checkGoodsStatus(id);
            if(b){
                TbGoods tbGoods = new TbGoods();
                tbGoods.setId(id);
                tbGoods.setIsMarketable(isMarketable);

                goodsMapper.updateByPrimaryKeySelective(tbGoods);
            }
        }
    }

    @Override
    public void updateAuditStatus(Long[] ids, String auditStatus) {
        for (Long id : ids) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);
            tbGoods.setAuditStatus(auditStatus);

            goodsMapper.updateByPrimaryKeySelective(tbGoods);//spu状态
            //sku的状态

            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);

            List<TbItem> list = itemMapper.selectByExample(example);//sku列表

            for (TbItem item : list) {
                item.setStatus(auditStatus);
                itemMapper.updateByPrimaryKey(item);
            }

        }
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    private void saveItem(GoodsVO vo){
        //判断是否启用了规格
        if (vo.getGoods().getIsEnableSpec().equals("1")) {
            //skuList
            List<TbItem> skuList = vo.getSkuList();
            for (TbItem item : skuList) {
                //title 商品标题 （spu名称 + 所有规格选项名称） 测试商品1113 移动3G 32G
                String title = vo.getGoods().getGoodsName();
                //{"网络":"移动3G","机身内存":"32G"}
                Map map = JSON.parseObject(item.getSpec());
                Set set = map.keySet();//map中所有的key
                for (Object key : set) {
                    Object optionName = map.get(key);
                    title += " " + optionName;
                }
                item.setTitle(title);

                //image itemImages中的第一个图片的url
                List<Map> itemImages = JSON.parseArray(vo.getGoodsDesc().getItemImages(), Map.class);
                if (itemImages != null && itemImages.size() > 0) {
                    Object url = itemImages.get(0).get("url");
                    item.setImage(url + "");
                }
                //categoryId  第三级商品分类id（叶子分类id）
                Long category3Id = vo.getGoods().getCategory3Id();
                item.setCategoryid(category3Id);

                //create_time
                item.setCreateTime(new Date());
                // update_time
                item.setUpdateTime(new Date());

                //goods_id spu的主键
                item.setGoodsId(vo.getGoods().getId());

                // seller_id  商家id
                String sellerId = vo.getGoods().getSellerId();
                item.setSellerId(sellerId);

                //category  冗余字段，叶子分类名称
                String name = itemCatMapper.selectByPrimaryKey(category3Id).getName();
                item.setCategory(name);

                //brand  品牌名称
                String brand = brandMapper.selectByPrimaryKey(vo.getGoods().getBrandId()).getName();
                item.setBrand(brand);

                //seller 商家名称
                String seller = sellerMapper.selectByPrimaryKey(sellerId).getName();
                item.setSeller(seller);

                itemMapper.insert(item);
            }
        } else {
            //没有启用规格的情况下，也要向sku表中存一条数据即可,默认的sku

            TbItem item = new TbItem();

            item.setTitle(vo.getGoods().getGoodsName());//spu名称
            item.setPrice(vo.getGoods().getPrice());
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量

            //create_time
            item.setCreateTime(new Date());
            // update_time
            item.setUpdateTime(new Date());


            //category  冗余字段，叶子分类名称
            String name = itemCatMapper.selectByPrimaryKey(vo.getGoods().getCategory3Id()).getName();
            item.setCategory(name);

            //brand  品牌名称
            String brand = brandMapper.selectByPrimaryKey(vo.getGoods().getBrandId()).getName();
            item.setBrand(brand);

            //seller 商家名称
            String seller = sellerMapper.selectByPrimaryKey(vo.getGoods().getSellerId()).getName();
            item.setSeller(seller);

            item.setSpec("{}");

            itemMapper.insert(item);

        }
    }

    /**
     * 增加
     */
    @Override
    public void add(GoodsVO vo) {
        goodsMapper.insert(vo.getGoods());//goods
        //tb_goods_desc
        Long id = vo.getGoods().getId();//spu主键
        vo.getGoodsDesc().setGoodsId(id);
        goodsDescMapper.insert(vo.getGoodsDesc());//goodsDesc

        saveItem( vo);

    }


    /**
     * 修改
     */
    @Override
    public void update(GoodsVO vo) {
        //商品修改后，运营商需要再来审核

//        goodsMapper.updateByPrimaryKey(goods);
        TbGoods goods = vo.getGoods();
        goods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods);

        TbGoodsDesc goodsDesc = vo.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(goodsDesc);

//        List<TbItem> skuList = vo.getSkuList();

        //先把之前的skulist删除，在重新添加
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getId());
        itemMapper.deleteByExample(example);

        saveItem(vo);

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public GoodsVO findOne(Long id) {
//        return goodsMapper.selectByPrimaryKey(id);
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);

        List<TbItem> skuList = itemMapper.selectByExample(example);//sku列表

        GoodsVO goodsVO = new GoodsVO();
        goodsVO.setGoods(tbGoods);
        goodsVO.setGoodsDesc(tbGoodsDesc);
        goodsVO.setSkuList(skuList);
        return goodsVO;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
//            goodsMapper.deleteByPrimaryKey(id);
            // is_delete =  1
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);
            tbGoods.setIsDelete("1");

            goodsMapper.updateByPrimaryKeySelective(tbGoods);
        }

        //把每一个sku的status设置3
        // update tb_item set status = 3  where goods_id in ( ids )

//        TbItemExample example = new TbItemExample();
//        TbItemExample.Criteria criteria = example.createCriteria();
//        criteria.andGoodsIdIn(Arrays.asList(ids));


        List<TbItem> list = getItemListToSolr(ids);

        for(TbItem item : list){
            item.setStatus("3");
            itemMapper.updateByPrimaryKey(item);
        }


    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
//                criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
//                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
           /* if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
//                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
                criteria.andIsDeleteIsNull();// is_delete=1 被删除  is_delete is null 未删除
            }*/

            criteria.andIsDeleteIsNull();// is_delete=1 被删除  is_delete is null 未删除
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
