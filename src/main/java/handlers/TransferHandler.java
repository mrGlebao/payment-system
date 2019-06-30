package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Transaction;
import domain.User;
import io.undertow.server.HttpHandler;
import repository.TransactionRepository;
import repository.TransactionRepositoryImpl;
import repository.UserRepository;
import repository.UserRepositoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TransferHandler implements HttpHandlerProvider {


    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public HttpHandler asHandler() {
//        if (exchange.isInIoThread()) {
//            exchange.dispatch(this);
//            return;
//        }
        return exchange -> {
            exchange.dispatch(() -> {
                try {
                    exchange.startBlocking();
                    Transaction t = silentRead(exchange.getInputStream(), Transaction.class)
                            .filter(Transaction::isValid)
                            .orElseThrow(IllegalArgumentException::new);
                    if (transactionRepository.contains(t.getId())) {
                        System.out.println("Transaction has already been processed");
                        return;
                    }
                    User sender = userRepository.getOne(t.getFrom())
                            .orElseThrow(IllegalArgumentException::new);
                    User receiver = userRepository.getOne(t.getTo())
                            .orElseThrow(IllegalArgumentException::new);
                    transactionRepository.add(t);
                    sender.transfer(receiver, t.getAmount());
                    exchange.getResponseSender().send("Ok");
                } catch (IllegalArgumentException ex) {
                    exchange.setStatusCode(400);
                    exchange.getResponseSender().send("Not ok");
                } catch (RuntimeException ex) {
                    exchange.setStatusCode(500);
                    exchange.getResponseSender().send("Not ok");
                }
            });
        };
    }

    private static <T> Optional<T> silentRead(InputStream s,
                                              Class<T> resultClass) {
        try {
            return Optional.ofNullable(mapper.readValue(s, resultClass));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
