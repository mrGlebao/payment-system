package domain;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertSame;

public class UserTest {

    private User from;
    private User to;

    @Before
    public void initUsers() {
        from = new User(1L, "From", BigDecimal.valueOf(100.00));
        to = new User(1L, "From", BigDecimal.valueOf(10.00));
    }

    @Test
    public void transfer_successRoute() {
        from.transfer(to, BigDecimal.TEN);
        assertSame("Payer balance is incorrect", 0, BigDecimal.valueOf(90.00).compareTo(from.getAmount()));
        assertSame("Receiver balance is incorrect", 0, BigDecimal.valueOf(20.00).compareTo(to.getAmount()));
    }

    @Test
    public void transfer_exceptionThrownIfAmountIsNull() {
        try {
            from.transfer(to, null);
        } catch (IllegalArgumentException ex) {
            assertSame("Payer balance is incorrect", 0, BigDecimal.valueOf(100.00).compareTo(from.getAmount()));
            assertSame("Receiver balance is incorrect", 0, BigDecimal.valueOf(10.00).compareTo(to.getAmount()));
            return;
        }
        throw new IllegalStateException("Must not reach this line");
    }

    @Test
    public void transfer_exceptionThrownIfAmountIsNegative() {
        try {
            from.transfer(to, BigDecimal.ONE.negate());
        } catch (IllegalArgumentException ex) {
            assertSame("Payer balance is incorrect", 0, BigDecimal.valueOf(100.00).compareTo(from.getAmount()));
            assertSame("Receiver balance is incorrect", 0, BigDecimal.valueOf(10.00).compareTo(to.getAmount()));
            return;
        }
        throw new IllegalStateException("Must not reach this line");
    }

    @Test
    public void transfer_exceptionThrownIfNotEnoughMoney() {
        try {
            from.transfer(to, BigDecimal.valueOf(200.00));
        } catch (IllegalArgumentException ex) {
            assertSame("Payer balance is incorrect", 0, BigDecimal.valueOf(100.00).compareTo(from.getAmount()));
            assertSame("Receiver balance is incorrect", 0, BigDecimal.valueOf(10.00).compareTo(to.getAmount()));
            return;
        }
        throw new IllegalStateException("Must not reach this line");
    }


}
