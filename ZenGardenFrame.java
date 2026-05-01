
package cmm.pvz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ZenGardenFrame extends JFrame {
    private JPanel gardenPanel;       // 花园面板
    private ArrayList<SoilSlot> soilSlots; // 泥土槽列表
    private RainSystem rainSystem;   // 下雨系统
    private Snail snail;              // 蜗牛
//    private PlantDictionary dictionary; // 植物图鉴
    private JLabel coinLabel;         // 金币显示
    private int coinCount = 100;      // 初始金币
    private ArrayList<Point> coinList; // 金币位置列表
    
    // 核心系统组件
    private PlantCollection plantCollection;// 植物图鉴
    // 状态
    private List<Plant> plantedPlants;

    // 植物配置（名称、种子图、花苞图、成熟图、奖励金币）
    private static final String[][] PLANT_CONFIG = {
            {"郁金香", "\\resources\\yjx1.png", "\\resources\\yjx2.png", "\\resources\\yjx3.png", "50"},
            {"康乃馨", "\\resources\\knx1.png", "\\resources\\knx2.png", "\\resources\\knx3.png", "60"},
            {"玫瑰", "\\resources\\mg1.png", "\\resources\\mg2.png", "\\resources\\mg3.png", "70"},
            {"人物", "resources\\person.jpg", "\\resources\\person.jpg", "\\resources\\person.jpg", "40"},
            {"百合", "\\resources\\bh1.png", "\\resources\\bh2.png", "\\resources\\bh3.png", "80"},
            {"向日葵", "\\resources\\xrk1.png", "\\resources\\xrk2.png", "\\resources\\xrk3.png", "90"},
            {"薰衣草", "\\resources\\xyc1.png", "\\resources\\xyc2.png", "\\resources\\xyc3.png", "100"}
    };

    public ZenGardenFrame() {
        plantedPlants = new ArrayList<>();
        plantCollection = new PlantCollection();

        // 窗口基础配置
        setTitle("禅境花园");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化组件
        initComponents();
        // 初始化天气系统（随机下雨）
        initWeatherSystem();
        // 开局送一个苗
        giveInitialPlant();
        setVisible(true);
    }

    // 初始化组件
    private void initComponents() {
    	// 背景面板
    	gardenPanel = new JPanel() {
    	    @Override
    	    protected void paintComponent(Graphics g) {
    	        super.paintComponent(g);
    	        // 绘制草地背景
    	        Image bg = new ImageIcon(ResourceLoader.getResource("\\resources\\ground1.jpg")).getImage();
    	        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    	        // 绘制下雨效果
    	        rainSystem.paintRain(g);
    	        // 绘制蜗牛
    	        snail.paintSnail(g);
    	        // 绘制金币
    	        paintCoins(g);
    	    }
    	};
    	// ===================== 【新增：点击收集金币】 =====================
    	gardenPanel.addMouseListener(new java.awt.event.MouseAdapter() {
    	    @Override
    	    public void mouseClicked(java.awt.event.MouseEvent e) {
    	        int clickX = e.getX();
    	        int clickY = e.getY();

    	        // 遍历金币，判断是否点中
    	        for (int i = 0; i < coinList.size(); i++) {
    	            Point p = coinList.get(i);
    	            // 点击范围 40x40 匹配金币大小
    	            if (clickX >= p.x && clickX <= p.x + 40 &&
    	                clickY >= p.y && clickY <= p.y + 40) {
    	                collectCoin(i); // 收集该金币
    	                gardenPanel.repaint(); // 刷新界面
    	                break;
    	            }
    	        }
    	    }
    	});
    	// ==============================================================
    	gardenPanel.setLayout(new GridLayout(3, 4, 20, 20)); // 3行4列泥土槽
    	add(gardenPanel, BorderLayout.CENTER);
//        gardenPanel.setLayout(new GridLayout(3, 4, 20, 20)); // 3行4列泥土槽
//        add(gardenPanel, BorderLayout.CENTER);

        // 初始化泥土槽
        soilSlots = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            SoilSlot slot = new SoilSlot(this);
            soilSlots.add(slot);
            gardenPanel.add(slot);
        }

        // 初始化其他系统
        rainSystem = new RainSystem(this);
        snail = new Snail(this);
//        dictionary = new PlantDictionary(this);
        coinList = new ArrayList<>();

        // 顶部控制面板
        JPanel controlPanel = new JPanel();
        coinLabel = new JLabel("金币: " + coinCount);
        JButton dictBtn = new JButton("植物图鉴");
//        JButton weatherBtn = new JButton("切换天气");
        JButton weatherBtn = new JButton("浇水");
        JButton shifeiBtn = new JButton("施肥");
        controlPanel.add(coinLabel);
        controlPanel.add(dictBtn);
        controlPanel.add(weatherBtn);
        controlPanel.add(shifeiBtn);
        add(controlPanel, BorderLayout.NORTH);

        // 按钮事件
        dictBtn.addActionListener(e ->  showPlantCollection());
        shifeiBtn.addActionListener(e -> {
        	SoilSlot.shifeiFlag=true;
        });
        weatherBtn.addActionListener(e -> {
        	RainSystem.jiaoshuiFlag=true;
//            if (rainSystem.isRaining()) {
//                rainSystem.stopRain();
////                weatherBtn.setText("切换天气（当前：晴天）");
//            } else {
//                rainSystem.startRain();
//                weatherBtn.setText("切换天气（当前：下雨）");
//            }
        });

        // ✅ 关键：启动界面自动刷新（让金币实时显示）
        new javax.swing.Timer(50, e -> gardenPanel.repaint()).start();
    }

    // 显示植物图鉴
    private void showPlantCollection() {
        new CollectionDialog(this, plantCollection, plantedPlants).setVisible(true);
    }

    // 初始化天气系统（随机下雨）
    private void initWeatherSystem() {
        Timer weatherTimer = new Timer();
        weatherTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Random random = new Random();
                if (random.nextBoolean()) {
                    rainSystem.startRain();
                } else {
                    rainSystem.stopRain();
                }
            }
        }, 0, 30 * 1000); // 每30分钟随机切换天气
    }

    // 开局送一个苗
    private void giveInitialPlant() {
        SoilSlot firstSlot = soilSlots.get(0);
        Plant initialPlant = createPlant("郁金香");
        firstSlot.plantSeed(initialPlant);
    }

    // 创建植物实例
    private Plant createPlant(String plantName) {
        for (String[] config : PLANT_CONFIG) {
            if (config[0].equals(plantName)) {
                ImageIcon seed = new ImageIcon(ResourceLoader.getResource(config[1]));
                ImageIcon bud = new ImageIcon(ResourceLoader.getResource(config[2]));
                ImageIcon mature = new ImageIcon(ResourceLoader.getResource(config[3]));
                int reward = Integer.parseInt(config[4]);
                return new Plant(plantName,plantName, seed, bud, mature, reward);
            }
        }
        return null;
    }

    // 显示植物选择对话框
    public void showPlantSelectDialog(SoilSlot slot) {
        String[] plantNames = new String[PLANT_CONFIG.length];
        int[] plantPrices = new int[PLANT_CONFIG.length];
        for (int i = 0; i < PLANT_CONFIG.length; i++) {
            plantNames[i] = PLANT_CONFIG[i][0] + " (价格：" + (Integer.parseInt(PLANT_CONFIG[i][4]) / 2) + "金币)";
            plantPrices[i] = Integer.parseInt(PLANT_CONFIG[i][4]) / 2;
        }

        String selected = (String) JOptionPane.showInputDialog(this, "选择要种植的植物",
                "种植植物", JOptionPane.QUESTION_MESSAGE, null, plantNames, plantNames[0]);

        if (selected != null) {
            // 解析选择的植物和价格
            String plantName = selected.split(" ")[0];
            int price = 0;
            for (int i = 0; i < PLANT_CONFIG.length; i++) {
                if (PLANT_CONFIG[i][0].equals(plantName)) {
                    price = Integer.parseInt(PLANT_CONFIG[i][4]) / 2;
                    break;
                }
            }

            // 检查金币是否足够
            if (coinCount >= price) {
                coinCount -= price;
                coinLabel.setText("金币: " + coinCount);
                Plant newPlant = createPlant(plantName);
                slot.plantSeed(newPlant);
                plantedPlants.add(newPlant);

                // 收集3个植物后解锁蜗牛
                if (plantCollection.getCollectedCount() >= 3 && !snail.isActive()) {//????
                    snail.activate();
                    ResourceLoader.playSound("\\resources\\music\\来了来了.wav");
                    JOptionPane.showMessageDialog(this, "解锁蜗牛！它会自动帮你收集金币～");
                }
            } else {
                JOptionPane.showMessageDialog(this, "金币不足！");
            }
        }
    }

    // 下雨时自动浇水所有植物
    public void waterAllPlantsByRain() {
        for (SoilSlot slot : soilSlots) {
            Plant plant = slot.getPlant();
            if (plant != null && plant.getStage() != Plant.GrowthStage.WILTED) {
                plant.water();
                // 植物成熟时生成金币
                if (plant.getStage() == Plant.GrowthStage.MATURE) {
                    generateCoin(slot.getX(), slot.getY());
                }
            }
        }
    }

    // 生成金币
