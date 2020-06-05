package com.offcn.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;
import java.util.List;

@Component("solrDeleteMessageListener")
public class SolrDeleteMessageListener implements MessageListener {

    @Autowired
    ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;//接受到消息
        try {
            Long[] ids = (Long[])objectMessage.getObject();
            System.out.println("ids : " + ids);
            itemSearchService.removeSkuList(ids);//从solr中删除数据
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
