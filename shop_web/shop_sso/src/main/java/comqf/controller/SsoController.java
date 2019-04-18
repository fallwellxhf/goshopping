package comqf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.entity.User;
import com.qf.serviceimpl.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sso")
public class SsoController {
    @Reference
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 跳入到登入界面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(String returnUrl, Model model){
//        System.out.println("进来了、、、、、");
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }
   @RequestMapping("/login")
    public String login(String username, String password, Model model, HttpServletResponse response,String returnUrl){
//       System.out.println("登入进来了、、、");

       User user = userService.loginUser(username, password);
       if (user==null) {
           //登入失败
//           System.out.println("登入失败");
           model.addAttribute("error", "0");
           return "login";
       }

       //如果没有设置登入成功的url，则默认跳转回首页
       if (returnUrl==null) {
           returnUrl = "http://localhost:8084/";
       }


        //用户信息存放到redis中,获得redis模板对象
       String token= UUID.randomUUID().toString();
       redisTemplate.opsForValue().set(token,user);
       //设置redis缓存失效时间
       redisTemplate.expire(token, 10, TimeUnit.DAYS);
       //将uuid回写到cookie中
       Cookie cookie=new Cookie("login_token",token);
       cookie.setMaxAge(60*60*24*10);
       response.addCookie(cookie);
        //登入成功后跳入首页
       return "redirect:"+returnUrl;
    }

    /**
     * 判断当前用户是否登入
     */
    @RequestMapping("/islogin")
    @ResponseBody
    public String isLogin(@CookieValue(name = "login_token", required = false) String loginToken){
        //获取浏览器cookie中的login_token
//        System.out.println("获得浏览器发送过来的请求：" + loginToken);
       //判断浏览器是否登入
//        System.out.println("判断浏览器是否登入？？");
        //通过token去redis中验证是否登入
        User user =null;
        if (loginToken != null) {
            user = (User) redisTemplate.opsForValue().get(loginToken);
//            System.out.println("用户的取值6666："+user);
        }
//        System.out.println("用户的取值："+user);
        return user == null ? "islogin(null)" : "islogin('"+ JSON.toJSONString(user) +"')";
    }







    /**
     * 跳入到注册界面
     * @return
     */
    @RequestMapping("/toReginster")
    public String toReginster(){
        return "register";
    }

    /**
     * 注册用户
     * @param user
     * @param model
     * @return
     */
    @RequestMapping("/register")
    public String register(User user, Model model){
        //调用注册服务
        int resut = userService.insertUser(user);
        if (resut<=0) {
            model.addAttribute("error","0");
            return "register";
        }

//         System.out.println("注册成功！");

        return "redirect:http://localhost:8081";
    }

    /**
     *
    注销 * @return
     */
    @RequestMapping("/logout")
    public String logout(@CookieValue(name = "login_token", required = false)String loginToken,HttpServletResponse response){
        //清空redis
        redisTemplate.delete(loginToken);
        //请求cookie
        Cookie cookie = new Cookie("login_token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "login";
    }

















}
