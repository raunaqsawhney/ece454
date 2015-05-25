import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

//Generated code
import ece454750s15a1.*;

public class A1PasswordHandler implements A1Password.Iface {
    
    public String hashPassword(String password, short logRounds) {
        System.out.println("BCrypt.hashpw(" + password + "," + logRounds + ")");

        // Default log_rounds = 10
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Hashed Password=" + hashed);
    
        return hashed;
    }

    public boolean checkPassword(String password, String hash) {
        System.out.println("BCrypt.checkpw(" + password + "," + hash + ")");

        boolean result = false;
        if (BCrypt.checkpw(password, hash)){
            System.out.println("Password Matches.");
            result = true;
        } else {
            System.out.println("Password does not match");
            result = false;
        }
        return result;
    }

}
