package org.ernest.transactionmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HexFormat;

@Data
@TableName("TRANSACTION")  // 指定表名
public class Transaction {

    @TableId(type = IdType.ASSIGN_ID)  // 主键自增
    private Long id;

    @NotNull(message = "The Amount must not be empty.")
    @Min(value = 0, message = "Amount must not be less than zero.")
    @DecimalMax(value = "1000000000000000", message = "The amount cannot be greater than 1000000000000000.", inclusive = true)
    private Long amount;  // 以分为单位

    @NotBlank(message = "type cannot be blank")
    @Size(min = 3, max = 50, message = "type must be between 3 and 50 characters")
    private String type;

    @NotBlank(message = "payer cannot be blank")
    @Size(min = 3, max = 100, message = "payer must be between 3 and 100 characters")
    private String payer;

    @NotBlank(message = "payee cannot be blank")
    @Size(min = 3, max = 100, message = "payee must be between 3 and 100 characters")
    private String payee;

    @Size(max = 500,message = "Description must not exceed five hundred characters.")
    private String description;

    @TableField(fill = FieldFill.INSERT)  // 插入时自动填充
    private Date createDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时自动填充
    private Date updateDate;




    public String calculateMD5() throws Exception {
        String input = "Transaction{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", payer='" + payer + '\'' +
                ", payee='" + payee + '\'' +
                '}';
        MessageDigest md = MessageDigest.getInstance("MD5");
        return HexFormat.of().formatHex(md.digest(input.getBytes()));
    }
}