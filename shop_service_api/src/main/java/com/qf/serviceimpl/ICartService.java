package com.qf.serviceimpl;

import com.qf.entity.ShopCart;
import com.qf.entity.User;

import java.util.List;

public interface ICartService {

    int addCart(String cartToken,ShopCart shopCart, User user);

    List<ShopCart> queryCartsById(String cartToken,User user);



}
