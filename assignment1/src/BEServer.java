import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.util.*;

// Generated code
import ece454750s15a1.*;

public class BEServer {

	public static class BENode {
		
		public String host;
		public int pport;
        public int mport;
        public int ncores;
        public int numConnections;
	}

    public static BEPasswordHandler passwordHandler;
    public static BEPassword.Processor passwordProcessor;

    public static BEManagementHandler managementHandler;
    public static BEManagement.Processor managementProcessor;

    public static String host;
	public static int pport;
	public static int mport;
	public static int ncores;
	public static ArrayList<FEServer.FESeed> seedList;
    public static PerfCounters perfManager = new PerfCounters();

    public static void main(String[] args) {
		
		try{
		    startup(args);
	    } catch(Exception x) {
            x.printStackTrace();
	    }

        try {
            passwordHandler = new BEPasswordHandler(perfManager);
            passwordProcessor = new BEPassword.Processor(passwordHandler);

            managementHandler = new BEManagementHandler(perfManager);
            managementProcessor = new BEManagement.Processor(managementHandler);
            
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

            // Record time of when the service is started
            perfManager.numSecondsUp = System.currentTimeMillis();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void passwordPort(BEPassword.Processor passwordProcessor) {
        try {
            TServerTransport serverTransport = new TServerSocket(pport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(passwordProcessor));

            System.out.println("[BEServer] Starting BE Password service on mport= " + pport);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void managementPort(BEManagement.Processor mangementProcessor) {
        try {
            TServerTransport serverTransport = new TServerSocket(mport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(managementProcessor));

            System.out.println("[BEServer] Starting BE Management service on mport= " + pport);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
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
			        System.out.println(String.valueOf(numOfSeeds));

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
	
            int i = 0;
			while (i < seedList.size())
			{
                System.out.println("[BEServer] seedList.get(" + i + ").host = " + seedList.get(i).host
                        + " seedList.get(" + i + ").mport = " + seedList.get(i).mport);

                TTransport transport;
                transport = new TSocket(seedList.get(i).host, seedList.get(i).mport);
				transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                FEManagement.Client client = new FEManagement.Client(protocol);

                System.out.println("[BEServer] host=" + host + " pport=" + pport + " mmport=" + mport + " ncores=" + ncores);

                client.joinCluster(host, pport, mport, ncores);
                System.out.println("[BEServer] Joined Cluster");

                transport.close();
                i++;
            }

		} catch(Exception x){
			x.printStackTrace();
		}
	}
}
