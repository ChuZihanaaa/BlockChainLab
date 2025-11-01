package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.SimpleStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class SimpleStorageController {

    private final SimpleStorageService storageService;

    /**
     * Deploy a new SimpleStorage contract
     * @return the address of the deployed contract
     */
    @PostMapping("/deploy")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deployContract() {
        return storageService.deployContract()
                .thenApply(address -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("contractAddress", address);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Load an existing contract
     * @param address the address of the contract to load
     * @return a success message
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadContract(@RequestParam String address) {
        storageService.loadContract(address);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contract loaded successfully");
        response.put("contractAddress", address);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current value stored in the contract
     * @return the stored value
     */
    @GetMapping("/value/get")
    public CompletableFuture<ResponseEntity<Map<String, String>>> getValue() {
        return storageService.getValue()
                .thenApply(value -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("value", value.toString());
                    response.put("contractAddress", storageService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Set a new value in the contract
     * @param value the new value to store
     * @return the transaction receipt details
     */
    @PostMapping("/value/set")
    public CompletableFuture<ResponseEntity<Map<String, String>>> setValue(@RequestParam String value) {
        BigInteger intValue = new BigInteger(value);
        return storageService.setValue(intValue)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", storageService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Get the address of the currently loaded contract
     * @return the contract address
     */
    @GetMapping("/address")
    public ResponseEntity<Map<String, String>> getContractAddress() {
        String address = storageService.getContractAddress();
        Map<String, String> response = new HashMap<>();
        if (address != null) {
            response.put("contractAddress", address);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No contract loaded");
            return ResponseEntity.ok(response);
        }
    }
}