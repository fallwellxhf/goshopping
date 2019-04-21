package comqf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.entity.Email;
import com.qf.entity.User;
import com.qf.serviceimpl.IUserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sso")
public class SsoController {
    @Reference
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;


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
       } else if (user.getStatus()==0) {
           //未激活
           model.addAttribute("error", "1");
          String mail= user.getEmail();
          int index=mail.indexOf("@");
          String tomail="http://mail."+mail.substring(index+1);

           model.addAttribute("toemail", tomail);
           return "login";
       }

       //如果没有设置登入成功的url，则默认跳转回首页
       if (returnUrl==null || "".equals(returnUrl)) {
           returnUrl = "http://localhost:8081/";
       }


        //用户信息存放到redis中,获得redis模板对象
       String token= UUID.randomUUID().toString();
       redisTemplate.opsForValue().set(token,user);
       //设置redis缓存失效时间
       redisTemplate.expire(token, 10, TimeUnit.DAYS);
       //将uuid回写到cookie中
       Cookie cookie=new Cookie("login_token",token);
       cookie.setMaxAge(60*60*24*10);
       cookie.setPath("/");
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
        //注册成功

        //发送激活邮件，怎么发送邮件，发送邮件内容是什么
        Email email = new Email();
        //设置发送目标
       email.setTo(user.getEmail());
       //设置发送方
        email.setSubject("锋玫官网激活邮件");
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("email_token"+user.getId(),uuid);
        redisTemplate.expire("email_token"+user.getUsername(),5,TimeUnit.MINUTES);

        String url = "http://localhost:8084/sso/jihuo?username="+user.getUsername()+"&token="+uuid;
        email.setContent("锋玫账号激活链接地址：<a href='"+url+"'>"+url+"</a>");
        email.setCreatetime(new Date());
        rabbitTemplate.convertAndSend("email_queue",email);
//         System.out.println("注册成功！");
        return "login";
    }

    /**
     * 激活请求
     * @return
     */
    @RequestMapping("/jihuo")
    public String jihuoUser(String username,String token){
        //验证token是否有效
        String redisToken = (String) redisTemplate.opsForValue().get("email_token_" + username);
        if (redisToken==null || !redisToken.equals(token)) {
            //激活失败
            return "jihuoeroor";
        }
        //认证成功
        userService.jihuoUser(username);

        //激活完成跳入登入地址
        return "redirect:/sso/toLogin";
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
