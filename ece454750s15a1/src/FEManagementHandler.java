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

    public boolean joinCluster(String host, int pport, int mport, int ncores, int nodeType){

        boolean result = false;

        if (nodeType == 0) {
            // This is an FE Node trying to join the cluster

            FEServer.FENode feNode = new FEServer.FENode();
            feNode.host = host;
            feNode.pport = pport;
            feNode.mport = mport;
            feNode.ncores = ncores;

            if (feList.contains(feNode)) {
                System.out.println("[FEManagementHandler] FE Already Exists (" + feNode.host + "," + feNode.pport + "," + feNode.mport + "," + feNode.ncores + ")");
            } else {
                System.out.println("[FEManagementHandler] Added FE (" + feNode.host + "," + feNode.pport + "," + feNode.mport + "," + feNode.ncores + ")");
                return feList.add(feNode);
            }

        } else if (nodeType == 1) {
            // This a BE Node trying to join the cluster

            BEServer.BENode beNode = new BEServer.BENode();
            beNode.host = host;
            beNode.pport = pport;
            beNode.mport = mport;
            beNode.ncores = ncores;

            if (beList.contains(beNode)) {
                System.out.println("[FEManagementHandler] BE Already Exists (" + beNode.host + "," + beNode.pport + "," + beNode.mport + "," + beNode.ncores + ")");
            } else {
                System.out.println("[FEManagementHandler] Added BE (" + beNode.host + "," + beNode.pport + "," + beNode.mport + "," + beNode.ncores + ")");
                return beList.add(beNode);
            }
        }
        return false;
    }

    public ArrayList<String> getBEList() {

        ArrayList<String> stringBEList = new ArrayList<String>();

        for (BEServer.BENode beListItem : beList) {
            stringBEList.add(beListItem.host + "," + beListItem.pport + "," + beListItem.mport + "," + beListItem.ncores);
        }

        return stringBEList;
    }

    public ArrayList<String> getFEList() {

        ArrayList<String> stringFEList = new ArrayList<String>();

        for (FEServer.FENode feListItem : feList) {
            stringFEList.add(feListItem.host + "," + feListItem.pport + "," + feListItem.mport + "," + feListItem.ncores);
        }

        return stringFEList;
    }
}