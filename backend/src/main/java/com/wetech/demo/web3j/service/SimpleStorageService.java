package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.simplestorage.SimpleStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleStorageService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    
    private SimpleStorage contract;
    /**
     * -- GETTER --
     *  Get the address of the currently loaded contract
     *
     * @return the contract address
     */
    @Getter
    private String contractAddress;

    /**
     * Deploy the SimpleStorage contract to the blockchain
     * @return the address of the deployed contract
     */
    public CompletableFuture<String> deployContract() {
        log.info("Deploying SimpleStorage contract...");
        return SimpleStorage.deploy(web3j, credentials, gasProvider)
                .sendAsync()
                .thenApply(contract -> {
                    this.contract = contract;
                    this.contractAddress = contract.getContractAddress();
                    log.info("SimpleStorage contract deployed to: {}", contractAddress);
                    return contractAddress;
                });
    }

    /**
     * Load an existing contract from the blockchain
     * @param contractAddress the address of the contract to load
     */
    public void loadContract(String contractAddress) {
        log.info("Loading SimpleStorage contract from address: {}", contractAddress);
        this.contract = SimpleStorage.load(contractAddress, web3j, credentials, gasProvider);
        this.contractAddress = contractAddress;
    }

    /**
     * Get the current value stored in the contract
     * @return the stored value
     */
    public CompletableFuture<BigInteger> getValue() {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Getting value from contract at address: {}", contractAddress);
        return contract.get().sendAsync();
    }

    /**
     * Set a new value in the contract
     * @param value the new value to store
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> setValue(BigInteger value) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Setting value {} in contract at address: {}", value, contractAddress);
        return contract.set(value).sendAsync();
    }
}