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
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建数据库连接
        connectToDatabase();

        // 主面板设置为带背景的 JPanel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 加载并绘制背景图片
                ImageIcon backgroundIcon = new ImageIcon("images/background.png");
                Image backgroundImage = backgroundIcon.getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10)); // 使用 BorderLayout 布局

        // 创建并设置横条区（账号管理）
        accountPanel = createPanel(new Color(70, 130, 180), 250, 80);
        accountButton = createRoundedButton("账号管理", new Color(100, 149, 237), 230, 50);
        accountPanel.add(accountButton, BorderLayout.CENTER);

        // 创建并设置左侧菜品分类区
        categoryPanel = createCategoryPanel();
        categoryPanel.setOpaque(false); // 设置为透明以显示背景

        // 创建右侧购物车区域
        cartPanel = createCartPanel();
        cartPanel.setOpaque(false); // 设置为透明以显示背景

        // 创建交界空白区域
        gapPanel = new JPanel();
        gapPanel.setOpaque(false); // 设置透明以显示背景
        gapPanel.setPreferredSize(new Dimension(50, 700)); // 交界空白区域宽度

        // 将面板添加到主面板
        mainPanel.add(accountPanel, BorderLayout.NORTH);
        mainPanel.add(categoryPanel, BorderLayout.WEST);
        mainPanel.add(gapPanel, BorderLayout.CENTER); // 添加空白交界处
        mainPanel.add(cartPanel, BorderLayout.EAST); // 右侧购物车区域

        // 将主面板添加到窗口
        setContentPane(mainPanel);
    }


    private JPanel createBackgroundPanel(String imagePath) {
        // 加载背景图片
        ImageIcon backgroundIcon = new ImageIcon(imagePath);
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout()); // 设置布局以覆盖组件

        // 使用 JPanel 作为主面板
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout()); // 设置布局

        return backgroundPanel;
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

    // 在 createCartPanel 方法中添加结算按钮
