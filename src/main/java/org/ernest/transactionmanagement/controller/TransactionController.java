package org.ernest.transactionmanagement.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.ernest.transactionmanagement.entity.Transaction;
import org.ernest.transactionmanagement.response.Response;
import org.ernest.transactionmanagement.response.ResponseFactory;
import org.ernest.transactionmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

//    @GetMapping
//    public ResponseEntity<Response<Page<Transaction>>> getAllTransactions(@RequestParam(value = "title", required = false) String title,
//                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
//                                                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
//        return ResponseEntity.ok(ResponseFactory.getSuccessData(TransactionService.getPage(title, PageRequest.of(page, size,Sort.by("createdAt").descending()))));
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<Transaction> getTransactionInfo(@PathVariable(value = "id") Long id) {
//        return ResponseEntity.ok(ResponseFactory.getSuccessData(TransactionService.findById(id)));
//    }
//
    @PostMapping
    public Response<Transaction> createTransaction(@Validated @RequestBody Transaction transaction) {
        return ResponseFactory.success(transactionService.createTransaction(transaction));
    }

    @PutMapping("/{id}")
    public Response<Transaction> updateTransaction(@PathVariable(value = "id") Long id, @Valid @RequestBody Transaction transaction) {
        return ResponseFactory.success(transactionService.updateTransaction(id, transaction));
    }

    @DeleteMapping("/{id}")
    public Response<Response> deleteTransaction(@PathVariable(value = "id") Long id) {
        transactionService.deleteTransaction(id);
        return ResponseFactory.success();
    }

    @GetMapping("/{id}")
    public Response<Transaction> getTransaction(@PathVariable(value = "id") Long id) {
        return ResponseFactory.success(transactionService.getTransaction(id));
    }

    @GetMapping
    public Response<Page<Transaction>> getAllTransas(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ResponseFactory.success(transactionService.getTransactions(new Page<Transaction>(page, size)));
    }
}
