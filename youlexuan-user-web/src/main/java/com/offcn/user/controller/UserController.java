package com.offcn.user.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonResponse;
import com.offcn.sms.service.SmsService;
import com.offcn.util.PhoneFormatCheckUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbUser;
import com.offcn.user.service.UserService;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
/**
 * 用户表controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout = 10000)
	private UserService userService;

	@Reference(timeout = 10000)
	private SmsService smsService;

	@RequestMapping("/name")
	public Map showName(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map=new HashMap();
		map.put("username",name);

		return map;

	}

	@RequestMapping("/sendCode")
	public Result sendCode(String phone){

		//消息队列，异步的发送短息


		//判断手机号格式是否正确
		boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
		if(!phoneLegal){
			//手机格式不正确
			return  new Result(false,"手机号不正确");
		}

		Result result = smsService.sendSmsCode(phone);

		return result;
	}

	/**
	 * 新用户注册
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String code){
		//先来判断验证码是否正确
		if(!userService.checkCode(user.getPhone(),code)){
			return  new Result(false,"验证码不正确，请重新输入");
		}

		//校验用户名是否存在
		if(!userService.checkUserNameEnable(user.getUsername())){
			return  new Result(false,"用户名已经存在");
		}

		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	

	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
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
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
}
