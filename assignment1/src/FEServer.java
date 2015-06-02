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

	public static FEPasswordHandler passwordHandler;
    public static FEPassword.Processor passwordProcessor;
	
    public static FEManagementHandler managementHandler;
    public static FEManagement.Processor managementProcessor;

	public static String host;
	public static int pport;
	public static int mport;
	public static int ncores;


    public static ArrayList<FEServer.FESeed> seedList;
	public static Long serviceUpTime;

	public static PerfCounters perfManager = new PerfCounters();
	public static CopyOnWriteArrayList<BEServer.BENode> beList = new CopyOnWriteArrayList<BEServer.BENode>();
	
    public static void main(String[] args) {

		try {

			// Start parsing the CLI
			startup(args);

//			boolean isFESeed = false;

//			// Differentiate between FESeed and FEServer
//			for (FEServer.FESeed feSeed : seedList) {
//				if (host == feSeed.host && mport == feSeed.mport) {
//					System.out.println("[FEServer] FE Node with host " + feSeed.host + " and mport " + feSeed.mport
//						+ " is an FESeed");
//				}
//			}

			passwordHandler = new FEPasswordHandler(beList, perfManager);
			passwordProcessor = new FEPassword.Processor(passwordHandler);

			managementHandler = new FEManagementHandler(beList, perfManager, serviceUpTime);
			managementProcessor = new FEManagement.Processor(managementHandler);

			Runnable managementPort = new Runnable() {
				public void run() {
					managementPort(managementProcessor);
				}
			};

			Runnable passwordPort = new Runnable() {
				public void run() {
					passwordPort(passwordProcessor);
				}
			};

			new Thread(managementPort).start();
			new Thread(passwordPort).start();

			// Record time of when the service is started
			serviceUpTime = System.currentTimeMillis();

			//openPasswordPort();

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

}
