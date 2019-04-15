package com.qf.shop_search;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.solr.common.SolrDocument;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopSearchApplicationTests {
    @Autowired
    private SolrClient solrClient;


    @Test
    public void add() {
        //创建document对想
        SolrInputDocument solrDocument=new SolrInputDocument();
       solrDocument.addField("id",3);
        solrDocument.addField("goodsname","huif空调");
        solrDocument.addField("goodspicture","http://www.baiud.com");
        solrDocument.addField("goodsdesc","商品大是大非详情");
        solrDocument.addField("goodsprice","5988.21");
        solrDocument.addField("sum","10020");

        try {
            solrClient.add(solrDocument);
            solrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void contextLoads() {
    }

    /**
     * 根据id删除
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void delete() throws IOException, SolrServerException {
        solrClient.deleteById("1");
        solrClient.commit();

    }
    @Test
    public void deleteByName() throws IOException, SolrServerException {
        solrClient.deleteByQuery("空调");//不要用这种方式，很危险
        solrClient.commit();

    }
    @Test
    public void query() throws IOException, SolrServerException {
        SolrQuery solrQuery=new SolrQuery();
        String keyword ="空调";
        solrQuery.setQuery("goodsname:"+keyword);
       //获得查询结果
        QueryResponse result = solrClient.query(solrQuery);
        SolrDocumentList documentList=result.getResults();
        for (SolrDocument d :documentList) {
            String id= (String) d.get("id");
            String goodsname= (String) d.get("goodsname");
            String goodsdesc= (String) d.get("goodsdesc");
            String goodspicture= (String) d.get("goodspicture");
            float goodsprice= (Float) d.get("goodsprice");
            int sum= (int) d.get("sum");
            System.out.println(id+"*8*****"+goodsprice+"*8*****"+goodsdesc+"*8*****"+goodsname);
        }
    }

}
