package handlers;


import com.fasterxml.jackson.databind.ObjectMapper;
import dto.PaymentIdDto;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Обработчик для получения id при переводе средств
 * Id обеспечивает идемпотентность запроса, и, как следствие, возможность его безопасно ретраить.
 */
@RequiredArgsConstructor
public final class TransferIdHandler implements HttpHandlerProvider {

    /**
     * По-хорошему стоило сделать строковый id, но для наглядности я решил обойтись long'ом
     */
    private final AtomicLong id = new AtomicLong(0L);
    private final ObjectMapper mapper;

    @Override
    public final HttpHandler asHandler() {
        return exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(new PaymentIdDto(id.incrementAndGet())));
        };
    }
}
