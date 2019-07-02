package repository;

import domain.Transaction;
import exceptions.TransactionAlreadyExistException;

public interface TransactionRepository {

    void addIfNotExist(Transaction transaction) throws TransactionAlreadyExistException;

}
