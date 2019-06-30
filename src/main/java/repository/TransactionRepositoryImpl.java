package repository;

import domain.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepositoryImpl implements TransactionRepository {

    private static final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public void add(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public boolean contains(Long id) {
        return transactions.containsKey(id);
    }
}
