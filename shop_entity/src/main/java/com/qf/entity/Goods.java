package com.qf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("goods")
public class Goods implements Serializable {
    @TableId(type= IdType.AUTO)//主键回填
    private int id;
    @TableField("goodsname")
    private String goodsname;
    private BigDecimal goodsprice;
    private int sum;
    private String goodsdesc;
    private String goodspicture;
    private int status;
    private Date createtime=new Date();
    private int belongtype;
}
