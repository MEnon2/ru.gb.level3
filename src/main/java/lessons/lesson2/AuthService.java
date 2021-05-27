package lessons.lesson2;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthService {

    void start() throws SQLException;

    void stop() throws SQLException;

    boolean createUser(String login, String pass, String nick) throws SQLException;

    boolean removeUser(String login, String pass) throws SQLException;

    Optional<String> getNickFromLoginAndPass(String login, String pass);


}
