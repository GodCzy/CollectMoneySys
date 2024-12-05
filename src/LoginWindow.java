import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JButton registerButton;

    public LoginWindow() {
        setTitle("登录系统");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1));

        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("用户名:"));
        usernamePanel.add(usernameField = new JTextField(20));
        add(usernamePanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("密码:"));
        passwordPanel.add(passwordField = new JPasswordField(20));
        add(passwordPanel);

        loginButton = new JButton("登录");
        add(loginButton);

        registerButton = new JButton("注册");
        add(registerButton);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        add(statusLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterWindow registerWindow = new RegisterWindow();
                registerWindow.setVisible(true);
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (authenticate(username, password)) {
            statusLabel.setText("登录成功！");
            statusLabel.setForeground(Color.GREEN);
            JOptionPane.showMessageDialog(this, "欢迎, " + username + "!", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            MainWindow mainWindow = new MainWindow(username);
            mainWindow.setVisible(true);
        } else {
            statusLabel.setText("账号或密码错误！");
            statusLabel.setForeground(Color.RED);
        }
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}
