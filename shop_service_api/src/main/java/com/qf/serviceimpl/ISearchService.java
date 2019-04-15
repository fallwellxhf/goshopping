package com.qf.serviceimpl;

import com.qf.entity.Goods;

import java.util.List;

public interface ISearchService {
    /**
     * 搜索商品服务，搜索商品列表
     */
    List<Goods> searchGoods(String keyword);

    /**
     * 将商品信息添加同步到索引库中
     * @param goods
     * @return
     */
    int insertGoods(Goods goods);
}
