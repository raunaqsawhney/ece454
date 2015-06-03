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

import java.lang.System;
import java.util.*;
import java.util.concurrent.*;

// Generated code
import ece454750s15a1.*;

public class FEServer {

	public static class FESeed {
		
		public String host;
		public int mport;
	}

	public static class FENode {

		public String host;
		public int pport;
		public int mport;
		public int ncores;
    }

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public static FEPasswordHandler passwordHandler;
    public static FEPassword.Processor passwordProcessor;
	
    public static FEManagementHandler managementHandler;
    public static FEManagement.Processor managementProcessor;

	public static String host;
	public static int pport;
	public static int mport;
	public static int ncores;

	public static Long serviceUpTime;

	public static PerfCounters perfManager = new PerfCounters();
	public static CopyOnWriteArrayList<BEServer.BENode> beList = new CopyOnWriteArrayList<BEServer.BENode>();
	public static CopyOnWriteArrayList<FEServer.FENode> feList = new CopyOnWriteArrayList<FEServer.FENode>();
	public static ArrayList<FEServer.FESeed> seedList;

	public static void main(String[] args) {
		try {

			// Start parsing the CLI
			startup(args);

			// Create service thread handlers
			passwordHandler = new FEPasswordHandler(beList, perfManager);
			passwordProcessor = new FEPassword.Processor(passwordHandler);

            managementHandler = new FEManagementHandler(beList, feList, perfManager, serviceUpTime);
			managementProcessor = new FEManagement.Processor(managementHandler);

			// Create service runnables
            Runnable passwordPort = new Runnable() {
                public void run() {
					passwordPort(passwordProcessor);
				}
			};

			final Runnable managementPort = new Runnable() {
				public void run() {
					managementPort(managementProcessor);
				}
			};

			final Runnable feSyncList = new Runnable() {
				public void run() {
					feSyncList();
				}
			};

			// Spawn service threads
			new Thread(managementPort).start();
			new Thread(passwordPort).start();
//			new Thread(feSyncList).start();

//			if (!isFESeed()) {
//				System.out.println("[FEServer] Starting FE Sync...");
//				new Thread(feSyncPort).start();
//			}

			// Record time of when the service is started

            executor.scheduleAtFixedRate(feSyncList, 0, 100, TimeUnit.MILLISECONDS);
            
            serviceUpTime = System.currentTimeMillis();

		//	openPasswordPort();

		} catch (Exception x) {
			x.printStackTrace();
		}
    }

	public static boolean isFESeed() {
		boolean isFESeed = false;

		// Differentiate between FESeed and FEServerc
		for (FEServer.FESeed feSeed : seedList) {
			if (host == feSeed.host && mport == feSeed.mport) {
				System.out.println("[FEServer] FE Node with host " + feSeed.host + " and mport " + feSeed.mport + " is an FESeed");
			}
		}

		return  isFESeed;
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
/*
		    int i = 0;
			while (i < seedList.size())
			{
				System.out.println("[FEServer] seedList.get(" + i + ").host = " + seedList.get(i).host
						+ " seedList.get(" + i + ").mport = " + seedList.get(i).mport);

				TTransport transport;
				transport = new TSocket(seedList.get(i).host, seedList.get(i).mport);
				transport.open();

				TProtocol protocol = new TBinaryProtocol(transport);
		        FEManagement.Client	client = new FEManagement.Client(protocol);

				System.out.println("[FEServer] host=" + host + " pport=" + pport + " mmport=" + mport + " ncores=" + ncores);

				// joinCluster: last argument is node type, 0 = FE, 1 = BE
				client.joinCluster(host, pport, mport, ncores, 0);
				System.out.println("[FEServer] Joined Cluster");

				transport.close();
				i++;
			}
*/
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

	public static void openPasswordPort() {
        try {
			passwordHandler = new FEPasswordHandler(beList,perfManager);
			passwordProcessor = new FEPassword.Processor(passwordHandler);
			
			TNonblockingServerSocket socket =  new TNonblockingServerSocket(pport);
            THsHaServer.Args arg = new THsHaServer.Args(socket);
			arg.protocolFactory(new TBinaryProtocol.Factory());
			arg.transportFactory(new TFramedTransport.Factory());
			arg.processorFactory(new TProcessorFactory(passwordProcessor));
			arg.workerThreads(5);

			TServer server = new THsHaServer(arg);
            server.serve();
			
			System.out.println("[FEServer] Starting FE Password service on mport= " + pport);

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

	public static  void feSyncList() {
	  
        try {
            
            System.out.println("[FEServer] Time = " + System.currentTimeMillis());
            System.out.println("[FEServer] Syncing FEServer host = " + host + " pport =  " + pport + " mport = " + mport + " ncores = " + ncores);

            Random randGen = new Random();
            int randomSeedIndex = randGen.nextInt(seedList.size());

            TTransport transport;
            transport = new TSocket(seedList.get(randomSeedIndex).host, seedList.get(randomSeedIndex).mport);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client = new FEManagement.Client(protocol);

            List<String> beSyncList = client.getBEList();
            List<String> feSyncList = client.getFEList();
    
            ArrayList<BEServer.BENode> beSyncArrayList = beListDecoder(beSyncList);
            ArrayList<FEServer.FENode> feSyncArrayList = feListDecoder(feSyncList);



            for (String beSyncListItem : beSyncList) {
                System.out.println("[FEServer] beSyncList BENode " + beSyncListItem);
            }   

            for (String feSyncListItem : feSyncList) {
                System.out.println("[FEServer] feSyncList FENode " + feSyncListItem);
            }

            transport.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
	
	public static ArrayList<FEServer.FENode> feListDecoder(List<String> list) {
		ArrayList<FEServer.FENode> tempList = new ArrayList<FEServer.FENode>();
		ArrayList<String> stringList = new ArrayList<String>(list);
		
		for (String n : stringList){
			String[] entryString = n.split(",");
			FEServer.FENode entry = new FEServer.FENode();
			entry.host = entryString[0];
			entry.pport = Integer.parseInt(entryString[1]);
			entry.mport = Integer.parseInt(entryString[2]);
			entry.ncores = Integer.parseInt(entryString[3]);
			
			tempList.add(entry);
		}
		
		return tempList;
	}
	
	public static ArrayList<BEServer.BENode> beListDecoder(List<String> list) {
		ArrayList<BEServer.BENode> tempList = new ArrayList<BEServer.BENode>();
		ArrayList<String> stringList = new ArrayList<String>(list);
		
		for (String n : stringList){
			String[] entryString = n.split(",");
			BEServer.BENode entry = new BEServer.BENode();
			entry.host = entryString[0];
			entry.pport = Integer.parseInt(entryString[1]);
			entry.mport = Integer.parseInt(entryString[2]);
			entry.ncores = Integer.parseInt(entryString[3]);
			
			tempList.add(entry);
		}
		
		return tempList;
	}
}
