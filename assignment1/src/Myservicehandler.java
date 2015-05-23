import org.apache.thrift.TException;

//Generated code
import assignment1.*;

public class Myservicehandler implements Myservice.Iface {
    
    public string hashPassword(string password, int logRounds) {
        System.out.println("BCrypt.hashpw(" + password + "," + logRounds + ")");
    }

    public boolean checkPassword(string password, string hash) {
        System.out.println("BCrypt.checkpw(" + password + "," hash + ")");
    }
}
