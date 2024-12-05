import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterWindow extends JFrame {
    // 组件声明
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel statusLabel;

    // 构造函数
    public RegisterWindow() {
        // 初始化界面
        setTitle("注册新用户");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1));

        // 用户名输入
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("用户名:"));
        usernameField = new JTextField(20);
        usernamePanel.add(usernameField);
        add(usernamePanel);

        // 密码输入
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("密码:"));
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);
        add(passwordPanel);

        // 注册按钮
        registerButton = new JButton("注册");
        add(registerButton);

        // 状态显示
        statusLabel = new JLabel("", SwingConstants.CENTER);
        add(statusLabel);

        // 注册按钮事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();  // 点击“注册”按钮时调用 handleRegister
            }
        });
    }

    // 处理注册操作的方法
    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("用户名和密码不能为空！");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // 调用 registerUser 方法，尝试注册用户
        if (registerUser(username, password)) {
            statusLabel.setText("注册成功！");
            statusLabel.setForeground(Color.GREEN);
            JOptionPane.showMessageDialog(this, "用户注册成功，请登录。", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // 关闭注册窗口
        } else {
            statusLabel.setText("注册失败，用户名可能已存在。");
            statusLabel.setForeground(Color.RED);
        }
    }

    // 注册用户的方法
    private boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (connection == null) {
                System.out.println("数据库连接失败！");
                return false;
            }

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return true; // 插入成功
            } else {
                System.out.println("插入用户失败，没有受影响的行。");
                return false; // 插入失败
            }
        } catch (Exception e) {
            System.err.println("SQL 异常: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 启动窗口的 main 方法
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterWindow registerWindow = new RegisterWindow();
            registerWindow.setVisible(true);
        });
    }
}
