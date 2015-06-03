import org.apache.thrift.TException;

import java.lang.System;
import java.util.*;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

    private CopyOnWriteArrayList<BEServer.BENode> beList = null;
    private CopyOnWriteArrayList<FEServer.FENode> feList = null;

    private PerfCounters perfManager = null;
    private PerfCounters perfCounter = new PerfCounters();
    private Long serviceUpTime;

    public FEManagementHandler(CopyOnWriteArrayList<BEServer.BENode> beList, CopyOnWriteArrayList<FEServer.FENode> feList,
                               PerfCounters perfManager, Long serviceUpTime) {
        this.beList = beList;
        this.feList = feList;

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

   public void joinCluster(String host, int pport, int mport, int ncores, int nodeType){

       if (nodeType == 0) {
           // This is an FE Node trying to join the cluster

           FEServer.FENode feNode = new FEServer.FENode();
           beNode.host = host;
           beNode.pport = pport;
           beNode.mport = mport;
           beNode.ncores = ncores;

           feList.add(feNode);
           System.out.println("[FEManagementHandler] Added FE to cluster");
       } else {
           BEServer.BENode beNode = new BEServer.BENode();
           beNode.host = host;
           beNode.pport = pport;
           beNode.mport = mport;
           beNode.ncores = ncores;

           beList.add(beNode);
           System.out.println("[FEManagementHandler] Added BE to cluster");
       }
   }
}
