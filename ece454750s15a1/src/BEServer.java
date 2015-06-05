import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
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
		
		@Override
		public int hashCode(){
			final int prime = 29;
			int result = 1;
			result = prime  * result + host.hashCode();
			result = prime * result + pport;
			result = prime * result + mport;
			result = prime * result + ncores;
			return result;
		}
		
		@Override
		public boolean equals(Object obj){
			if (this == obj){return true;}
			if (obj == null){return false;}
			if (getClass() != obj.getClass()){return false;}
			BEServer.BENode other = (BEServer.BENode) obj;
			if (!host.equals(other.host)){return false;}
			if (pport != other.pport){return false;}
			if (mport != other.mport){return false;}
			if (ncores != other.ncores){return false;}	
			return true;
		}
		
	}

    public static boolean connectToAllKnownSeeds = false;

    public static BEPasswordHandler passwordHandler;
    public static BEPassword.Processor passwordProcessor;

    public static BEManagementHandler managementHandler;
    public static BEManagement.Processor managementProcessor;

    public static String host;
	public static int pport;
	public static int mport;
	public static int ncores;

    public static Long serviceUpTime;

    public static ArrayList<FEServer.FESeed> seedList;
    public static PerfCounters perfManager = new PerfCounters();

    public static void main(String[] args) {
		
		try{
		    startup(args);
	    } catch(Exception x) {
            x.printStackTrace();
	    }

        try {
            //passwordHandler = new BEPasswordHandler(perfManager);
            //passwordProcessor = new BEPassword.Processor(passwordHandler);

            managementHandler = new BEManagementHandler(perfManager, serviceUpTime);
            managementProcessor = new BEManagement.Processor(managementHandler);
            
            Runnable openPasswordPort = new Runnable() {
                public void run() {
                    openPasswordPort();
                }
            };

            Runnable managementPort = new Runnable() {
                public void run() {
                    managementPort(managementProcessor);
                }
            };

            Runnable connectToSeed = new Runnable() {
                public void run() {
                    connectToSeed();
                }
            };

            new Thread(openPasswordPort).start();
            new Thread(managementPort).start();
            new Thread(connectToSeed).start();

            // Record time of when the service is started
            serviceUpTime = System.currentTimeMillis();

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

    public static void openPasswordPort() {
        try {
            passwordHandler = new BEPasswordHandler(perfManager);
            passwordProcessor = new BEPassword.Processor(passwordHandler);

            TNonblockingServerSocket socket =  new TNonblockingServerSocket(pport);
            THsHaServer.Args arg = new THsHaServer.Args(socket);
            arg.protocolFactory(new TBinaryProtocol.Factory());
            arg.transportFactory(new TFramedTransport.Factory());
            arg.processorFactory(new TProcessorFactory(passwordProcessor));
            arg.workerThreads(ncores);

            TServer server = new THsHaServer(arg);

            System.out.println("[BEServer] Started BE Password service on pport= " + pport);
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

					for (int j = 0; j < numOfSeeds; j++){
						FEServer.FESeed tempSeed = new FEServer.FESeed();

						tempSeedString = tempSeedsList[j].split(":");
						tempSeed.host = tempSeedString[0];
						tempSeed.mport = Integer.parseInt(tempSeedString[1]);
						seedList.add(tempSeed);
					}
				} else {}
			}
		} catch (Exception x) {
			System.out.println("There is an issue with the CLI arguments");
			x.printStackTrace();
		}
	}
    
    public static void connectToSeed() {

        while (!connectToAllKnownSeeds) {
            try {
                System.out.println("[BEServer] (" + host + "," + pport + "," + mport + "," + ncores + ")");
                System.out.println("[BEServer] Known seeds:");
                for (FEServer.FESeed seed : seedList){
                    System.out.println("BEServer] (" + seed.host + ":" + seed.mport + ")");
                }
        
                Random randGen = new Random();
                int randomSeedIndex = randGen.nextInt(seedList.size());

                TTransport transport;
                System.out.println("[BEServer] Random seed (" + seedList.get(randomSeedIndex).host + "," + seedList.get(randomSeedIndex).mport + ")");
                transport = new TSocket(seedList.get(randomSeedIndex).host, seedList.get(randomSeedIndex).mport);
                transport.open();
                
                connectToAllKnownSeeds = true;
                System.out.println("[BEServer] FESeed connection established");

                TProtocol protocol = new TBinaryProtocol(transport);
                FEManagement.Client client = new FEManagement.Client(protocol);

                // Join cluster with node type BE, 0 = FE, 1 = BE
                boolean result =  client.joinCluster(host, pport, mport, ncores, 1);
                
                if (!result) {
                    System.out.println("[BEServer] FE Unable to join cluster");
                } else {
                    System.out.println("[BEServer] Joined Cluster");
                }

                transport.close();

            } catch (Exception x){
                System.out.println("[BEServer] Waiting on FE Seed to connect...");
                try {
                     Thread.sleep(1000);
                } catch (Exception y) {
                    y.printStackTrace();
                }
            }
        }
    }
}
