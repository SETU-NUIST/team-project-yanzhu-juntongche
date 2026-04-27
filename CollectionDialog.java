package cmm.pvz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 植物图鉴对话框：展示所有植物信息和收集进度
 */
public class CollectionDialog extends JDialog {

    
    public CollectionDialog(JFrame parent, PlantCollection plantCollection, java.util.List<Plant> plantedPlants) {
        super(parent, "植物图鉴", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("我的植物图鉴", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 120, 50));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 图鉴内容（滚动面板）
        JScrollPane scrollPane = new JScrollPane();
        JPanel collectionPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        collectionPanel.setBackground(new Color(240, 255, 240));

        Map<String, PlantCollection.PlantData> allPlants = plantCollection.getAllPlants();
        int collectedCount = plantCollection.getCollectedPlantIds().size();
        int totalCount = allPlants.size();

        // 添加进度标签
        JLabel progressLabel = new JLabel(String.format("已收集：%d/%d", collectedCount, totalCount), SwingConstants.CENTER);
        progressLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        progressLabel.setForeground(collectedCount == totalCount ? new Color(0, 128, 0) : new Color(128, 0, 0));
        collectionPanel.add(progressLabel);

        // 添加所有植物卡片
        for (Map.Entry<String, PlantCollection.PlantData> entry : allPlants.entrySet()) {
            PlantCollection.PlantData data = entry.getValue();
            boolean isCollected = plantCollection.isCollected(data.getPlantId());

            // 检查是否已种植
            boolean isPlanted = plantedPlants.stream()
                    .anyMatch(p -> p.getPlantId().equals(data.getPlantId()));

            // 植物卡片
            JPanel card = createPlantCard(data, isCollected, isPlanted);
            collectionPanel.add(card);
        }

        scrollPane.setViewportView(collectionPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


	// 创建植物卡片
    private JPanel createPlantCard(PlantCollection.PlantData data, boolean isCollected, boolean isPlanted) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(isCollected ? new Color(200, 255, 200) : new Color(255, 220, 220));
        card.setBorder(BorderFactory.createLineBorder(isCollected ? new Color(0, 128, 0) : new Color(128, 0, 0), 2));

        // 植物图片（用颜色方块模拟）
        JLabel imageLabel = new JLabel();
        BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Image bg = new ImageIcon(ResourceLoader.getResource("\\resources\\"+data.getImagePath())).getImage();
        g.drawImage(bg, 0, 0, 80, 80, this);
//        g.setColor(isCollected ? new Color(50, 205, 50) : new Color(200, 200, 200));
//        g.fillRect(0, 0, 80, 80);
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("微软雅黑", Font.BOLD, 30));
//        g.drawString(data.getPlantName().charAt(0) + "", 30, 55);
        g.dispose();
        imageLabel.setIcon(new ImageIcon(img));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 植物信息
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(card.getBackground());
        JLabel nameLabel = new JLabel(data.getPlantName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JLabel statusLabel = new JLabel(isCollected ? "&#9989;已收集" : "&#10060;未收集", SwingConstants.CENTER);
        statusLabel.setForeground(isCollected ? new Color(0, 128, 0) : new Color(128, 0, 0));
        infoPanel.add(nameLabel);
        infoPanel.add(statusLabel);

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        // 已种植标记
        if (isPlanted) {
            JLabel plantedLabel = new JLabel("&#127793;已种植", SwingConstants.CENTER);
            plantedLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            plantedLabel.setForeground(new Color(0, 128, 0));
            card.add(plantedLabel, BorderLayout.NORTH);
        }

        return card;
    }
}
