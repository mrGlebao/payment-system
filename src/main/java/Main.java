import handlers.TransferHandler;
import handlers.TransferIdHandler;
import server.UndertowServerProvider;

public class Main {


    public static void main(String[] args) {
        UndertowServerProvider
                .getServer(new TransferIdHandler(), new TransferHandler())
                .start();
    }

}
