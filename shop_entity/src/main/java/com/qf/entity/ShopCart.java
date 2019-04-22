package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopcart")
public class ShopCart implements Serializable {
    private int id;
//    @TableField("goodsid")
    private  int gid;
//    @TableField("userid")
    private int uid;
//    @TableField("number")
    private int gnumber;
//    @TableField("summoney")
    private BigDecimal allprice;
    private int status;
    private Date createtime;
    @TableField(exist = false)
    private Goods goods;
}
