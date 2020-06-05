package com.offcn.sms.service;


import com.offcn.entity.Result;

public interface SmsService {

    /**
     * 向手机号中发送一个6位的随机验证码
     * @param phone
     */
    public Result sendSmsCode(String phone);

}
