package handlers;

import io.undertow.server.HttpHandler;

/**
 * Адаптер, представляющий пользовательские обработчики в виде, используемом undertow
 */
public interface HttpHandlerProvider {

    HttpHandler asHandler();

}
