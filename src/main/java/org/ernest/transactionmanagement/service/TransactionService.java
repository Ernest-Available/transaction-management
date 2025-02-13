package org.ernest.transactionmanagement.service;

import org.ernest.transactionmanagement.entity.Transaction;
import org.springframework.stereotype.Service;


@Service
public interface TransactionService {
    /*
    * 新增交易
    * */
    Transaction createTransaction(Transaction transaction);
}
