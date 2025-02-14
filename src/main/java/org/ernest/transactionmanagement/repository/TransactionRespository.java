package org.ernest.transactionmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ernest.transactionmanagement.entity.Transaction;

@Mapper
public interface TransactionRespository extends BaseMapper<Transaction> {

    @Select("SELECT * FROM transaction order by UPDATE_DATE desc")
    Page<Transaction> selectPage(Page<Transaction> page);
}