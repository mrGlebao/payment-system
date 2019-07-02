package handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Transaction;
import domain.User;
import exceptions.TransactionAlreadyExistException;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.RandomUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import repository.TransactionRepository;
import repository.UserRepository;
import server.UndertowServerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransferHandlerRoundtripTest {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static UserRepository userRepository = mock(UserRepository.class);

    private static TransactionRepository transactionRepository = mock(TransactionRepository.class);

    private static final Undertow server = UndertowServerProvider
            .getServer(new TransferIdHandler(mapper),
                    new TransferHandler(userRepository, transactionRepository, mapper));

    private Request request;
    private Transaction transaction;

    @Before
    public void initSuccessRoute() throws JsonProcessingException {
        Mockito.reset(userRepository, transactionRepository);
        transaction = new Transaction();
        transaction.setFrom(1L);
        transaction.setTo(2L);
        transaction.setId(3L);
        transaction.setAmount(BigDecimal.ONE);

        User from = new User(1L, "User from", BigDecimal.TEN);
        User to = new User(2L, "User to", BigDecimal.ONE);
        when(userRepository.getOne(1L)).thenReturn(Optional.of(from));
        when(userRepository.getOne(2L)).thenReturn(Optional.of(to));
        request = new Request.Builder()
                .url("http://localhost:8080/transfer")
                .put(RequestBody.create(mapper.writeValueAsBytes(transaction)))
                .build();
    }


    @BeforeClass
    public static void initServer() {
        server.start();
    }

    @AfterClass
    public static void shutdownServer() {
        server.stop();
    }

    @Test
    public void transfer_typeIsJson() throws IOException {
        assertEquals("application/json", client.newCall(request).execute().header(Headers.CONTENT_TYPE.toString()));
    }

    @Test
    public void transfer_onSuccessRouteReturn200() throws IOException {
        assertEquals(200, client.newCall(request).execute().code());
    }

    @Test
    public void transfer_ifTransactionNotValidReturn400() throws IOException {
        request = new Request.Builder()
                .url("http://localhost:8080/transfer")
                .put(RequestBody.create(mapper.writeValueAsBytes(new Transaction())))
                .build();
        assertEquals(400, client.newCall(request).execute().code());
        verify(transactionRepository, never()).addIfNotExist(transaction);
    }

    @Test
    public void transfer_ifFromIsMissingReturn404() throws IOException {
        when(userRepository.getOne(1L)).thenReturn(Optional.empty());
        assertEquals(404, client.newCall(request).execute().code());
        verify(transactionRepository, never()).addIfNotExist(any(Transaction.class));
    }

    @Test
    public void transfer_ifToIsMissingReturn404() throws IOException {
        when(userRepository.getOne(2L)).thenReturn(Optional.empty());
        assertEquals(404, client.newCall(request).execute().code());
        verify(transactionRepository, never()).addIfNotExist(any(Transaction.class));
    }

    @Test
    public void transfer_ifTransactionExistsReturn200() throws IOException {
        doThrow(new TransactionAlreadyExistException(1L)).when(transactionRepository).addIfNotExist(any(Transaction.class));
        assertEquals(200, client.newCall(request).execute().code());
        verify(transactionRepository).addIfNotExist(transaction);
    }

    @Test
    public void transfer_moneyTransferedIfEverythingIsOk() throws IOException {
        Mockito.reset(userRepository, transactionRepository);
        BigDecimal fromAmount = BigDecimal.valueOf(20.00);
        User from = new User(1L, "User from", fromAmount);
        BigDecimal toAmount = BigDecimal.valueOf(10.00);
        User to = new User(2L, "User to", toAmount);
        when(userRepository.getOne(1L)).thenReturn(Optional.of(from));
        when(userRepository.getOne(2L)).thenReturn(Optional.of(to));
        Transaction tr = new Transaction();
        tr.setFrom(1L);
        tr.setTo(2L);
        tr.setId(3L);
        tr.setAmount(BigDecimal.valueOf(RandomUtils.nextLong(0, 10)));
        request = new Request.Builder()
                .url("http://localhost:8080/transfer")
                .put(RequestBody.create(mapper.writeValueAsBytes(tr)))
                .build();

        client.newCall(request).execute();

        assertSame("Payer balance is incorrect", 0, tr.getAmount().compareTo(fromAmount.subtract(from.getAmount())));
        assertSame("Receiver balance is incorrect", 0, tr.getAmount().compareTo(to.getAmount().subtract(toAmount)));
    }

}
