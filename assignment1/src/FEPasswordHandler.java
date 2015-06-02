import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;


import org.mindrot.jbcrypt.BCrypt;
import java.util.*;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEPasswordHandler implements FEPassword.Iface {

	private CopyOnWriteArrayList<BEServer.BENode> beList = null;

	public FEPasswordHandler(CopyOnWriteArrayList<BEServer.BENode> beList) {
		this.beList = beList;
	}

    public String hashPassword(String password, short logRounds) {
	
	// TODO: Load balancing (Should be implemented as seperate function), retry connections, exception handling when no servers available
	
	String hashedPassword = null;
	Random rand = new Random();	
	int beServerIndex = rand.nextInt(beList.size());
	
	try {
            TTransport transport;
            transport = new TSocket(beList.get(beServerIndex).host, beList.get(beServerIndex).pport);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            BEPassword.Client client = new BEPassword.Client(protocol);

		System.out.println("[FEPasswordHandler] Password to HASH = " + password);

		hashedPassword = client.hashPassword(password, logRounds);

            transport.close();
    } catch (TException x) {
            x.printStackTrace();
    }

		System.out.println("[FEPasswordHandler] hashedPassword = " + hashedPassword);

		// Receive hashed password from BENode and return to client
        return hashedPassword;
    }

    public boolean checkPassword(String password, String hash) {
	
		// TODO: Load balancing , retry connections, exception handling when no servers available
		// Step 8 TODO: Smart connection pooling, reuse connections.

		boolean result = false;
		Random rand = new Random();	
		int beServerIndex = rand.nextInt(beList.size());
		
		try {
				TTransport transport;
				transport = new TSocket(beList.get(beServerIndex).host, beList.get(beServerIndex).pport);
				transport.open();

				TProtocol protocol = new TBinaryProtocol(transport);
				BEPassword.Client client = new BEPassword.Client(protocol);

			System.out.println("[FEPasswordHandler] Password to Check= " + password);

			result = client.checkPassword(password, hash);
				System.out.println("[FEPasswordHandler] checkPassword RESULT= " + result);

				transport.close();
		} catch (TException x) {
				x.printStackTrace();
		}

		// Received result of checkPassword from BENode and return to client
        return result;
    }

}
