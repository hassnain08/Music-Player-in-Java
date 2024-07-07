import java.io.*;
import java.util.Scanner;

public class Login_SignUp {
    private static final String USERS_FILE = "users.txt";

    public void signup(String username, String password) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true)))
        {
            writer.write(username + ":" + password);
            writer.newLine();
        }
    }

    public boolean login(String username, String password) throws IOException
    {
        try (Scanner scanner = new Scanner(new File(USERS_FILE)))
        {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                if (parts[0].equals(username) && parts[1].equals(password))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
