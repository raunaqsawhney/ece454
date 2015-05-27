import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;
import  java.util.ArrayList;

//Generated code
import ece454750s15a1.*;

public class A1ManagementHandler implements A1Management.Iface {

    public PerfCounters getPerfCounters() {
    
    }

    public ArrayList<String> getGroupMembers() {
        ArrayList<String> groupMembersList = new ArrayList<>();

        groupMembersList.add("Raunaq Sawhney");
        groupMembersList.add("Thomas Chen");

        return groupMembersList;

    }
}
