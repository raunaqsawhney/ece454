namespace java ece454750s15a1

struct PerfCounters {

    // number of seconds since service startup
    1: i32 numSecondsUp,
    // total number of requests received by service handler
    2: i32 numRequestsReceived,
    // total number of requests completed by service handler
    3: i32 numRequestsCompleted
}

exception ServiceUnavailableException {
    1:string msg
}

service BEPassword {
    
    string hashPassword(1:string password, 2:i16 logRounds) throws (1:ServiceUnavailableException e),
    bool checkPassword(1:string password, 2:string hash) throws (1:ServiceUnavailableException e)
}

service FEPassword {

    string hashPassword(1:string password, 2:i16 logRounds) throws (1:ServiceUnavailableException e),
    bool checkPassword(1:string password, 2:string hash) throws (1:ServiceUnavailableException e)
}

service BEManagement {
    PerfCounters getPerfCounters(),
    list<string> getGroupMembers()
}

service FEManagement {
    PerfCounters getPerfCounters(),
    list<string> getGroupMembers(),
    bool joinCluster(1:string host, 2:i32 pport, 3:i32 mport, 4:i32 ncores, 5:i32 nodeType),
    list<string> getBEList(),
    list<string> getFEList()
}

