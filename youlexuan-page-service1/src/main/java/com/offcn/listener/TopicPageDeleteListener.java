package com.offcn.listener;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component("topicPageDeleteListener")
public class TopicPageDeleteListener implements MessageListener {

    @Autowired
    ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            System.out.println("页面服务："+ Arrays.asList(ids));
            for(Long id : ids){
                itemPageService.deleteItemHtml(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
