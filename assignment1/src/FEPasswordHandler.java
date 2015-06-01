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

            hashedPassword = client.hashPassword(password, logRounds);

            transport.close();
    } catch (TException x) {
            x.printStackTrace();
    }
	
        // Implement forwarding of request

        return hashedPassword;
    }

    public boolean checkPassword(String password, String hash) {
	
		// TODO: Load balancing , retry connections, exception handling when no servers available
	
		boolean result = false;
		Random rand = new Random();	
		int beServerIndex = rand.nextInt(beList.size());
		
		try {
				TTransport transport;
				transport = new TSocket(beList.get(beServerIndex).host, beList.get(beServerIndex).pport);
				transport.open();

				TProtocol protocol = new TBinaryProtocol(transport);
				BEPassword.Client client = new BEPassword.Client(protocol);

				result = client.checkPassword(password, hash);

				transport.close();
		} catch (TException x) {
				x.printStackTrace();
		}

        return result;
    }

}
