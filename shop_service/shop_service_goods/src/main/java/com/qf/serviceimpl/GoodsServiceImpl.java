package com.qf.serviceimpl;
import com.qf.serviceimpl.ISearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.shop_service_goods.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
@Service
public class GoodsServiceImpl implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Reference
    private ISearchService searchService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public List<Goods> queryAll() {
        return goodsMapper.selectList(null);
    }

    @Override
    public int insert(Goods goods) {
        //添加商品
        int result = goodsMapper.insert(goods);
        //通过dubbo调用搜索服务，同步索引库
//        int i = searchService.insertGoods(goods);
//        if (i > 0) {
//            System.out.println("同步到索引库中成功");
//        }
        //将添加的信息放入rabbitmq中
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.FANOUT_NAME,"",goods);



        return result;
    }

    @Override
    public Goods queryById(int gid) {
        return goodsMapper.selectById(gid);
    }
}
