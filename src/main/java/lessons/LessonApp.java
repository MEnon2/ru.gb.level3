package lessons;

import java.sql.SQLException;

public class LessonApp {

    private static AuthService authService;

    public static void main(String[] args) throws SQLException {

        authService = new BaseAuthService();
        authService.start();
        authService.createUser("testUser1","passUser1", "nickUser1");
        authService.updateUserInfo("testUser1", "nick", "nickUser2");
        authService.deleteUser("testUser1");

        authService.stop();
    }


}
