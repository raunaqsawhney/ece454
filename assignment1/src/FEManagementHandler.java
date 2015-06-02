import org.apache.thrift.TException;

import java.lang.System;
import java.util.*;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

    private CopyOnWriteArrayList<BEServer.BENode> beList = null;
    private PerfCounters perfManager = null;
    private PerfCounters perfCounter = new PerfCounters();
    private Long serviceUpTime;


    public FEManagementHandler(CopyOnWriteArrayList<BEServer.BENode> beList, PerfCounters perfManager, Long serviceUpTime) {
        this.beList = beList;
        this.perfManager = perfManager;
    }

    public PerfCounters getPerfCounters() {

        perfCounter.numSecondsUp = (int)(System.currentTimeMillis() - serviceUpTime);
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
