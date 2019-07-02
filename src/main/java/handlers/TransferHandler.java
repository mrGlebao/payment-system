package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Transaction;
import domain.User;
import exceptions.HttpCodeException;
import exceptions.TransactionAlreadyExistException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
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
            Transaction transaction = readFromInputStream(exchange.getInputStream(), Transaction.class)
                    .filter(Transaction::isValid)
                    .orElseThrow(() -> new HttpCodeException(400, "Can't deserialize a valid transaction"));
            User sender = userRepository.getOne(transaction.getFrom())
                    .orElseThrow(() -> new HttpCodeException(404, "can't find user, id " + transaction.getFrom()));
            User receiver = userRepository.getOne(transaction.getTo())
                    .orElseThrow(() -> new HttpCodeException(404, "can't find user, id " + transaction.getTo()));
            try {
                transactionRepository.addIfNotExist(transaction);
            } catch (TransactionAlreadyExistException ex) {
                ex.printStackTrace();
                throw new HttpCodeException(200, ex.getMessage());
            }
            sender.transfer(receiver, transaction.getAmount());
            responseToClient(exchange, 200, "Success");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            if (ex instanceof HttpCodeException) {
                HttpCodeException coded = (HttpCodeException) ex;
                responseToClient(exchange, coded.getHttpCode(), coded.getMessage());
                return;
            }
            responseToClient(exchange, 500, "Something bad happened");
        }
    }

    private static void responseToClient(HttpServerExchange exchange,
                                         int statusCode,
                                         String message) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send(message);
    }

    private <T> Optional<T> readFromInputStream(InputStream s,
                                                Class<T> resultClass) {
        try {
            return Optional.ofNullable(mapper.readValue(s, resultClass));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
