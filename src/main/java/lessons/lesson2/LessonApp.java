package lessons.lesson2;

import java.sql.SQLException;

public class LessonApp {

    private static AuthService authService;

    public static void main(String[] args) throws SQLException {

        authService = new BaseAuthService();
        authService.start();

//        authService.in
        authService.stop();
    }


}
