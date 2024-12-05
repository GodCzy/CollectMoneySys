import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        // 数据库连接参数
        String url = "jdbc:mysql://localhost:3306/eatsys?serverTimezone=GMT%2B8"; // 添加时区配置
        String user = "root";  // 数据库用户名
        String password = "123456";  // 数据库密码

        // 建立连接
        return DriverManager.getConnection(url, user, password);
    }
}
