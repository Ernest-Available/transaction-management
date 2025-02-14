package org.ernest.transactionmanagement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ernest.transactionmanagement.entity.Transaction;
import org.ernest.transactionmanagement.response.ResponseFactory;
import org.ernest.transactionmanagement.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction sampleTransaction;

    @BeforeEach
    public void setUp() {
        // 创建一个示例交易
        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setAmount(1000L);
        sampleTransaction.setType("PAYMENT");
        sampleTransaction.setPayer("Alice");
        sampleTransaction.setPayee("Bob");
        sampleTransaction.setDescription("Payment for services");
    }

    private Page<Transaction> createPageWithSampleTransaction(int page, int size) {
        // 创建 MyBatis-Plus 的 Page 对象
        Page<Transaction> pageResult = new Page<>(page, size);
        pageResult.setRecords(List.of(sampleTransaction)); // 设置分页数据
        pageResult.setTotal(1); // 设置总记录数
        return pageResult;
    }

    private String getJsonContent(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    public void testGetAllTransactions() throws Exception {
        // Setup
        Page<Transaction> pageResult = createPageWithSampleTransaction(0, 1);
        when(transactionService.getTransactions(any(Page.class)))
                .thenReturn(pageResult);

        // Run the test
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions")
                        .param("page", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the results
        String expectedJson = getJsonContent(ResponseFactory.success(pageResult));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testGetAllTransactions_NoItems() throws Exception {
        // Setup
        Page<Transaction> emptyPage = new Page<>(0, 1);
        emptyPage.setRecords(Collections.emptyList()); // 设置空数据
        emptyPage.setTotal(0); // 设置总记录数为 0

        when(transactionService.getTransactions(any(Page.class)))
                .thenReturn(emptyPage);

        // Run the test
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions")
                        .param("page", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse the actual and expected JSON
        JsonNode actualJson = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode expectedJson = objectMapper.readTree(getJsonContent(ResponseFactory.success(emptyPage)));

        // Compare the JSON nodes
        JsonNode actualData = actualJson.get("data");
        JsonNode expectedData = expectedJson.get("data");

        assertThat(actualJson.get("status")).isEqualTo(expectedJson.get("status"));
        assertThat(actualJson.get("message")).isEqualTo(expectedJson.get("message"));
        assertThat(actualData.get("records")).isEqualTo(expectedData.get("records"));
        assertThat(actualData.get("total")).isEqualTo(expectedData.get("total"));
        assertThat(actualData.get("size")).isEqualTo(expectedData.get("size"));
        assertThat(actualData.get("current")).isEqualTo(expectedData.get("current"));
        assertThat(actualData.get("pages")).isEqualTo(expectedData.get("pages"));
    }

    @Test
    public void testGetTransactionInfo() throws Exception {
        // Setup
        when(transactionService.getTransaction(1L)).thenReturn(sampleTransaction);

        // Run the test
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the results
        String expectedJson = getJsonContent(ResponseFactory.success(sampleTransaction));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testCreateTransaction() throws Exception {
        // Setup
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(sampleTransaction);

        // Run the test
        String jsonContent = getJsonContent(sampleTransaction);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the results
        String expectedJson = getJsonContent(ResponseFactory.success(sampleTransaction));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        // Setup
        when(transactionService.updateTransaction(eq(1L), any(Transaction.class))).thenReturn(sampleTransaction);

        // Run the test
        String jsonContent = getJsonContent(sampleTransaction);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions/1")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the results
        String expectedJson = getJsonContent(ResponseFactory.success(sampleTransaction));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        // Run the test
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the results
        String expectedJson = getJsonContent(ResponseFactory.success());
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }
}