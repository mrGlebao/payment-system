package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Transaction;
import domain.User;
import exceptions.TransactionAlreadyExistException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import repository.TransactionRepository;
import repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TransferHandler implements HttpHandlerProvider {


    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ObjectMapper mapper;

    public TransferHandler(UserRepository userRepository,
                           TransactionRepository transactionRepository,
                           ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.mapper = mapper;
    }

    @Override
    public HttpHandler asHandler() {

        return new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) {
                if (exchange.isInIoThread()) {
                    exchange.dispatch(this);
                    return;
                }
                exchange.startBlocking();
                accept(exchange);
            }
        };
    }

    private void accept(HttpServerExchange exchange) {
        try {
            Transaction t = silentRead(exchange.getInputStream(), Transaction.class)
                    .filter(Transaction::isValid)
                    .orElseThrow(IllegalArgumentException::new);

            User sender = userRepository.getOne(t.getFrom())
                    .orElseThrow(IllegalArgumentException::new);
            User receiver = userRepository.getOne(t.getTo())
                    .orElseThrow(IllegalArgumentException::new);
            try {
                transactionRepository.addIfNotExist(t);
            } catch (TransactionAlreadyExistException ex) {
                System.out.println(ex.getMessage());
                responseOkToClient(exchange);
                return;
            }
            sender.transfer(receiver, t.getAmount());
            responseOkToClient(exchange);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            responseNotOkToClient(exchange, 400);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            responseNotOkToClient(exchange, 500);
        }
    }

    private static void responseOkToClient(HttpServerExchange exchange) {
        exchange.setStatusCode(200);
        exchange.getResponseSender().send("Ok");
    }

    private static void responseNotOkToClient(HttpServerExchange exchange,
                                              int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send("Not ok");
    }

    private <T> Optional<T> silentRead(InputStream s,
                                       Class<T> resultClass) {
        try {
            return Optional.ofNullable(mapper.readValue(s, resultClass));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
