import org.apache.thrift.TException;

import java.lang.String;
import java.lang.System;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

public class FEManagementHandler implements FEManagement.Iface {

    private CopyOnWriteArrayList<BEServer.BENode> beList = null;
    private CopyOnWriteArrayList<FEServer.FENode> feList = null;

    private PerfCounters perfManager = null;
    private PerfCounters perfCounter = new PerfCounters();
    private Long serviceUpTime;

    public FEManagementHandler(CopyOnWriteArrayList<BEServer.BENode> beList,
                               CopyOnWriteArrayList<FEServer.FENode> feList,
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
           feNode.host = host;
           feNode.pport = pport;
           feNode.mport = mport;
           feNode.ncores = ncores;

           feList.add(feNode);
           System.out.println("[FEManagementHandler] Added FE to cluster");
       } else {

           // This a BE Node trying to join the cluster

           BEServer.BENode beNode = new BEServer.BENode();
           beNode.host = host;
           beNode.pport = pport;
           beNode.mport = mport;
           beNode.ncores = ncores;

           beList.add(beNode);
           System.out.println("[FEManagementHandler] Added BE to cluster");
       }
   }

    public ArrayList<String> getBEList() {

        ArrayList<String> stringBEList = new ArrayList<String>();

        System.out.println("[FEManagementHandler] BEList (getBEList)");
        for (BEServer.BENode beListItem : beList) {
            System.out.println("-host " + beListItem.host + " -pport  " + beListItem.pport + " -mport " + beListItem.mport + " -ncores " + beListItem.ncores);
            stringBEList.add("-host " + beListItem.host + " -pport  " + beListItem.pport + " -mport " + beListItem.mport + " -ncores " + beListItem.ncores);
        }

        return stringBEList;
    }

    public ArrayList<String> getFEList() {

        ArrayList<String> stringFEList = new ArrayList<String>();

        System.out.println("[FEManagementHandler] FEList (getFEList)");
        for (FEServer.FENode feListItem : feList) {
            System.out.println("-host " + feListItem.host + " -pport  " + feListItem.pport + " -mport " + feListItem.mport + " -ncores " + feListItem.ncores);
            stringFEList.add("-host " + feListItem.host + " -pport  " + feListItem.pport + " -mport " + feListItem.mport + " -ncores " + feListItem.ncores);
        }

        return stringFEList;
    }
}
