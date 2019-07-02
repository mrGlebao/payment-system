package exceptions;

public final class TransactionAlreadyExistException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Transaction id: %s already exists!";

    public TransactionAlreadyExistException(Long transactionId) {
        super(String.format(MESSAGE_FORMAT, transactionId));
    }

}
