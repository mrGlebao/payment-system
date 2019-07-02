package handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Transaction;
import io.undertow.Undertow;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import repository.TransactionRepository;
import repository.TransactionRepositoryStub;
import repository.UserRepository;
import repository.UserRepositoryStub;
import server.UndertowServerProvider;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TransferHandlerTest {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final UserRepository userRepository = new UserRepositoryStub();
    private static final TransactionRepository transactionRepository = new TransactionRepositoryStub();
    private static final Undertow server = UndertowServerProvider
            .getServer(new TransferIdHandler(mapper),
                    new TransferHandler(userRepository, transactionRepository, mapper));

    private static Request request;


    static {
        try {
            Transaction t = new Transaction();
            t.setFrom(1L);
            t.setTo(2L);
            t.setId(1L);
            t.setAmount(BigDecimal.ONE);
            request = new Request.Builder()
                    .url("http://localhost:8080/transfer")
                    .put(RequestBody.create(mapper.writeValueAsBytes(t)))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void initServer() {
        server.start();
    }

    @AfterClass
    public static void shutdownServer() {
        server.stop();
    }

    @After


    @Test
    public void transfer_statusIs200() throws IOException {
        assertEquals(200, client.newCall(request).execute().code());
    }

}
