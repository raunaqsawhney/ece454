import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

// Generated code
import ece454750s15a1.*;

public class BEServer {

    public static BEPasswordHandler handler;
    public static BEPassword.Processor processor;

    public static void main(String[] args) {
        try {
            handler = new BEPasswordHandler();
            processor = new BEPassword.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(BEPassword.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(2357);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor));

            System.out.println("Starting the ece454750s15a1 Simple Server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
