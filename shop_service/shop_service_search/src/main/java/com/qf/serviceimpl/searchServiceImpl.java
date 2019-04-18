package com.qf.serviceimpl;
import com.alibaba.dubbo.config.annotation.Service;
import com.qf.entity.Goods;
import com.qf.serviceimpl.ISearchService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class searchServiceImpl implements  ISearchService{
    @Autowired
    private SolrClient solrClient;

    @Override
    public List<Goods> searchGoods(String keyword) {
        SolrQuery solrQuery=new SolrQuery();
        if (keyword == null || "".equals(keyword)) {
            //搜索所有
            solrQuery.setQuery("*:*");
        } else {
            //搜索具体关键字
            solrQuery.setQuery("goodsname:"+keyword+" || goodsdesc:"+keyword);
        }
        //开启高亮
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font");
        //添加高亮的字段
        solrQuery.addHighlightField("goodsname");
        List<Goods> list=new ArrayList<>();
        try {
            //获得结果
            QueryResponse result=solrClient.query(solrQuery);
            //获得高亮的结果
            //map<id,map<field,lust<string>>
            //field当前高亮的的字段
            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();
            SolrDocumentList results=result.getResults();
            for (SolrDocument document:results) {
                Goods goods = new Goods();
                goods.setId(Integer.parseInt(document.get("id")+""));
                goods.setGoodsname(document.get("goodsname")+"");
                goods.setGoodsprice(BigDecimal.valueOf(Double.parseDouble(document.get("goodsprice")+"")));
                goods.setGoodspicture(document.get("goodspicture")+"");
                goods.setSum(Integer.parseInt(document.get("sum")+""));
               //判断当前商品是否有高亮
                if (highlighting.containsKey(goods.getId()+"")) {
                    //有高亮的内容
                    Map<String, List<String>> stringListMap = highlighting.get(goods.getId() + "");
                    if (stringListMap.get("goodsname") != null) {
                        //将高亮的内容替换到对象中
                        String gname = stringListMap.get("goodsname").get(0);
                    System.out.println("高亮商品关键字："+gname);
                        goods.setGoodsname(gname);
                    }
                }
                list.add(goods);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(list);
        return list;
    }

    @Override
    public int insertGoods(Goods goods) {
        //将goods对象添加到索引库中
        //将goods转成document对象文件
        System.out.println("将goods对象添加到索引库中:"+goods);
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
            return 1;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}



