import ece454750s15a1.*;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import java.util.*;

public class Client {
	
	private static String host;
	private static int pport;
	
    public static void main(String [] args) {
        if (args.length < 2) {
            System.exit(0);
        }else{
			host = args[0];
			pport = Integer.parseInt(args[1]);
		}

        try {
            TSocket socket = new TSocket(host, pport);
			TTransport transport = new TFramedTransport(socket);
			 
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEPassword.Client client = new FEPassword.Client(protocol);
            FEManagement.Client client_man = new FEManagement.Client(protocol);

            perform(client);
            //perform_man(client_man);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(FEPassword.Client client) throws TException  {
        
        String passwordHash = client.hashPassword("ThisIsThePassword", (short) 10);
        System.out.println("[Client] Password Hash =" + passwordHash);

        boolean checkPassword = client.checkPassword("ThisIsThePassword", passwordHash);
        System.out.println("[Client] Check Password (Should Pass) =" + checkPassword);

        checkPassword = client.checkPassword("ThisIsNotThePassword", passwordHash);
        System.out.println("[Client] Check Password (Should Fail) =" + checkPassword);

    }

    private static void perform_man(FEManagement.Client client_man) throws TException {
        PerfCounters perfCounter = new PerfCounters();
        perfCounter = client_man.getPerfCounters();

        System.out.println("[Client] NUM SECONDS UP: " + perfCounter.numSecondsUp + " REQ REC: " + perfCounter.numRequestsReceived + " REQ COM: " + perfCounter.numRequestsCompleted);

    }
}
