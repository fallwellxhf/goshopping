package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.aop.IsLogin;
import com.qf.entity.ShopCart;
import com.qf.entity.User;
import com.qf.serviceimpl.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Reference
    private ICartService cartService;


    /**
     * 自定义注解—+aop
     * 添加购物车
     * @param shopCart
     * @return
     */

    @RequestMapping("/add")
    @IsLogin
    public String addCart(
            @CookieValue(name = "cart_token",required = false) String cartToken,
            ShopCart shopCart,
            User user,
            HttpServletResponse response){

        if (cartToken == null) {
            cartToken= UUID.randomUUID().toString();
        }
        cartService.addCart(cartToken, shopCart, user);

//        System.out.println("添加购物车");
        //判断当前用户是否登入？？
        //回写cookie
        Cookie cookie = new Cookie("cart_token", cartToken);
        cookie.setMaxAge(60*60*24*365);
        cookie.setPath("/");//设置cookie的有效路径
        response.addCookie(cookie);
        return "addsucc";
    }
    @RequestMapping("/list")
    @ResponseBody
    @IsLogin
    public String cartlist(User user,
         @CookieValue(name = "cart_token",required = false)String cartToken){

        List<ShopCart> shopCarts = cartService.queryCartsById(cartToken, user);

        return "showcartlist('"+ JSON.toJSONString(shopCarts) +"')";
    }












}
