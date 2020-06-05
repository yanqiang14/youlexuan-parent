package com.offcn.sms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.offcn.entity.Result;
import com.offcn.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {


    @Value("${accessKeyId}")
    String accessKeyId ;

    @Value("${accessSecret}")
    String accessSecret ;

    @Value("${signName}")
    String signName;

    @Value("${templateCode}")
    String templateCode;

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public Result sendSmsCode(String phone) {

        String code  = (long) (Math.random() * 1000000)+"";

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");

        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "优乐选");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");

        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            String data = response.getData();

            Map map = JSON.parseObject(data);
            String returnCode = (String) map.get("Code");
            if(returnCode!=null && returnCode.equals("OK")){
                //phone - code ==》smsCodeHash
                redisTemplate.boundHashOps("smsCodeHash").put(phone,code);
                System.out.println(code);
                return  new Result(true,"发送成功，及时查收");
            }

        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }


        return new Result(false,"发送失败");
    }

}
