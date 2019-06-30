package repository;

import domain.Transaction;

public interface TransactionRepository {

    void add(Transaction transaction);

    boolean contains(Long id);

}
