package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Пользователь
 */
@Getter
@ToString
@AllArgsConstructor
public final class User {

    public final Long id;
    public final String name;
    private BigDecimal amount;

    public final synchronized void transfer(User user,
                               BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Wrong amount: " + amount);
        }
        if (this.amount.subtract(amount).signum() < 0) {
            throw new IllegalArgumentException(String.format("User id %s has not enough money for transfer", this.id));
        }
        this.amount = this.amount.subtract(amount);
        user.amount = user.amount.add(amount);
    }

}
