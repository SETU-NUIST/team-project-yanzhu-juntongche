package cmm.pvz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Snail {
    private ImageIcon snailIcon;
    private int x=100, y=110,count=0,flag=0;
    private int speed = 1;
    private Random random;
    private Timer moveTimer;
    private ZenGardenFrame garden;
    private boolean isActive;

    public Snail(ZenGardenFrame garden) {
        this.garden = garden;
        this.random = new Random();
        // ✅ 修复图片路径
        this.snailIcon = new ImageIcon(ResourceLoader.getResource("\\resources\\woniu.jpg"));
        // 随机出生位置
        this.x = random.nextInt(600);
        this.y = random.nextInt(400);
        this.isActive = false;
    }

    // 激活蜗牛
    public void activate() {
        if (isActive) return;
        isActive = true;

        moveTimer = new Timer(120, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ✅ 真正平滑随机移动（修复！）
                
                if(x>560 && y>400) {
                	flag=1;
                	System.out.println(flag);
                }else if(x>560 && y<400) {
                	flag=2;System.out.println(flag);
                }else if(x<560 && y>400) {
                	flag=3;System.out.println(flag);
                }else if(x<560 &&x>50 && y<400 && y>50) {
                	flag=4;System.out.println(flag);
                }
                else if(x>50 && y<50) {
                	flag=5;System.out.println(flag);
                }else if(x>50 && y> 50 ) {
                	flag=6;System.out.println(flag);
                }else if(x<50 && y>50) {
                	flag=7;System.out.println(flag);
                }else if(x<50 && y<50) {
                	flag=8;System.out.println(flag);
                }
                count++;
                if(RainSystem.state) {
                	speed=5;
                }else {
                	speed=2;
                }
                switch(flag) {
                	case 1: x-=2;y-=2;break;
                	case 2: x-=1;y-=2;break;
                	case 3: x-=random.nextInt(4);y+=random.nextInt(4)+speed;break;
                	case 4: x+=random.nextInt(6)-2;y-=random.nextInt(3)+3+speed;break;
                	case 5: x+=random.nextInt(3)+speed;y+=random.nextInt(3)+speed;break;
                	case 6: x+=random.nextInt(3)+speed;y+=random.nextInt(3)+speed;break;
                	case 7:  x+=random.nextInt(3)+speed;y-=random.nextInt(3);break;
                	case 8:  x+=random.nextInt(3)+speed;y+=random.nextInt(3)+speed;break;
                	
                }
                // ✅ 正确边界限制（不会卡、不会飞）
                x = Math.max(20, Math.min(x, garden.getWidth() - 160));
                y = Math.max(20, Math.min(y, garden.getHeight() - 160));
//                System.out.println(x+ "   "+y);
                // 自动收集金币
                collectCoins();
                garden.repaint();
            }
        });
        moveTimer.start();
    }

    // 收集金币
    private void collectCoins() {
        if (garden.getCoinList() != null && !garden.getCoinList().isEmpty()) {
            int index = random.nextInt(garden.getCoinList().size());
            garden.collectCoin(index);
            ResourceLoader.playSound("/sound/coin.wav");
        }
    }

    // 绘制蜗牛
    public void paintSnail(Graphics g) {
        if (!isActive) return;
        g.drawImage(snailIcon.getImage(), x, y, 50, 50, garden);
    }

    public boolean isActive() {
        return isActive;
    }
}