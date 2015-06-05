package ece454750s15a1;

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
import org.apache.thrift.transport.TNonblockingSocket;
public class Client {
	
	private static String host;
	private static int pport;
	private static int mport;

    public static void main(String [] args) {
        if (args.length < 2) {
            System.exit(0);
        }else{
			host = args[0];
			pport = Integer.parseInt(args[1]);
            mport = Integer.parseInt(args[2]);
		}

        try {
            TTransport passwordTransport;
            passwordTransport = new TSocket(host, pport);
            passwordTransport.open();

            TTransport managementTransport;
            managementTransport = new TSocket(host, mport);
            managementTransport.open();

            TProtocol passwordProtocol = new TBinaryProtocol(passwordTransport);
            FEPassword.Client passwordClient = new FEPassword.Client(passwordProtocol);

            TProtocol managementProtocol = new TBinaryProtocol(managementTransport);
            FEManagement.Client managementClient = new FEManagement.Client(managementProtocol);

            performPassword(passwordClient);
            performManagementGroup(managementClient);
            performManagement(managementClient);
           
            passwordTransport.close();
            managementTransport.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private static void performPassword(FEPassword.Client passwordClient) throws TException  {
        String passwordHash = passwordClient.hashPassword("ThisIsThePassword", (short) 10);
        boolean checkPassword = passwordClient.checkPassword("ThisIsThePassword", passwordHash);

        if (!checkPassword || passwordHash.isEmpty()) {
            System.out.println("[Client] Request not completed");
        } else {
            System.out.println("[Client] Request completed");
        }
    }

    public static void performManagementGroup(FEManagement.Client managementClient) throws TException {

        List<String> groupMembers = managementClient.getGroupMembers();
        for (String groupMember : groupMembers) {
            System.out.println(groupMember);
        }

    }
    private static void performManagement(FEManagement.Client managementClient) {
        try {
            PerfCounters perfCounter = new PerfCounters();
            perfCounter = managementClient.getPerfCounters();
            System.out.println("[Client] BE NUM SECONDS UP: " + perfCounter.numSecondsUp + " REQ REC: " + perfCounter.numRequestsReceived + " REQ COM: " + perfCounter.numRequestsCompleted);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
