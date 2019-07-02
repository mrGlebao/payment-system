package domain;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;

public class TransactionTest {

    private Transaction createTransaction(Long id,
                                          Long from,
                                          Long to,
                                          BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        return transaction;
    }

    @Test
    public void isValid_falseIfIdIsNull() {
        assertFalse(createTransaction(null, 2L, 3L, BigDecimal.TEN).isValid());
    }

    @Test
    public void isValid_falseIfFromIsNull() {
        assertFalse(createTransaction(1L, null, 3L, BigDecimal.TEN).isValid());
    }

    @Test
    public void isValid_falseIfToIsNull() {
        assertFalse(createTransaction(null, 2L, null, BigDecimal.TEN).isValid());
    }

    @Test
    public void isValid_falseIfFromEqualsTo() {
        assertFalse(createTransaction(1L, 2L, 2L, BigDecimal.TEN).isValid());
    }

    @Test
    public void isValid_falseIfAmountIsNull() {
        assertFalse(createTransaction(1L, 2L, 3L, null).isValid());
    }

    @Test
    public void isValid_falseIfAmountIsLessThan0() {
        assertFalse(createTransaction(1L, 2L, 3L, BigDecimal.TEN.negate()).isValid());
    }

    @Test
    public void isValid_trueIfEverythingIsOk() {
        assertFalse(createTransaction(1L, 2L, 3L, BigDecimal.TEN.negate()).isValid());
    }

}
