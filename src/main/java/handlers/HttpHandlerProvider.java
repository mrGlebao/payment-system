package handlers;

import io.undertow.server.HttpHandler;

public interface HttpHandlerProvider {

    HttpHandler asHandler();

}
