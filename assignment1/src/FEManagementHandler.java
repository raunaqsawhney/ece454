import org.apache.thrift.TException;

import java.lang.System;
import java.util.*;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

    private CopyOnWriteArrayList<BEServer.BENode> beList = null;
    private PerfCounter perfManager = null;
    private PerfCounter perfManager = new PerfCounter();


    public FEManagementHandler(CopyOnWriteArrayList<BEServer.BENode> beList, PerfCounter perfManager) {
        this.beList = beList;
        this.perfManager = perfManager;
    }

    public PerfCounters getPerfCounters() {

        perfCounter.numSecondsUp = System.currentTimeMillis() - perfManager.numSecondsUp;
        perfCounter.numRequestsReceived = perfManager.numRequestsReceived;
        perfCounter.numRequestsCompleted = perfManager.numRequestsCompleted;


        System.out.println("[FEManagementHandler] NUM SECONDS UP: " + perfCounter.numSecondsUp + " REQ REC: " + perfCounter.numRequestsReceived + " REQ COM: " + perfCounter.numRequestsCompleted);

        return perfCounter;

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
