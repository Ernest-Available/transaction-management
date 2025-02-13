package org.ernest.transactionmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("TRANSACTION")  // 指定表名
public class Transaction {

    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;

    private Long amount;  // 以分为单位

    private String type;

    private String payer;

    private String payee;

    private String description;

    @TableField(fill = FieldFill.INSERT)  // 插入时自动填充
    private Date createDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时自动填充
    private Date updateDate;
}