package cm.qf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/topage")
public class PageController {
    @RequestMapping("/{topage}")
    public String topage(@PathVariable("topage") String topage){
//        System.out.println("**"+topage);
        return topage;//可以经过视图解析器
    }



}
