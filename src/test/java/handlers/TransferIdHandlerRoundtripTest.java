package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.PaymentIdDto;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import repository.TransactionRepository;
import repository.TransactionRepositoryStub;
import repository.UserRepository;
import repository.UserRepositoryStub;
import server.UndertowServerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TransferIdHandlerRoundtripTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final UserRepository userRepository = new UserRepositoryStub();
    private static final TransactionRepository transactionRepository = new TransactionRepositoryStub();

    private static final Undertow server = UndertowServerFactory
            .constructServer(new TransferIdHandler(mapper), new TransferHandler(userRepository, transactionRepository, mapper));
    private static final Request request = new Request.Builder()
            .url("http://localhost:8080/id")
            .build();
    private final OkHttpClient client = new OkHttpClient();

    @BeforeClass
    public static void initServer() {
        server.start();
    }

    @AfterClass
    public static void shutdownServer() {
        server.stop();
    }

    @Test
    public void getId_typeIsJson() throws IOException {
        assertEquals("application/json", client.newCall(request).execute().header(Headers.CONTENT_TYPE.toString()));
    }

    @Test
    public void getId_statusIs200() throws IOException {
        assertEquals(200, client.newCall(request).execute().code());
    }

    @Test
    public void getId_idsAreSequential() throws IOException {
        PaymentIdDto dto1 = mapper.readValue(client.newCall(request).execute().body().string(), PaymentIdDto.class);
        PaymentIdDto dto2 = mapper.readValue(client.newCall(request).execute().body().string(), PaymentIdDto.class);
        assertEquals(String.valueOf(dto1.getId()), String.valueOf(dto2.getId() - 1));
    }

}
