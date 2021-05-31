package lessons.lesson2;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthService {

    void start() throws SQLException;

    void stop();

    boolean createUser(String login, String pass, String nick) throws SQLException;

    boolean deleteUser(String login) throws SQLException;

    boolean updateUserInfo(String login, String field, String newValue) throws SQLException;

    Optional<String> getUserNick(String login, String pass);


}
