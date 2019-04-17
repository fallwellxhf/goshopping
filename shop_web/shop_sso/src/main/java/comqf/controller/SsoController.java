package comqf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.User;
import com.qf.serviceimpl.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sso")
public class SsoController {
    @Reference
    private IUserService userService;

    /**
     * 跳入到登入界面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 跳入到注册界面
     * @return
     */
    @RequestMapping("/toReginster")
    public String toReginster(){
        return "register";
    }

    @RequestMapping("/register")
    public String register(User user, Model model){
        //调用注册服务
        int resut = userService.insertUser(user);
        if (resut<=0) {
            model.addAttribute("error","0");
            return "register";
        }

         System.out.println("注册成功！");

        return "redirect:http://localhost:8081";
    }



















}
