package handlers;


import com.fasterxml.jackson.databind.ObjectMapper;
import dto.PaymentIdDto;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Обработчик для получения id при переводе средств
 * Id обеспечивает идемпотентность запроса, и, как следствие, возможность его безопасно ретраить.
 */
public final class TransferIdHandler implements HttpHandlerProvider {

    /**
     * По-хорошему стоило сделать строковый id, но для наглядности я решил обойтись long'ом
     */
    private final AtomicLong id;
    private final ObjectMapper mapper;

    public TransferIdHandler(ObjectMapper mapper) {
        this.mapper = mapper;
        this.id = new AtomicLong(0L);
    }

    @Override
    public final HttpHandler asHandler() {
        return exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(new PaymentIdDto(id.incrementAndGet())));
        };
    }
}
