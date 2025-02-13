package org.ernest.transactionmanagement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ernest.transactionmanagement.entity.Transaction;
import org.springframework.stereotype.Service;


@Service
public interface TransactionService {
    /*
     * 新增交易
     * */
    Transaction createTransaction(Transaction transaction);

    /*
     * 删除交易
     *
     * @param id
     * */
    void deleteTransaction(Long id);

    /*
     * 修改
     *
     * @param id
     * @patam Transaction 完整的
     * */
    Transaction updateTransaction(Long id, Transaction transaction);

    /*
    * id查询
    *
    * */
    Transaction getTransaction(Long id);

    Page<Transaction> getTransactions(Page<Transaction> page);
}
