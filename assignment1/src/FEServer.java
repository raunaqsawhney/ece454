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

    public static BEPasswordHandler handler;
    public static BEPassword.Processor processor;
    
    public static void main(String[] args) {
        try {

            managementPassword = new BEManagementHandler();
            managementProcessor = new BEManagement.Processor(managementHander);

            passwordHandler = new BEPasswordHandler();
            passwordProcessor = new BEPassword.Processor(passwordHandler);

            Runnable simpleManagement = new Runnable() {
                public void run() {
                    simpleManagement(managementProcessor);
                }
            };
            
            new Thread(simpleManagement).start();

            Runnable simplePassword = new Runnable() {
                public void run() {
                    simple(passwordProcessor);
                }
            };

            new Thread(simplePassword).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
     
    public static void simpleManagement(BEManagement.Processor managementProcessor) {
        try {
            TServerTransport serverTransport = new TServerSocket(1357);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(managementProcessor));

            System.out.println("Starting the ece454750s15a1 Management server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void simplePassword(BEManagement.Processor passwordProcessor) {
        try {
            TServerTransport serverTransport = new TServerSocket(1357);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(passwordProcessor));

            System.out.println("Starting the ece454750s15a1 Password server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
