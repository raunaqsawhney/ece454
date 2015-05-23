/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Generated code
import tutorial.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.io.IOException;  
import org.apache.thrift.TException; 
import org.apache.thrift.async.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class ParallelClient {

  static int counter = 5;
  static CountDownLatch latch = new CountDownLatch(counter);


  public static void main(String [] args) {

    if (args.length != 1 || !args[0].contains("simple")) {
      System.out.println("Please enter 'simple' ");
      System.exit(0);
    }

    try {

      for(int i = 0; i < 5; ++i){
         System.out.println("Send request i = " + i);
         new Thread() {
            public void run() {
               try {
                  TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
                  TAsyncClientManager clientManager = new TAsyncClientManager();
                  TNonblockingTransport transport = new TNonblockingSocket("localhost", 9090); 
                  Myservice.AsyncClient client = new Myservice.AsyncClient(
                      protocolFactory, clientManager, transport);
	          client.DelayAdd(100, 200, 3, new AddCallBack(latch, transport));
	       } catch (TException x) {
		       x.printStackTrace();
	       } catch (IOException e) {  
		       e.printStackTrace();
               }
            }
         }.start();
	 System.out.println("After Send request i = " + i);
      }
      boolean wait = latch.await(30, TimeUnit.SECONDS);
      System.out.println("latch.await =:" + wait);

      System.out.println("Exiting client.");

    } catch (InterruptedException e) {  
      e.printStackTrace();
    } 
  }

  static class AddCallBack 
    implements AsyncMethodCallback<Myservice.AsyncClient.DelayAdd_call> {

    private CountDownLatch latch;    
    private TNonblockingTransport transport;    

    public AddCallBack(CountDownLatch latch, TNonblockingTransport transp) {
        this.latch = latch;
        this.transport = transp;
    }
    public void onComplete(Myservice.AsyncClient.DelayAdd_call add_call) {
        try {
            long result = add_call.getResult();
            System.out.println("Add from server: " + result);
        } catch (TException e) {
            e.printStackTrace();
	} finally {
            transport.close();
            latch.countDown();
	}
    }
    
    public void onError(Exception e) {
        System.out.println("Error : ");
        e.printStackTrace();
	latch.countDown();
    }
  }    

}
