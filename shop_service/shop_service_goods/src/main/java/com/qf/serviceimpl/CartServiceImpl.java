package com.qf.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.CartMapper;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.entity.ShopCart;
import com.qf.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import java.math.BigDecimal;
import java.util.List;
@Service
public class CartServiceImpl implements ICartService{
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
   private RedisTemplate redisTemplate;

    @Override
    public int addCart(String cartToken,ShopCart shopCart, User user) {
        //处理商品的小计
        Goods goods = goodsMapper.selectById(shopCart.getGid());
        BigDecimal number = BigDecimal.valueOf(shopCart.getGnumber());
        //计算当前商品的小计
        BigDecimal multiply = goods.getGoodsprice().multiply(number);
       shopCart.setAllprice(multiply);
        if (user != null) {
            //已经登入
            shopCart.setUid(user.getId());
            //添加到数据库
            cartMapper.insert(shopCart);
        } else {
            //未登入
            //进——。》远
            redisTemplate.opsForList().leftPush(cartToken,shopCart);
        }



        return 1;
    }

    /**
     *
     * 查询购物车数据
     * @param cartToken
     * @param user
     * @return
     */
    @Override
    public List<ShopCart> queryCartsById(String cartToken, User user) {
        List<ShopCart> shopCarts = null;
        if (user != null) {
            //去购物车
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("uid", user.getId());
            shopCarts=cartMapper.selectList(queryWrapper);
        } else if (cartToken != null){
                //去redis查询购物车信息
              //查询链表的长度
                Long size = redisTemplate.opsForList().size(cartToken);
                //获取链表的所有数据
            shopCarts= redisTemplate.opsForList().range(cartToken, 0, size);
        }

        //根据购物车的商品id查询商品的详细信息
        if (shopCarts!=null) {
            for (ShopCart shopCart : shopCarts) {
                //获得购物车对应的商品信息
                Goods goods = goodsMapper.selectById(shopCart.getGid());
                //设置到购物车的对象中
                shopCart.setGoods(goods);
            }
        }


        return shopCarts;
    }
}
