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

import java.lang.Exception;
import java.lang.System;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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
			FEServer.FENode other = (FEServer.FENode) obj;
			if (!host.equals(other.host)){return false;}
			if (pport != other.pport){return false;}
			if (mport != other.mport){return false;}
			if (ncores != other.ncores){return false;}	
			return true;
		}
	
    }
    public static boolean connectToAllKnownSeeds = false;

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
	public static ArrayList<FESeed> seedList = new ArrayList<FESeed>();

    public static CopyOnWriteArrayList <BEServer.BENode> beSyncArrayList = new CopyOnWriteArrayList<BEServer.BENode>();
    public static CopyOnWriteArrayList <FEServer.FENode> feSyncArrayList = new CopyOnWriteArrayList<FEServer.FENode>();

	public static void main(String[] args) {
		try {

			// Start parsing the CLI
			startup(args);

			// Create service thread handlers
			//passwordHandler = new FEPasswordHandler(beList, perfManager);
			//passwordProcessor = new FEPassword.Processor(passwordHandler);

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

            final Runnable connectToSeed = new Runnable() {
                public void run() {
                    connectToSeed();
                }
            };

			final Runnable feSyncList = new Runnable() {
				public void run() {
					feSyncList();
				}
			};

			final Runnable checkForDeadBE = new Runnable() {
				public void run() {
					checkforDeadBE();
				}
			};

            final Runnable checkForDeadFE = new Runnable() {
                public void run() {
                    checkforDeadFE();
                }
            };

			// Spawn service threads
			new Thread(managementPort).start();
			//new Thread(passwordPort).start();
            new Thread(connectToSeed).start();

            executor.scheduleAtFixedRate(feSyncList, 0, 1, TimeUnit.SECONDS);
            executor.scheduleAtFixedRate(checkForDeadBE, 0, 500, TimeUnit.MILLISECONDS);
            executor.scheduleAtFixedRate(checkForDeadFE, 0, 500, TimeUnit.MILLISECONDS);

            serviceUpTime = System.currentTimeMillis();

			openPasswordPort();

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
	}

	public static void connectToSeed() {
        TTransport transport;
        int retryCount = 0;

        for (int i = 0; i < seedList.size(); i++) {
            try {
                System.out.println("[FEServer] (" + host + "," + pport + "," + mport + "," + ncores + ")");
                System.out.println("[FEServer] Known seeds:");
                for (FEServer.FESeed seed : seedList){
                    System.out.println("FEServer] (" + seed.host + ":" + seed.mport + ")");
                }

                System.out.println("[FEServer] Seed(" + i + ") " + seedList.get(i).host + "," + seedList.get(i).mport);
                transport = new TSocket(seedList.get(i).host, seedList.get(i).mport);
                transport.open();
                System.out.println("[FEServer] FESeed connection established");

                TProtocol protocol = new TBinaryProtocol(transport);
                FEManagement.Client client = new FEManagement.Client(protocol);

                // Join cluster with node type BE, 0 = FE, 1 = BE
                if (!client.joinCluster(host, pport, mport, ncores, 0)) {
                    System.out.println("[FEServer] FE Unable to join cluster");
                } else {
                    System.out.println("[FEServer] Joined Cluster");
                }

                transport.close();

            } catch (Exception x){
                if (retryCount < 3) {
                    System.out.println("[FEServer] Waiting on FE Seed to connect (" + retryCount + ")...");
                    try {
                        retryCount++;
                        i--;
                        Thread.sleep(1000);
                    } catch (Exception y) {
                        y.printStackTrace();
                    }
                }else{
                    retryCount = 0;
                }
            }
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
            System.out.println("[FEServer] Trying HsHa...");

            passwordHandler = new FEPasswordHandler(beList,perfManager);
			passwordProcessor = new FEPassword.Processor(passwordHandler);
			
			TNonblockingServerSocket socket =  new TNonblockingServerSocket(pport);
            System.out.println("[FEServer] Created socket....");

            THsHaServer.Args arg = new THsHaServer.Args(socket);
            System.out.println("[FEServer] Did args...");

            arg.protocolFactory(new TBinaryProtocol.Factory());
            System.out.println("[FEServer] Created pool factory (protocol)...");

            arg.transportFactory(new TFramedTransport.Factory());
            System.out.println("[FEServer] Created pool factory (transport)...");

            arg.processorFactory(new TProcessorFactory(passwordProcessor));
            System.out.println("[FEServer] Created pool factory (processor)...");

            arg.workerThreads(5);
            System.out.println("[FEServer] 5 worker threads...");


            TServer server = new THsHaServer(arg);

            System.out.println("[FEServer] HsHa Starting FE Password service on pport (B)= " + pport);

            server.serve();
			
			System.out.println("[FEServer] HsHa Starting FE Password service on pport (A)= " + pport);

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

	public static void feSyncList() {
	  
        try {
            
            Random randGen = new Random();
            int randomSeedIndex = randGen.nextInt(seedList.size());

            TTransport transport;
            transport = new TSocket(seedList.get(randomSeedIndex).host, seedList.get(randomSeedIndex).mport);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client = new FEManagement.Client(protocol);

            beSyncArrayList = beListDecoder(client.getBEList());
            feSyncArrayList = feListDecoder(client.getFEList());

//			HashSet<BEServer.BENode> beTempSet = new HashSet<BEServer.BENode>(beSyncArrayList);
//			beTempSet.addAll(beList);
//			beList = new CopyOnWriteArrayList<BEServer.BENode>(beTempSet);
//			for (BEServer.BENode temp : beTempSet){
//				numBE++;
//				System.out.println("[FEServer] FEList " + temp.host + ":" + temp.pport + ":" + temp.mport);
//			}
//
//
//			HashSet<FEServer.FENode> feTempSet = new HashSet<FEServer.FENode>(feSyncArrayList);
//			feTempSet.addAll(feList);
//			feList = new CopyOnWriteArrayList<FEServer.FENode>(feTempSet);
//			for (FEServer.FENode temp : feTempSet){
//				numFE++;
//				System.out.println("[FEServer] FEList " + temp.host + ":" + temp.pport + ":" + temp.mport);
//			}

            for (FEServer.FENode feSyncNode : feSyncArrayList) {
                if (!feList.contains(feSyncNode)) {
                    feList.add(feSyncNode);
                }
            }
            for (BEServer.BENode beSyncNode : beSyncArrayList) {
                if (!beList.contains(beSyncNode)) {
                    beList.add(beSyncNode);
                }
            }

            int numBE = beList.size();
            int numFE = feList.size();
			
            /*int numBE = 0;
            int numFE = 0;
            for (String beSyncListItem : beSyncList) {
                numBE++;
                System.out.println("[FEServer] BESyncList BENode: (" + beSyncListItem + ")");
            }   
            System.out.println("[FEServer]");
            for (String feSyncListItem : feSyncList) {
                numFE++;
                System.out.println("[FEServer] FESyncList FENode: (" + feSyncListItem + ")");
            }*/
            System.out.println("[FEServer] numBE: " + numBE + "---- numFE: " + numFE);

            transport.close();
        } catch (Exception x) {
            //x.printStackTrace();
        }
    }

	public static void checkforDeadBE() {

		int index = 0;

		try {
			TTransport transport;

			for (BEServer.BENode beNode : beList) {
				transport = new TSocket(beNode.host, beNode.pport);
				transport.open();
				transport.close();

				index++;
			}
		} catch (Exception x) {
		    System.out.println("[FEServer] BE REMOVED: " + beList.get(index).host + "," + beList.get(index).pport + "," + beList.get(index).mport + "," + beList.get(index).ncores);
            beList.remove(index);
        }
	}

    public static void checkforDeadFE() {

        int index = 0;

        try {
            TTransport transport;

            for (FEServer.FENode feNode : feList) {
                transport = new TSocket(feNode.host, feNode.pport);
                transport.open();
                transport.close();

                index++;
            }
        } catch (Exception x) {
            System.out.println("[FEServer] FE REMOVED: " + feList.get(index).host + "," + feList.get(index).pport + "," + feList.get(index).mport + "," + feList.get(index).ncores);
            feList.remove(index);
        }
    }
	
	public static CopyOnWriteArrayList<FEServer.FENode> feListDecoder(List<String> list) {

        CopyOnWriteArrayList<FEServer.FENode> tempList = new CopyOnWriteArrayList<FEServer.FENode>();
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
	
	public static CopyOnWriteArrayList<BEServer.BENode> beListDecoder(List<String> list) {

        CopyOnWriteArrayList<BEServer.BENode> tempList = new CopyOnWriteArrayList<BEServer.BENode>();
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

