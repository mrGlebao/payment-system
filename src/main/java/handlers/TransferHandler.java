package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Transaction;
import domain.User;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import repository.TransactionRepository;
import repository.TransactionRepositoryImpl;
import repository.UserRepository;
import repository.UserRepositoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

public class TransferHandler implements HttpHandlerProvider {


    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public HttpHandler asHandler() {

        return new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) {
                if (exchange.isInIoThread()) {
                    exchange.dispatch(this);
                    return;
                }
                exchangeConsumer.accept(exchange);
            }
        };
    }

    private final Consumer<HttpServerExchange> exchangeConsumer = exchange -> {
        try {
            exchange.startBlocking();
            Transaction t = silentRead(exchange.getInputStream(), Transaction.class)
                    .filter(Transaction::isValid)
                    .orElseThrow(IllegalArgumentException::new);
            if (transactionRepository.contains(t.getId())) {
                System.out.println("Transaction has already been processed");
                responseOkToClient(exchange);
                return;
            }
            User sender = userRepository.getOne(t.getFrom())
                    .orElseThrow(IllegalArgumentException::new);
            User receiver = userRepository.getOne(t.getTo())
                    .orElseThrow(IllegalArgumentException::new);
            transactionRepository.add(t);
            sender.transfer(receiver, t.getAmount());
            responseOkToClient(exchange);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            responseNotOkToClient(exchange, 400);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            responseNotOkToClient(exchange, 500);
        }
    };

    private static void responseOkToClient(HttpServerExchange exchange) {
        exchange.setStatusCode(200);
        exchange.getResponseSender().send("Ok");
    }

    private static void responseNotOkToClient(HttpServerExchange exchange,
                                              int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send("Not ok");
    }

    private static <T> Optional<T> silentRead(InputStream s,
                                              Class<T> resultClass) {
        try {
            return Optional.ofNullable(mapper.readValue(s, resultClass));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
