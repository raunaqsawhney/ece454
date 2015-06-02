import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import java.util.*;
import java.util.concurrent.*;

// Generated code
import ece454750s15a1.*;

public class FEServer {

	public static class FESeed {
		
		public String host;
		public int mport;
	}

	public static FEPasswordHandler passwordHandler;
    public static FEPassword.Processor passwordProcessor;
	
    public static FEManagementHandler managementHandler;
    public static FEManagement.Processor managementProcessor;

	public static String host;
	public static int pport;
	public static int mport;
	public static int ncores;
	public static ArrayList<FEServer.FESeed> seedList;
	public static CopyOnWriteArrayList<BEServer.BENode> beList = new CopyOnWriteArrayList<BEServer.BENode>();
	
    public static void main(String[] args) {

		try {

			// Start parsing the CLI
			startup(args);

			boolean isFESeed = false;

			// Differentiate between FESeed and FEServer
			for (FEServer.FESeed feSeed : seedList) {
				if (host == feSeed.host && mport == feSeed.mport) {
					System.out.println("[FEServer] FE Node with host " + feSeed.host + " and mport " + feSeed.mport
						+ " is an FESeed");
				}
			}

			passwordHandler = new FEPasswordHandler(beList);
			passwordProcessor = new FEPassword.Processor(passwordHandler);

			managementHandler = new FEManagementHandler(seedList, beList);
			managementProcessor = new FEManagement.Processor(managementHandler);

			Runnable passwordPort = new Runnable() {
				public void run() {
					passwordPort(passwordProcessor);
				}
			};

			Runnable managementPort = new Runnable() {
				public void run() {
					managementPort(managementProcessor);
				}
			};

			new Thread(passwordPort).start();
			new Thread(managementPort).start();

		} catch (Exception x) {
			x.printStackTrace();
		}
    }

	public static void startup(String[] args) {
		try {
			seedList = new ArrayList<FEServer.FESeed>();

			for (int i = 0; i < args.length; i++){
				if (args[i].equals("-host")){
					i++;
					host = args[i];
				}else if (args[i].equals("-pport")){
					i++;
					pport = Integer.parseInt(args[i]);
				}else if (args[i].equals("-mport")){
					i++;
					mport = Integer.parseInt(args[i]);
				}else if (args[i].equals("-ncores")){
					i++;
					ncores = Integer.parseInt(args[i]);
				}else if (args[i].equals("-seeds")){
					i++;
					String tempSeedString[];
					String tempSeedsList[] = args[i].split(",");
					int numOfSeeds = tempSeedsList.length;

					for (int j = 0; j < numOfSeeds; j++){
						FEServer.FESeed tempSeed = new FEServer.FESeed();

						tempSeedString = tempSeedsList[j].split(":");
						tempSeed.host = tempSeedString[0];
						tempSeed.mport = Integer.parseInt(tempSeedString[1]);
						seedList.add(tempSeed);
					}
				}else{}
			}
		} catch (Exception x) {
			System.out.println("There is an issue with the CLI arguments");
			x.printStackTrace();
		}

		try {
			System.out.println("host: " + host);
			System.out.println("pport: " + pport);
			System.out.println("mport: " + mport);
			System.out.println("ncores: " + ncores);

			System.out.println();
			System.out.println("Seeds:");
			for (FEServer.FESeed seed : seedList){
				System.out.println(seed.host + " " + seed.mport);
			}

		} catch(Exception x){
			x.printStackTrace();
		}
	}

	public static void passwordPort(FEPassword.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(pport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor));

			System.out.println("[FEServer] Starting FE Password service on mport= " + pport);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void managementPort(FEManagement.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(mport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor));

			System.out.println("[FEServer] Starting FE Management service on mport= " + mport);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
