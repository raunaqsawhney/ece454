import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;
import  java.util.ArrayList;

//Generated code
import ece454750s15a1.*;

public class BEManagementHandler implements BEManagement.Iface {

    private PerfCounters perfManager = null;
    private PerfCounters perfCounter = new PerfCounters();
    private Long serviceUpTime;


    public BEManagementHandler(PerfCounters perfManager, Long serviceUpTime) {
        this.perfManager = perfManager;
    }

    public PerfCounters getPerfCounters() {

        perfCounter.numSecondsUp = (int)(System.currentTimeMillis() - serviceUpTime);
        perfCounter.numRequestsReceived = perfManager.numRequestsReceived;
        perfCounter.numRequestsCompleted = perfManager.numRequestsCompleted;

        return perfCounter;

    }

    public ArrayList<String> getGroupMembers() {
        ArrayList<String> groupMembersList = new ArrayList();

        groupMembersList.add("Raunaq Sawhney");
        groupMembersList.add("Thomas Chen");

        return groupMembersList;

    }
}
