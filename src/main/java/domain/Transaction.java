package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

@Data
public final class Transaction {

    private Long id;
    private Long from;
    private Long to;
    private BigDecimal amount;

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
