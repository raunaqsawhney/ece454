import org.apache.thrift.TException;
import java.util.concurrent.*;


//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

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

    boolean joinCluster(String host, short pport, short mport, short ncores){




    }



}