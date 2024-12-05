import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private JButton accountButton;
    private JPanel accountPanel, categoryPanel, gapPanel, cartPanel;
    private JTextArea cartDetails;
    private JLabel totalPriceLabel;
    private int totalPrice = 0;

    private Connection connection;

    public MainWindow(String username) {
        setTitle("欢迎, " + username);
        setSize(900, 700);  // 增大窗口尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建数据库连接
        connectToDatabase();

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));  // 使用 BorderLayout 布局

        // 创建并设置横条区（账号管理）
        accountPanel = createPanel(new Color(70, 130, 180), 250, 80);
        accountButton = createRoundedButton("账号管理", new Color(100, 149, 237), 230, 50);
        accountPanel.add(accountButton, BorderLayout.CENTER);

        // 创建并设置左侧菜品分类区
        categoryPanel = createCategoryPanel();

        // 创建右侧购物车区域
        cartPanel = createCartPanel();

        // 创建交界空白区域
        gapPanel = new JPanel();
        gapPanel.setPreferredSize(new Dimension(50, 700));  // 交界空白区域宽度

        // 将面板添加到主面板
        mainPanel.add(accountPanel, BorderLayout.NORTH);
        mainPanel.add(categoryPanel, BorderLayout.WEST);
        mainPanel.add(gapPanel, BorderLayout.CENTER); // 添加空白交界处
        mainPanel.add(cartPanel, BorderLayout.EAST); // 右侧购物车区域

        // 将主面板添加到窗口
        setContentPane(mainPanel);
    }

    private JPanel createPanel(Color bgColor, int width, int height) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(bgColor);
        panel.setBorder(createShadowBorder()); // 给面板加上阴影效果
        return panel;
    }

    private Border createShadowBorder() {
        return BorderFactory.createLineBorder(new Color(200, 200, 200), 5, true); // 创建柔和的阴影效果
    }

    // 创建圆角按钮并添加阴影效果
    private JButton createRoundedButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 16)); // 字体调整为微软雅黑，支持中文
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setOpaque(true);
        button.setBorder(createRoundedBorder(15)); // 圆角边框
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainWindow.this, "你选择了" + text);
            }
        });

        return button;
    }

    // 创建圆角边框
    private Border createRoundedBorder(int radius) {
        Border shadowBorder = new SoftBevelBorder(SoftBevelBorder.RAISED, Color.GRAY, Color.LIGHT_GRAY);
        return BorderFactory.createCompoundBorder(shadowBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 边距
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // 使用垂直布局

        try {
            // 获取菜品分类
            List<String> categories = getCategoriesFromDatabase();
            for (String category : categories) {
                // 创建按钮
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new BorderLayout()); // 用于按钮和分界线的布局

                JButton categoryButton = new JButton(category);
                categoryButton.setPreferredSize(new Dimension(200, 50));
                categoryButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
                categoryButton.setBackground(new Color(100, 149, 237));
                categoryButton.setForeground(Color.WHITE);
                categoryButton.setFocusPainted(false);
                categoryButton.setOpaque(true);
                categoryButton.setBorder(createRoundedBorder(15));

                // 添加点击事件
                categoryButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showDishesInCategory(category);
                    }
                });

                // 将按钮添加到面板
                buttonPanel.add(categoryButton, BorderLayout.CENTER);

                // 添加分隔线
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setPreferredSize(new Dimension(200, 2));  // 设置分隔线的高度和宽度
                buttonPanel.add(separator, BorderLayout.SOUTH); // 将分隔线放在按钮的下方

                // 将每个按钮面板添加到总面板
                panel.add(buttonPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 使用自定义的购物车图片
        String imagePath = "images/custom_cart_icon.png"; // 替换为你自己的图片路径
        ImageIcon cartImageIcon = new ImageIcon(imagePath);
        Image cartImage = cartImageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // 设置图标大小
        JLabel cartIcon = new JLabel(new ImageIcon(cartImage)); // 创建自定义图标

        panel.add(cartIcon);

        // 购物车详情
        cartDetails = new JTextArea(10, 20);
        cartDetails.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartDetails);
        panel.add(scrollPane);

        // 总价标签
        totalPriceLabel = new JLabel("总价: ￥0.00");
        panel.add(totalPriceLabel);

        return panel;
    }


    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/eatsys?serverTimezone=GMT%2B8"; // 添加时区配置
            String user = "root";  // 数据库用户名
            String password = "123456";  // 数据库密码
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> getCategoriesFromDatabase() throws SQLException {
        List<String> categories = new ArrayList<>();
        String query = "SELECT category_name FROM dish_categories";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            categories.add(rs.getString("category_name"));
        }
        return categories;
    }

    private void showDishesInCategory(String category) {
        // 清除之前显示的内容
        gapPanel.removeAll();

        try {
            List<Dish> dishes = getDishesFromCategory(category);
            JPanel dishesPanel = new JPanel();
            dishesPanel.setLayout(new GridLayout(dishes.size(), 1));

            for (Dish dish : dishes) {
                JPanel dishPanel = new JPanel();
                dishPanel.setLayout(new BorderLayout());

                // 显示菜品名称
                JButton dishButton = new JButton(dish.getName());
                dishButton.setPreferredSize(new Dimension(200, 50));
                dishButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
                dishButton.setBackground(new Color(100, 149, 237));
                dishButton.setForeground(Color.WHITE);
                dishButton.setFocusPainted(false);
                dishButton.setOpaque(true);
                dishButton.setBorder(createRoundedBorder(15));

                // 显示菜品图片，并限制图片大小
                String imagePath = dish.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    // 构造图片的相对路径
                    String fullImagePath = "images/" + imagePath;  // 假设 images 文件夹在项目根目录下
                    ImageIcon dishImageIcon = new ImageIcon(fullImagePath);  // 创建图片图标

                    // 限制图片的大小，例如设置最大宽度为 100px，最大高度为 100px
                    Image dishImage = dishImageIcon.getImage();
                    Image resizedImage = dishImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);  // 缩放图片
                    ImageIcon resizedDishImageIcon = new ImageIcon(resizedImage);  // 创建缩放后的图片图标

                    // 将缩放后的图片放置在菜品面板中
                    JLabel imageLabel = new JLabel(resizedDishImageIcon);
                    dishPanel.add(imageLabel, BorderLayout.WEST);  // 将图片放在左侧
                }

                // 添加点击事件来添加到购物车
                dishButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addToCart(dish.getName());
                    }
                });

                dishPanel.add(dishButton, BorderLayout.CENTER);

                // 添加分隔线
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setPreferredSize(new Dimension(200, 2));
                dishPanel.add(separator, BorderLayout.SOUTH);

                dishesPanel.add(dishPanel);
            }

            gapPanel.add(dishesPanel);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        gapPanel.revalidate();
        gapPanel.repaint();
    }



    private List<Dish> getDishesFromCategory(String category) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String query = "SELECT name, image_path FROM dishes WHERE category_id = (SELECT id FROM dish_categories WHERE category_name = ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, category);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String dishName = rs.getString("name");
            String imagePath = rs.getString("image_path"); // 获取图片路径
            dishes.add(new Dish(dishName, imagePath));
        }
        return dishes;
    }


    private void addToCart(String dishName) {
        // 假设每个菜品的价格为30元，实际情况要从数据库查询
        int price = 30;  // 这里需要查询数据库来获取价格
        totalPrice += price;

        // 更新购物车详情和总价
        cartDetails.append(dishName + " - ￥" + price + "\n");
        totalPriceLabel.setText("总价: ￥" + totalPrice + ".00");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow("用户名"); // 将"用户名"替换为登录用户的用户名
            mainWindow.setVisible(true);
        });
    }
}

// 新增 Dish 类用于存储菜品信息
class Dish {
    private String name;
    private String imagePath;

    public Dish(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }
}

