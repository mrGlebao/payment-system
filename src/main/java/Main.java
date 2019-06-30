import handlers.TransferHandler;
import handlers.TransferIdHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

public class Main {

    private static final HttpHandler ROUTES = Handlers.routing()
            .get("/id", new TransferIdHandler().asHandler())
            .put("/transfer", new TransferHandler().asHandler());

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(ROUTES)
                .build();
        server.start();
    }

}
