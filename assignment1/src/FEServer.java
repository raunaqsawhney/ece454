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

public class FEServer {

    public static Myservicehandler handler;
    public static Myservice.Processor processor;
    
    public static void main(String[] args) {
        try {
            handler = new Myserverhandler();
            processor = new Myservice.Processor(handler);

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

    public static void simple(Myservice.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(1357);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor));

            System.out.println("Starting the ece454750s15a1 server...");
            server.server();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
