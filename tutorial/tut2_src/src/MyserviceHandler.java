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

import org.apache.thrift.TException;

// Generated code
import tutorial.*;

import java.util.HashMap;

public class MyserviceHandler implements Myservice.Iface {

  private HashMap<Integer,Item> map;

  public MyserviceHandler() {
    map = new HashMap<Integer, Item>();
  }

  public int add(int n1, int n2) {
    System.out.println("add(" + n1 + "," + n2 + ")");
    return n1 + n2;
  }

  public int DelayAdd(int n1, int n2, int delay) {
    System.out.println("Sleep " + delay + " seconds.");
    try{Thread.sleep(delay * 1000);}catch(InterruptedException e){System.out.println(e);}

    System.out.println("Delayadd(" + n1 + "," + n2 + ")");
    return n1 + n2;
  }

  public void putItem(Item item) {
    System.out.println("putItem key(" + item.key + ")");
    map.put(item.key, item);
  }

  public Item getItem(int key) {
    System.out.println("getItem(" + key + ")");
    return map.get(key);
  }
}

