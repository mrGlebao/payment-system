package handlers;

import io.undertow.server.HttpHandler;

public class TransferHandler implements HttpHandlerProvider {

    @Override
    public HttpHandler asHandler() {
        return exchange -> {
            throw new UnsupportedOperationException("Implement transfer logic");
        };
    }
}
