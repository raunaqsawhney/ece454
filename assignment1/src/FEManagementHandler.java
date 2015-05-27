import org.apache.thrift.TException;

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



}