//    public void generateCoin(int x, int y) {
//        coinList.add(new Point(x + 50, y + 50));
//        coinCount += 10; // 基础金币奖励
//        coinLabel.setText("金币: " + coinCount);
//    }
    public void generateCoin(int x, int y) {
        // 关键：必须用 slot 里的相对坐标，不然画在面板外面看不见！
        coinList.add(new Point(30, 30)); // 固定画在格子内，保证可见
        coinCount += 1;
        coinLabel.setText("金币: " + coinCount);
    }

//
//    // ✅ 修复：正确绘制金币
//    private void paintCoins(Graphics g) {
////        Image coinImg = new ImageIcon(ResourceLoader.getResource("\\resources\\jb.jpg")).getImage();
////        for (Point p : coinList) {
////            g.drawImage(coinImg, p.x, p.y, 30, 30, null);
////        }
//    	Image coinImg = new ImageIcon(ResourceLoader.getResource("\\resources\\jb.jpg")).getImage();
//        
//        // 关键：创建一个临时副本遍历，避免并发修改崩溃
//        ArrayList<Point> tempCoins = new ArrayList<>(coinList);
//        
//        for (Point p : tempCoins) {
//            g.drawImage(coinImg, p.x, p.y, 30, 30, null);
//        }
//    }
 // 绘制金币 —— 修复并发异常 + 强制显示
    private void paintCoins(Graphics g) {
        try {
            Image coinImg = new ImageIcon(ResourceLoader.getResource("\\resources\\jb.jpg")).getImage();
            
            // 防并发修改
            ArrayList<Point> tempCoins = new ArrayList<>(coinList);
            
            // 画金币（放大到 40×40，绝对看得见）
            for (Point p : tempCoins) {
                g.drawImage(coinImg, p.x, p.y, 40, 40, null);
            }
        } catch (Exception e) {
            // 图片加载失败就用黄色圆形代替，保证一定能看到金币！
            ArrayList<Point> tempCoins = new ArrayList<>(coinList);
            for (Point p : tempCoins) {
                g.setColor(Color.YELLOW);
                g.fillOval(p.x, p.y, 40, 40);
            }
        }
    }
    // 收集金币
    public void collectCoin(int index) {
        if (index >= 0 && index < coinList.size()) {
            coinList.remove(index);
            coinCount += 1;
            coinLabel.setText("金币: " + coinCount);
        }
    }

    // Getter
    public PlantCollection getPlantCollection() {return plantCollection;}
    public ArrayList<Point> getCoinList() { return coinList; }
    public JPanel getGardenPanel() { return gardenPanel; }
}