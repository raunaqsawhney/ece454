import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

//Generated code
import ece454750s15a1.*;

public class BEPasswordHandler implements BEPassword.Iface {

    private PerfCounter perfCounter = new PerfCounter();

    public String hashPassword(String password, short logRounds) {

        // Default log_rounds = 10
        System.out.println("[BEPasswordHandler] Password to HASH=" + password);
        perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
        perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
        System.out.println("[BEPasswordHandler] Hashed Password=" + hashed);

        return hashed;
    }

    public boolean checkPassword(String password, String hash) {
        System.out.println("[BEPasswordHandler] Password to Check=" + password);

        boolean result = false;
        perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;

        if (BCrypt.checkpw(password, hash)){
            System.out.println("[BEPasswordHandler] Password Matches.");
            result = true;
        } else {
            System.out.println("[BEPasswordHandler] Password does not match");
            result = false;
        }
        perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;

        return result;
    }

}
