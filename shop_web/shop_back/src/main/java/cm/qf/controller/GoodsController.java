package cm.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.serviceimpl.IGoodsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private IGoodsService goodsService;
    @Value("${server.ip}")
    private  String serverip;
    /**
     * 查询商品列表
     * @return
     */
    @RequestMapping("/list")
    public String goodlist(Model model){
        List<Goods> goods=goodsService.queryAll();
//        System.out.println("获得商品有："+goods);
       model.addAttribute("goods",goods);
       model.addAttribute("serverip",serverip);
        return "goodslist";
    }

    /**
     * 商品上传
     * @param goods
     * @return
     */
    @RequestMapping("/insert")
    public String insert(Goods goods){
        System.out.println("添加商品的信息："+goods);
        goodsService.insert(goods);
        return "redirect:/goods/list";
    }

}
