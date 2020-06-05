package com.offcn.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component("solrMessageListener")
public class SolrMessageListener implements MessageListener {

    @Autowired
    ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println("监听消息："+ text);
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            //将list集合导入solr中
            itemSearchService.importItemList(tbItems);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
