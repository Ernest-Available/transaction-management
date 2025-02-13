package org.ernest.transactionmanagement.service.Impl;

import com.google.common.hash.BloomFilter;
import org.ernest.transactionmanagement.entity.Transaction;
import org.ernest.transactionmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransacServiceImpl implements TransactionService {

    @Autowired
    private BloomFilter<String> bloomFilter;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return null;
    }

//    @Override
//    public Transaction createTransaction(Transaction transaction) {
//        if (incidentRepository.existsByTitle(incident.getTitle())) {
//            throw new ServiceException("Title already exists: " + incident.getTitle());
//        }
//        Incident savedIncident = incidentRepository.save(incident);
//        // 更新布隆过滤器
//        bloomFilter.put(savedIncident.getId().toString());
//        return savedIncident;
//    }
}
