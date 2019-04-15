package com.qf.listener;

import com.qf.entity.Goods;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQListener {
    @Autowired
    private SolrClient solrClient;

    @RabbitListener(queues = "goods_queue1")
    public void handleMsg(Goods goods){
        //接收到MQ的消息
        System.out.println("接收到mq的消息："+goods);
        SolrInputDocument document=new SolrInputDocument();
        document.setField("id",goods.getId());
        document.setField("goodsname",goods.getGoodsname());
        document.setField("goodsdesc",goods.getGoodsdesc());
        document.setField("goodsprice",goods.getGoodsprice().doubleValue());
        document.setField("goodspicture",goods.getGoodspicture());
        document.setField("sum",goods.getSum());
        try {
            solrClient.add(document);
            solrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
