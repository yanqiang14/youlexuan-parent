package com.offcn.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.CartVO;
import com.offcn.entity.Result;
import com.offcn.pojo.TbOrderItem;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 10000)
    CartService cartService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/findCartList")
    public List<CartVO> findCartList(){

        //先去判断是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取用户名
        //未登录情况  name = anonymousUser

        System.out.println("name = " + name);

        //无论你是否登录，都先读取cookie中的购物车
        String cartList = CookieUtil.getCookieValue(request, "cartList","utf-8");// [{},{},{}]
        if(cartList==null || cartList.length()==0){
            cartList = "[]";
        }
        List<CartVO> cookieCartList = JSON.parseArray(cartList, CartVO.class);


        if(name!=null && name.equals("anonymousUser")){
            //未登录，就不需要从redis中读取
            return cookieCartList;

        }else{
            //已经登录，把redis中以前的购物车读取出来，和cookie中的购物车合并

            // username(用户名)   List<CartVO>
            List<CartVO> redisCartList = (List<CartVO>) redisTemplate.boundHashOps("cartListHash").get(name);

            if(redisCartList==null){
                redisCartList = new ArrayList<>();
            }

            //合并
            List<CartVO> merge = merge(redisCartList, cookieCartList);
            //清除cookie
            CookieUtil.deleteCookie(request,response,"cartList");
            //重新存入redis
            redisTemplate.boundHashOps("cartListHash").put(name,merge);

            return merge;
        }


    }


    @RequestMapping("/addCart")
    public Result addGoodsToCartList(Long itemId, Integer num){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取用户名
        try {
            List<CartVO> cartList = findCartList();//从redis、或者cookie

            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if(name.equals("anonymousUser")){
                //未登录情况下，把购物车存在cookie中
                String string = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",string,3600,"utf-8");
            }else{
                //已经登录了，我们要将购物车数据存在redis中
                redisTemplate.boundHashOps("cartListHash").put(name,cartList);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }


    private List<CartVO> merge(List<CartVO> redisCartList,List<CartVO> cookieCartList){
        for (CartVO cartVO : cookieCartList) {
            List<TbOrderItem> orderItemList = cartVO.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList){
                Long itemId = orderItem.getItemId();
                Integer num = orderItem.getNum();
                redisCartList = cartService.addGoodsToCartList(redisCartList, itemId, num);
            }
        }
        return redisCartList;
    }

}
