package lessons;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthService {

    void start();

    void stop();

    boolean createUser(String login, String pass, String nick);

    boolean deleteUser(String login);

    boolean updateUserInfo(String login, String field, String newValue);

    Optional<String> getUserNick(String login, String pass);


}
