import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import java.util.*;

// Generated code
import ece454750s15a1.*;

public class BEServer {

    public static BEPasswordHandler handler;
    public static BEPassword.Processor processor;

	public static String host;
	public static int pport;
	public static int mport;
	public static int ncores;
	public static List<FEServer.FESeed> seedList;
	
    public static void main(String[] args) {
        
		try{
			startup(args);
		}catch(Exception x){
			x.printStackTrace();
		}
		
		try {
            handler = new BEPasswordHandler();
            processor = new BEPassword.Processor(handler);
/*
            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();*/
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(BEPassword.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(11357);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor));

            System.out.println("Starting the ece454750s15a1 Simple Server...");
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
			while (!seedList.empty())
			{
                TTransport transport;
                transport = new TSocket(seedList[i].host, seedList[i].mport);
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                FEPassword.Client client = new BEPassword.Client(protocol);

                client.joinCluster(host, pport, mport, ncores);

                transport.close();
                i++;
            }

		} catch(Exception x){
			x.printStackTrace();
		}
	}
}