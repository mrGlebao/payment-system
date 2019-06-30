package handlers;


import dto.PaymentIdDto;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import java.util.concurrent.atomic.AtomicLong;

public class TransferIdHandler implements HttpHandlerProvider {

    private static AtomicLong id = new AtomicLong(0);

    @Override
    public HttpHandler asHandler() {
        return exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(new PaymentIdDto(id.incrementAndGet()).toString());
        };
    }
}
