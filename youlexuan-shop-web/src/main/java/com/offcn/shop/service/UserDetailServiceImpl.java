package com.offcn.shop.service;

import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 利用UserDetailsServiceImpl从数据库中读取账号信息
 * UserDetailsService，接口，是security提供给我们的
 */
public class UserDetailServiceImpl implements UserDetailsService {

    /*  注入sellerService服务 */

    SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    //参数：你在登陆页面表单中输入的username（sellerId）
    @Override
    public UserDetails loadUserByUsername(String sellerId) throws UsernameNotFoundException {

        TbSeller seller = sellerService.findOne(sellerId);

        //status = 1 ，审核通过才可以登录

        if(seller==null){
            return null;//账号不存在,登录失败
        }

        if(!seller.getStatus().equals("1")){
            //没有被审核通过，不允许登录
            return null;
        }

        List<GrantedAuthority> list = new ArrayList<>();//角色权限集合
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        return new User(seller.getSellerId(),seller.getPassword(),list);//正确的账号信息，将要和登录表单中的信息进行校验，如果表单中的密码和seller.getPassword()一致，就登录成功

    }
}
