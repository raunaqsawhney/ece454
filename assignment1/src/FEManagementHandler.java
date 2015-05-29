import org.apache.thrift.TException;
import java.util.*;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

    private Map<String, String> beList = null;

    public FEManagementHandler() {
        beList = new ConcurrentHashMap<String, String>();
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

   public void joinCluster(String host, String pport, String mport, String ncores){
        beList.put("host", host);
        beList.put("pport",  String.valueOf(pport)); 
        beList.put("mport", String.valueOf(mport));
        beList.put("ncores", String.valueOf(ncores));
    }
}
