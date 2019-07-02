package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@AllArgsConstructor
public final class User {

    public final Long id;
    public final String name;
    private BigDecimal amount;

    public final void transfer(User user,
                               BigDecimal amount) {
        if (amount == null
                || amount.signum() < 0
                || this.amount.subtract(amount).signum() < 0) {
            throw new IllegalArgumentException("Wrong amount: " + amount);
        }
        this.amount = this.amount.subtract(amount);
        user.amount = user.amount.add(amount);
    }

}
