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

import org.apache.thrift.TProcessorFactory;  
import org.apache.thrift.protocol.*; 
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;


// Generated code
import tutorial.*;

import java.util.HashMap;

public class JavaHsHaServer {

  public static MyserviceHandler handler;

  public static Myservice.Processor processor;

  public static void main(String [] args) {
    try {
      handler = new MyserviceHandler();
      processor = new Myservice.Processor(handler);

      TNonblockingServerSocket socket = new TNonblockingServerSocket(1357);  
      THsHaServer.Args arg = new THsHaServer.Args(socket); 
      arg.protocolFactory(new TBinaryProtocol.Factory());  
      arg.transportFactory(new TFramedTransport.Factory()); 
      arg.processorFactory(new TProcessorFactory(processor));  
      arg.workerThreads(5);

      TServer server = new THsHaServer(arg);  
      server.serve();  
      System.out.println("HsHa server started.");  
    } catch (TTransportException e) {  
      e.printStackTrace();  
    } catch (Exception e) {  
	e.printStackTrace();  
    }
  }
}
