import org.apache.thrift.TException;
import java.util.*;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

	private ArrayList<FEServer.FESeed> seedList;
    private CopyOnWriteArrayList<BEServer.BENode> beList = null;
    
    public FEManagementHandler(ArrayList<FEServer.FESeed> seedList, CopyOnWriteArrayList<BEServer.BENode> beList) {
        this.seedList = seedList;
		this.beList = beList;
    }

    public PerfCounters getPerfCounters() {

        PerfCounters testPerfCounters = new PerfCounters();

        return testPerfCounters;

    }

    public ArrayList<String> getGroupMembers() {
        ArrayList<String> groupMembersList = new ArrayList();

        groupMembersList.add("Raunaq Sawhney");
        groupMembersList.add("Thomas Chen");

        return groupMembersList;

    }

   public void joinCluster(String host, int pport, int mport, int ncores){
   
       BEServer.BENode beNode = new BEServer.BENode();  
       beNode.host = host;
       beNode.pport = pport;
       beNode.mport = mport;
       beNode.ncores = ncores;

       beList.add(beNode);

       System.out.println("[FEManagementHandler] Added BE to cluster");
   }
}
