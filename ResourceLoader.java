package cmm.pvz;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class ResourceLoader {
    // 获取资源路径
    public static URL getResource(String path) {
        return ResourceLoader.class.getClassLoader().getResource(path);
    }

    // 播放音效
    public static void playSound(String soundPath) {
        new Thread(() -> {
            try {
                URL soundUrl = getResource(soundPath);
                if (soundUrl == null) return;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
}