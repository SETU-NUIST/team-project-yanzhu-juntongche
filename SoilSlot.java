package cmm.pvz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class    SoilSlot extends JPanel {
    private static final int SLOT_SIZE = 120;
    private Plant plant;          // 空位上的植物
    private ImageIcon soilIcon;   // 泥土背景
    private ZenGardenFrame garden; // 花园引用
    public static Boolean shifeiFlag=false;
    public SoilSlot(ZenGardenFrame garden) {
        this.garden = garden;
        this.soilIcon = new ImageIcon(ResourceLoader.getResource("\\resources\\ground2.jpg"));
        setPreferredSize(new Dimension(SLOT_SIZE, SLOT_SIZE));
        setOpaque(false);

        // 鼠标悬停提示
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (plant != null) {
                    String tip = plant.getName() + " - " + plant.getGrowthProgress();
                    setToolTipText(tip);
                } else {
                    setToolTipText("空泥土槽 (点击种植植物)");
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (plant == null || plant.getStage() == Plant.GrowthStage.WILTED) {
                        // 弹出种植选择框
                        garden.showPlantSelectDialog(SoilSlot.this);
                }else if (!e.isMetaDown()&&SoilSlot.shifeiFlag){
                    	if (plant != null) {SoilSlot.shifeiFlag=false;plant.fertilize();ResourceLoader.playSound("\\resources\\music\\把我养肥了我是不会亏待你的.wav");repaint();}
                    }
//                    } else {
//                        // 右键浇水，左键施肥（可自定义）
//                    	System.out.println("12你好啊");
//                    	System.out.println(e.isMetaDown());
//                    	System.out.println(plant);
//                        if (e.isMetaDown()) {
//                            if (plant != null  && RainSystem.jiaoshuiFlag) {System.out.println("你好歹"); plant.water();RainSystem.jiaoshuiFlag=false;ResourceLoader.playSound("\\resources\\music\\这波我很舒服.wav");}
//                        } else {
//                            if (plant != null) plant.fertilize();
//                        }
//                        repaint();
//                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (plant != null  && RainSystem.jiaoshuiFlag) { plant.water();RainSystem.jiaoshuiFlag=false;ResourceLoader.playSound("\\resources\\music\\这波我很舒服.wav");repaint();}
                } 
                repaint();
//                if (e.getButton() == MouseEvent.BUTTON1) {
//                    // 右键浇水，左键施肥（可自定义）
//                	System.out.println("12你好啊");
//                	System.out.println(e.isMetaDown());
//                	System.out.println(plant);
//                    if (e.isMetaDown()) {
//                        if (plant != null  && RainSystem.jiaoshuiFlag) {System.out.println("你好歹"); plant.water();RainSystem.jiaoshuiFlag=false;ResourceLoader.playSound("\\resources\\music\\这波我很舒服.wav");}
//                    } else {
//                        if (plant != null) plant.fertilize();
//                    }
//                    repaint();
//                }
            }
        });
    }

    // 种植植物
    public void plantSeed(Plant newPlant) {
        this.plant = newPlant;
        repaint();
        // 加入图鉴
        garden.getPlantCollection().collectPlant(newPlant.getName());
    }

    // 绘制泥土槽和植物
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 绘制土壤
        g.drawImage(soilIcon.getImage(), 30, 30, SLOT_SIZE, SLOT_SIZE, this);

        // 绘制植物
        if (plant != null) {
//            System.out.println("植物存在，开始绘制图片");
            
            Image plantImage = plant.getCurrentIcon().getImage();
            
            if (plantImage == null) {
                System.err.println("错误：植物图片为空！");
                return;
            }

            // ==============================================
            // ✅ 固定图片大小：120x120，居中，完整显示
            // ==============================================
            int imgW = 120;  // 你要的宽度
            int imgH = 120;  // 你要的高度
            
            // 居中计算（基于土壤的 30,30 位置）
            int centerX = 30 + (SLOT_SIZE - imgW) / 2;
            int centerY = 30 + (SLOT_SIZE - imgH) / 2;

            // ✅ 绘制：固定大小 + 完整显示 + 不变形
            g.drawImage(plantImage, centerX, centerY, imgW, imgH, this);
        }
    }
    // Getter & Setter
    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }
}