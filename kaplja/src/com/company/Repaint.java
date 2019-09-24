package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import static com.company.GameWindow.*;

public class Repaint {

    public static void onRepaint(Graphics g) {
        long current_time = System.nanoTime();
        float delta_time = (current_time - last_frame_time) * 0.000000001f;
        last_frame_time = current_time;
        kap_top = kap_top + kap_v * delta_time;
        kap_left = kap_left + (direction * kap_v) * delta_time;

        g.drawImage(sky, 0, 0, null);
        g.drawImage(kap, (int) kap_left, (int) kap_top, null);
        if (kap_top > game_window.getHeight() + 100 || kap_top < -200 || kap_left > game_window.getWidth() || kap_left < -100) {
            g.drawImage(gameover, 280, 100, null);
            g.drawImage(restart, 0, 0, null);
            end = true;
        }
        if (kap_left <= 0.0 || kap_left + kap_width > game_window.getWidth()) {
            if (direction == -1) direction = 1;
            else direction = -1;
        }
        if (drawRecords) {
            for (int i = 0; i < recordsLast.size(); i++) {
                g.drawString(recordsLast.get(i), 200, 25 + 25 * i);
            }
        }
        nameEntry.isActive = end;
        nameEntry.update(g);

    }

    public static void kapResize() throws IOException {
        kap = ImageIO.read(GameWindow.class.getResourceAsStream("drop.png")).getScaledInstance((int) kap_width, (int) kap_height, Image.SCALE_DEFAULT);
    }
}