// 修改购物车面板的创建方法
    private JPanel createCartPanel() {
        // 创建带背景图片的购物车面板
        JPanel cartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 加载并绘制背景图片
                ImageIcon backgroundIcon = new ImageIcon("images/cart_background.jpg");
                Image backgroundImage = backgroundIcon.getImage();
                // 调整背景图片大小以适应面板
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS)); // 使用垂直布局
        cartPanel.setPreferredSize(new Dimension(250, 500)); // 调整购物车面板的宽度和高度

        // 添加间距
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 使用自定义的购物车图标
        String imagePath = "images/custom_cart_icon.png"; // 替换为你的购物车图标路径
        ImageIcon cartImageIcon = new ImageIcon(imagePath);
        Image cartImage = cartImageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 设置图标大小
        JLabel cartIcon = new JLabel(new ImageIcon(cartImage)); // 创建自定义图标
        cartIcon.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        cartPanel.add(cartIcon);

        // 购物车详情
        cartDetails = new JTextArea(10, 20);
        cartDetails.setEditable(false);
        cartDetails.setOpaque(false); // 设置文本区域透明
        cartDetails.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        cartDetails.setForeground(Color.WHITE); // 设置文本颜色
        JScrollPane scrollPane = new JScrollPane(cartDetails);
        scrollPane.setOpaque(false); // 设置滚动面板透明
        scrollPane.getViewport().setOpaque(false); // 设置滚动视图透明
        cartPanel.add(scrollPane);

        // 总价标签
        totalPriceLabel = new JLabel("总价: ￥0.00");
        totalPriceLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        totalPriceLabel.setForeground(Color.WHITE); // 设置文本颜色
        totalPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        cartPanel.add(totalPriceLabel);

        // 结算按钮
        JButton checkoutButton = new JButton("结算");
        checkoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        checkoutButton.setBackground(new Color(100, 149, 237));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setPreferredSize(new Dimension(200, 50));
        checkoutButton.setOpaque(true);
        checkoutButton.setBorder(createRoundedBorder(15));
        checkoutButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐

        // 结算按钮点击事件
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartDetails.getText().isEmpty()) { // 检查购物车是否为空
                    JOptionPane.showMessageDialog(MainWindow.this, "您未选择任何菜品", "提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MainWindow.this, "支付成功！感谢您的购物！", "支付成功", JOptionPane.INFORMATION_MESSAGE);

                    // 清空购物车
                    cartDetails.setText("");
                    totalPrice = 0;
                    totalPriceLabel.setText("总价: ￥0.00");
                }
            }
        });

        cartPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 添加间距
        cartPanel.add(checkoutButton);

        return cartPanel;
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
                    String fullImagePath = "images/" + imagePath;  // 假设 images 文件夹在项目根目录下
                    ImageIcon dishImageIcon = new ImageIcon(fullImagePath);
                    Image dishImage = dishImageIcon.getImage();
                    Image resizedImage = dishImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);  // 缩放图片
                    ImageIcon resizedDishImageIcon = new ImageIcon(resizedImage);  // 创建缩放后的图片图标

                    // 将缩放后的图片放置在菜品面板中
                    JLabel imageLabel = new JLabel(resizedDishImageIcon);
                    dishPanel.add(imageLabel, BorderLayout.WEST);  // 将图片放在左侧
                }

                // 显示价格的小按钮
                JButton priceButton = new JButton("￥" + dish.getPrice());
                priceButton.setPreferredSize(new Dimension(60, 30));
                priceButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                priceButton.setBackground(new Color(240, 128, 128));  // 选择一个显眼的颜色
                priceButton.setForeground(Color.WHITE);
                priceButton.setFocusPainted(false);
                priceButton.setOpaque(true);
                priceButton.setBorder(createRoundedBorder(15));

                // 添加点击事件，点击按钮时显示价格
                priceButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(MainWindow.this, dish.getName() + "的价格是: ￥" + dish.getPrice(), "菜品价格", JOptionPane.INFORMATION_MESSAGE);
                    }
                });

                // 小加号按钮
                JButton addButton = new JButton("+");
                addButton.setPreferredSize(new Dimension(40, 40));
                addButton.setFont(new Font("微软雅黑", Font.PLAIN, 20));
                addButton.setBackground(new Color(34, 139, 34));  // 加号按钮绿色
                addButton.setForeground(Color.WHITE);
                addButton.setFocusPainted(false);
                addButton.setOpaque(true);
                addButton.setBorder(createRoundedBorder(15));

                // 添加加号按钮的点击事件，将菜品添加到购物车
                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addToCart(dish);
                    }
                });

                // 在菜品按钮旁边放置价格按钮和加号按钮
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(priceButton);  // 添加价格按钮
                buttonPanel.add(addButton);    // 添加加号按钮

                dishPanel.add(dishButton, BorderLayout.CENTER);
                dishPanel.add(buttonPanel, BorderLayout.EAST);  // 将价格按钮和加号按钮放到右边

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
        String query = "SELECT name, image_path, price FROM dishes WHERE category_id = (SELECT id FROM dish_categories WHERE category_name = ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, category);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String dishName = rs.getString("name");
            String imagePath = rs.getString("image_path"); // 获取图片路径
            int price = rs.getInt("price");  // 获取菜品价格
            dishes.add(new Dish(dishName, imagePath, price));
        }
        return dishes;
    }


    private void addToCart(Dish dish) {
        // 获取菜品价格
        int price = dish.getPrice();

        // 更新购物车详情和总价
        cartDetails.append(dish.getName() + " - ￥" + price + "\n");
        totalPrice += price;
        totalPriceLabel.setText("总价: ￥" + totalPrice + ".00");
    }


    // 新增 Dish 类用于存储菜品信息（包括价格）
    class Dish {
        private String name;
        private String imagePath;
        private int price;  // 增加价格字段

        public Dish(String name, String imagePath, int price) {
            this.name = name;
            this.imagePath = imagePath;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getPrice() {
            return price;  // 获取价格
        }
    }
}


