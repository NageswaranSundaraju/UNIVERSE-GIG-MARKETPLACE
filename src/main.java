import com.formdev.flatlaf.FlatDarkLaf;
import controller.AuthController;
import model.dao.UserDAO;
import view.LoginWindow;
import view.RegisterWindow;
import javax.swing.UIManager;


public class main {
	public static void main(String[] args) {
//        System.out.println("Testing UniVerse Database Connection...");
//        
//        // This will trigger your connection test and print [SUCCESS] or [ERROR]
//        DatabaseConnection.getConnection();
//        
//        UserDAO.addUser()

		// Enforce the modern FlatLaf Dark theme across all Swing elements
		try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Theme Initialization Failed.");
        }

        // Run application components on Thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Initialize View UI Windows
                LoginWindow loginWin = new LoginWindow();
                RegisterWindow registerWin = new RegisterWindow();

                // Initialize Model DAO Components
                UserDAO userDAO = new UserDAO();

                // Initialize Controller to wire everything together!
                new AuthController(loginWin, registerWin, userDAO);

                // Start Application with only the login view visible
                loginWin.setVisible(true);
            }
        });
        
    }

}
