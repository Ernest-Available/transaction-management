package org.ernest.transactionmanagement.controller;


import jakarta.validation.Valid;
import org.ernest.transactionmanagement.entity.Transaction;
import org.ernest.transactionmanagement.exception.BusenessException;
import org.ernest.transactionmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

//    @GetMapping
//    public ResponseEntity<Response<Page<Transaction>>> getAllIncidents(@RequestParam(value = "title", required = false) String title,
//                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
//                                                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
//        return ResponseEntity.ok(ResponseFactory.getSuccessData(incidentService.getPage(title, PageRequest.of(page, size,Sort.by("createdAt").descending()))));
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<Transaction> getIncidentInfo(@PathVariable(value = "id") Long id) {
//        return ResponseEntity.ok(ResponseFactory.getSuccessData(incidentService.findById(id)));
//    }
//
    @PostMapping
    public ResponseEntity<Transaction> createIncident() {
        throw  new BusenessException("message----");
//        return ResponseEntity.ok(new Transaction());
    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Response> updateIncident(@PathVariable(value = "id") Long id, @Valid @RequestBody Incident incident) {
//        return ResponseEntity.ok(ResponseFactory.getSuccessData(incidentService.updateIncident(id, incident)));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Response> deleteIncident(@PathVariable(value = "id") Long id) {
//        incidentService.deleteIncident(id);
//        return ResponseEntity.ok(ResponseFactory.getSuccess());
//    }
}
