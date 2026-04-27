package cmm.pvz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RainSystem {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private ArrayList<Raindrop> raindrops; // 雨滴列表
    private ImageIcon dropIcon;            // 雨滴图片
    private boolean isRaining;             // 是否下雨
    public static Boolean jiaoshuiFlag=false,state=false;
    private Random random;
    private Timer rainTimer;               // 下雨定时器
    private ZenGardenFrame garden;         // 花园引用

    // 雨滴实体
    private class Raindrop {
        int x, y, speed;
        public Raindrop(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }

    public RainSystem(ZenGardenFrame zenGardenFrame) {
        this.garden = zenGardenFrame;
        this.random = new Random();
        this.raindrops = new ArrayList<>();
        this.dropIcon = new ImageIcon(ResourceLoader.getResource("\\resources\\woniu.jpg"));
        this.isRaining = false;
    }
//    public void stratJs(Plant plant) {
//    	if(jiaoshuiFlag) {
//    		ResourceLoader.playSound("\\resources\\music\\这波我很舒服.wav");
//    		garden.repaint(); // 重绘花园
//    		jiaoshuiFlag=false;
//    	}
//    }
    
	public int getWIDTH() {
		return WIDTH;
	}
	public int getHEIGHT() {
		return HEIGHT;
	}
	public ArrayList<Raindrop> getRaindrops() {
		return raindrops;
	}
	public ImageIcon getDropIcon() {
		return dropIcon;
	}
	public Random getRandom() {
		return random;
	}
	public Timer getRainTimer() {
		return rainTimer;
	}
	public ZenGardenFrame getGarden() {
		return garden;
	}
	// 开始下雨
    public void startRain() {
        if (isRaining) return;
        isRaining = true;
        RainSystem.state=true;
        // 播放下雨音效
//        ResourceLoader.playSound("\\resources\\music\\rain.wav");
        
        // 雨滴生成定时器
        rainTimer = new Timer();
        rainTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 生成新雨滴
                int x = random.nextInt(WIDTH);
                int y = 0;
                int speed = random.nextInt(5) + 3;
                raindrops.add(new Raindrop(x, y, speed));

                // 移动雨滴
                for (int i = 0; i < raindrops.size(); i++) {
                    Raindrop drop = raindrops.get(i);
                    drop.y += drop.speed;
                    // 超出屏幕移除
                    if (drop.y > HEIGHT) {
                        raindrops.remove(i);
                        i--;
                    }
                }

                // 下雨时自动浇水所有植物
                if (isRaining) {
                    garden.waterAllPlantsByRain();
                }
//                ResourceLoader.playSound("\\resources\\music\\rain.wav");
                garden.repaint(); // 重绘花园
            }
        }, 0, 50); // 每50ms更新雨滴
    }

    // 停止下雨
    public void stopRain() {
        isRaining = false;
        RainSystem.state=false;
        if (rainTimer != null) {
            rainTimer.cancel();
        }
        raindrops.clear();
        garden.repaint();
    }

    // 绘制雨滴
    public void paintRain(Graphics g) {
        if (!isRaining) return;
        synchronized (raindrops) {
            for (int i = 0; i < raindrops.size(); i++) {
                Raindrop drop = raindrops.get(i);
                g.drawImage(dropIcon.getImage(), drop.x, drop.y, 5, 15, garden);
            }
        }
//        for (Raindrop drop : raindrops) {
//            g.drawImage(dropIcon.getImage(), drop.x, drop.y, 5, 15, garden);
//        }
    }

    // Getter
    public boolean isRaining() { return isRaining; }
}