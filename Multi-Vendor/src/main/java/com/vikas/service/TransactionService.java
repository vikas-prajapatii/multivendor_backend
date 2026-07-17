package com.vikas.service;

import com.vikas.model.Order;
import com.vikas.model.Seller;
import com.vikas.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Order order);
    List<Transaction> getTransactionBySellerId(Seller seller);

    List<Transaction> getAllTransaction();
}
