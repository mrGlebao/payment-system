import com.fasterxml.jackson.databind.ObjectMapper;
import handlers.TransferHandler;
import handlers.TransferIdHandler;
import repository.TransactionRepositoryStub;
import repository.UserRepositoryStub;
import server.UndertowServerFactory;

public class Main {


    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        UndertowServerFactory
                .constructServer(new TransferIdHandler(mapper),
                        new TransferHandler(
                                new UserRepositoryStub(),
                                new TransactionRepositoryStub(),
                                mapper))
                .start();
    }

}
