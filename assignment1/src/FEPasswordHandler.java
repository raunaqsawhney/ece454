import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

//Generated code
import ece454750s15a1.*;

public class FEPasswordHandler implements FEPassword.Iface {

    public String hashPassword(String password, short logRounds) {

        // Implement forwarding of request

        return "hashed FROM BE";
    }

    public boolean checkPassword(String password, String hash) {


        // Implement forwarding of request

        return true;
    }

}
