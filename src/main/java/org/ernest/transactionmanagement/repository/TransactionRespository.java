package org.ernest.transactionmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ernest.transactionmanagement.entity.Transaction;

@Mapper
public interface TransactionRespository extends BaseMapper<Transaction> {
}