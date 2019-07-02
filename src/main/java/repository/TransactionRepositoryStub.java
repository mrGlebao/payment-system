package repository;

import domain.Transaction;
import exceptions.TransactionAlreadyExistException;

import java.util.HashMap;
import java.util.Map;

public class TransactionRepositoryStub implements TransactionRepository {

    private final Map<Long, Transaction> transactions = new HashMap<>();

    @Override
    public synchronized void addIfNotExist(Transaction transaction) throws TransactionAlreadyExistException {
        Long transactionId = transaction.getId();
        if (transactions.containsKey(transactionId)) {
            throw new TransactionAlreadyExistException(transactionId);
        }
        transactions.put(transactionId, transaction);
    }

}
