package server;

import handlers.TransferHandler;
import handlers.TransferIdHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

public final class UndertowServerProvider {

    private static HttpHandler routes(TransferIdHandler idHandler,
                                      TransferHandler transferHandler) {
        return Handlers.routing()
                .get("/id", idHandler.asHandler())
                .put("/transfer", transferHandler.asHandler());
    }

    public static Undertow getServer(TransferIdHandler idHandler,
                                     TransferHandler transferHandler) {
        return Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(routes(idHandler, transferHandler))
                .build();
    }
}
