package com.qf.aop;

import com.qf.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;

@Aspect
public class LoginAop {
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 环绕增强，判断当前的目标方法是否作用于登入环境
     * @return
     */
//    @Around("execution(* )")
    @Around("@annotation(IsLogin)")
    public  Object isLogin(ProceedingJoinPoint joinPoint){
        //object是作用于方法的返回值
        try {
//            System.out.println("在目标方法前执行、、、、、");
           //判断是否登入？？
            //1.获得请求中cookie的login中的login_token
            //1.1获得request
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            //2通过request获得cookie
            Cookie[] cookies = request.getCookies();
            String loginToken = null;

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login_token")) {
                    //登入凭证
                    loginToken = cookie.getValue();
                    break;
                }
            }
            System.out.println("获得cookie中的凭证："+loginToken);

            User user=null;
            //3.判断凭证是否为空
            if (loginToken!=null) {
                //1、说明有凭证，但不一定代表登入，通过凭证去查询redis


                //2、查询redis
               user= (User) redisTemplate.opsForValue().get(loginToken);

            }

            System.out.println("获得redis中的用户信息："+user);
            //3判断是否登入
            if (user == null) {
                //未登入
                //4、如果未登入，判断iSlogin注解上的mustlogin是否为true
                //获得@islogin//获得方法签名
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();//获得反射方法对象
                IsLogin isLogin = method.getAnnotation(IsLogin.class);
                //通过注解获得方法返回
                boolean flag = isLogin.mustLogin();
                //判断是否强制跳转强制舔砖到登入界面
                if (flag) {
                    //获得当前的路径
                    String returnUrl=request.getRequestURL().toString();
                    //获得当前请求的参数
                    //request.getQueryString()获得get请求的参数
                    //request.getParameterMap()；获得post请求参数
                    String params=request.getQueryString();//拿到请求？后面的参数字符串
                    returnUrl = returnUrl + "?" + params;
                    returnUrl = URLEncoder.encode(returnUrl, "utf-8");


                    //跳转到登入界面
                    return "redirect:http://localhost:8084/sso/toLogin?returnUrl="+returnUrl;
                }

            }
            //4、将user对象设置到目标方法的形参中
            //1)获得目标方法的原来的参数列表
            Object[] args = joinPoint.getArgs();//获得实参
            //2）从众多实际参数中获得一个类型为user的参数
            for (int i=0;i<args.length;i++) {
                if (args[i] != null&& args[i].getClass()==User.class) {
                    args[i]=user;
                    break;
                }
            }
            System.out.println("获得当前的新的参数列表："+ Arrays.toString(args));
            //执行的目标方法
            Object proceed=joinPoint.proceed(args);//调用addcart方法
            System.out.println("在目标方法之后执行、、、");
            return proceed;

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }









}
