Thrift Java Tutorial
==================================================
1) Compile the Java library

    thrift/lib/java$ make
or:

    thrift/lib/java$ ant

4) Run the tutorial:

start server and client with one step:

    thrift/tutorial/tut1_src$ ant tutorial


or:

    thrift/tutorial/tut1_src$ ant tutorialserver
    thrift/tutorial/tut1_src$ ant tutorialclient
    thrift/tutorial/tut2_src$ ant HsHaserver
    thrift/tutorial/tut2_src$ ant Asyncclient
    thrift/tutorial/tut2_src$ ant ParallelClient
