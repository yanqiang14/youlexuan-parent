package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.CartVO;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    TbItemMapper itemMapper;

    @Override
    public List<CartVO> addGoodsToCartList(List<CartVO> cartList, Long itemId, Integer num) {

        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        String sellerId = item.getSellerId();

        CartVO cart = findCartFromCartList(cartList,sellerId);//找到一个购物车

        if(cart == null){//购物车列表中还没有当前商家的信息
            CartVO cartVO = new CartVO();//初始化

            cartVO.setSellerId(sellerId);
            cartVO.setSellerName(item.getSeller());

            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);

            cartVO.setOrderItemList(orderItemList);

            cartList.add(cartVO);

            return cartList;
        }else{
            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            TbOrderItem orderItem  = findOrderItem(orderItemList,itemId);

            if(orderItem == null){
                //说明这个商品以前从来没有添加到购物车中
                //新建一条购物明细
//                TbOrderItem tbOrderItem = new TbOrderItem();
                TbOrderItem tbOrderItem = createOrderItem(item, num);

                orderItemList.add(tbOrderItem);
                cart.setOrderItemList(orderItemList);
            }else{
                //说明以前添加过这个商品
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*orderItem.getNum()));

                //判断数量是否等于0
                if(orderItem.getNum()==0){
                    //orderItem
                    orderItemList.remove(orderItem);
                }

                if(orderItemList.size()==0){
                    // orderItemList
                    cartList.remove(cart);
                }

            }

        }


        return cartList;
    }

    private TbOrderItem findOrderItem(List<TbOrderItem> orderItemList, Long itemId) {

        for (TbOrderItem orderItem : orderItemList) {
            Long id = orderItem.getItemId();
            if(id.longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        
        return null;
    }


    private CartVO findCartFromCartList(List<CartVO> cartList,String sellerId){

        for (CartVO cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }

        return null;
    }

    /**
     * 新建购物明细
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());//单价
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
//        orderItem.setSellerId(item.getSeller());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }



}
