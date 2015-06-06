package ece454750s15a1;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import org.mindrot.jbcrypt.BCrypt;

import java.lang.Exception;
import java.lang.System;
import java.util.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.*;

//Generated code
import ece454750s15a1.*;

import javax.naming.ServiceUnavailableException;


public class FEPasswordHandler implements FEPassword.Iface {

	private CopyOnWriteArrayList<BEServer.BENode> beList = null;
	private PerfCounters perfCounter = new PerfCounters();

	public FEPasswordHandler(CopyOnWriteArrayList<BEServer.BENode> beList, PerfCounters perfCounter) {
		this.beList = beList;
		this.perfCounter = perfCounter;
	}

	public int balanceLoad() {

		Random randGen = new Random();
		int totalCurrentWeight = 0;
		int randomBENodeIndex = 0;

		randGen.setSeed(System.currentTimeMillis());

		// Step 1: Determine the total weights of all known BE Servers
		for (int i = 0; i < beList.size(); i++) {
			totalCurrentWeight += beList.get(i).ncores;
		}

		System.out.println("[FEPasswordHandler] TotalCurrentWeight = " + totalCurrentWeight);
		randomBENodeIndex = randGen.nextInt(totalCurrentWeight);
		System.out.println("[FEPasswordHandler] randomBENodeIndex = " + randomBENodeIndex);

		for (int i = 0; i < beList.size(); i++) {
			// Returns index of BEServer in BEList to use for request
			if (randomBENodeIndex < beList.get(i).ncores) return i;
			randomBENodeIndex -= beList.get(i).ncores;
		}
		return -1;
	}

    public String hashPassword(String password, short logRounds) {

        String hashedPassword = null;
		boolean requestServiced = false;

        perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;
        if (beList.isEmpty()) {
          // throw new ServiceUnavailableException("Unable to process request, no BEs available");
        } else {
			while(!beList.isEmpty() && requestServiced == false) 
			{
				try {
					Random rand = new Random();
					int beServerIndex = balanceLoad();
					System.out.println("[FEPasswordHandler] beServerIndex = " + beServerIndex);

					TTransport transport;
					transport = new TSocket(beList.get(beServerIndex).host, beList.get(beServerIndex).pport);
					transport.open();

					TProtocol protocol = new TBinaryProtocol(transport);
					BEPassword.Client client = new BEPassword.Client(protocol);

					System.out.println("[FEPasswordHandler] Password to HASH = " + password);

					hashedPassword = client.hashPassword(password, logRounds);
					perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
					requestServiced = true;
					
                    System.out.println("[FEPasswordHandler] hashedPassword = " + hashedPassword);
					transport.close();
				} catch (Exception x) {
					System.out.println("[FEPasswordHandler] BE could not complete hash request, retrying with different BE...");
					try {
						Thread.sleep(500);
					} catch (Exception y) {
						y.printStackTrace();
					}
				}
			}
			if (beList.isEmpty() && requestServiced == false) {
				// throw new ServiceUnavailableException("Unable to process request, no BEs available");
			}
        }
        return hashedPassword;
    }

    public boolean checkPassword(String password, String hash) {

        boolean result = false;
		boolean requestServiced = false;
		
        perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;
        if (beList.isEmpty()) {
		//	throw new ServiceUnavailableException ("Unable to process request, no BEs available");
        } else {
			while(!beList.isEmpty() && requestServiced == false)
			{
				try {
					Random rand = new Random();
					int beServerIndex = balanceLoad();
					System.out.println("[FEPasswordHandler] beServerIndex = " + beServerIndex);

					TTransport transport;
					transport = new TSocket(beList.get(beServerIndex).host, beList.get(beServerIndex).pport);
					transport.open();

					TProtocol protocol = new TBinaryProtocol(transport);
					BEPassword.Client client = new BEPassword.Client(protocol);

					System.out.println("[FEPasswordHandler] Password to Check= " + password);

					result = client.checkPassword(password, hash);
					perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
					requestServiced = true;
					
                    System.out.println("[FEPasswordHandler] checkPassword Result= " + result);
					transport.close();
				} catch (Exception x) {

					System.out.println("[FEPasswordHandler] BE could not complete check request, retrying with different BE...");
					try {
						Thread.sleep(500);
					} catch (Exception y) {
						y.printStackTrace();
					}
				}
			}
			if (beList.isEmpty() && requestServiced == false) {
				// throw new ServiceUnavailableException("Unable to process request, no BEs available");
			}			
        }
        return result;
    }
}
