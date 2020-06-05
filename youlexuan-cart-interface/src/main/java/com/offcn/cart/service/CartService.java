package com.offcn.cart.service;

import com.offcn.entity.CartVO;

import java.util.List;

public interface CartService {


    /**
     * 添加购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<CartVO> addGoodsToCartList(List<CartVO> cartList, Long itemId, Integer num);
}
