package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Транзакция
 */
@Data
public final class Transaction {

    private Long id;
    private Long from;
    private Long to;
    private BigDecimal amount;

    /**
     * Метод для валидации содержимого транзакции.
     * Для более обширных моделей данных было бы целесообразно выделить сервис-валидатор,
     * или использовать javax.validation, однако в данном случае достаточно перечислить правила
     * во внутреннем проверочном методе
     */
    @JsonIgnore
    public final boolean isValid() {
        return id != null
                && from != null
                && to != null
                && amount != null
                && amount.signum() != -1
                && !from.equals(to);
    }

}
