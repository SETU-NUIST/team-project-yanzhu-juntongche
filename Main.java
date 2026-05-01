package cmm.pvz;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Swing UI 线程启动
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ZenGardenFrame(); // 启动花园窗口
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}