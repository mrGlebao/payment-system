package handlers;


import com.fasterxml.jackson.databind.ObjectMapper;
import dto.PaymentIdDto;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import java.util.concurrent.atomic.AtomicLong;

public class TransferIdHandler implements HttpHandlerProvider {

    private static AtomicLong id = new AtomicLong(0);

    private final ObjectMapper mapper;

    public TransferIdHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public HttpHandler asHandler() {
        return exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(new PaymentIdDto(id.incrementAndGet())));
        };
    }
}
