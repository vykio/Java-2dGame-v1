package com.vykio.game;

import com.vykio.game.entities.Player;
import com.vykio.game.gfx.Colours;
import com.vykio.game.gfx.Screen;
import com.vykio.game.gfx.SpriteSheet;
import com.vykio.game.gfx.Font;
import com.vykio.game.level.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 500;
    public static final int HEIGHT = WIDTH / 16*9;
    public static final int SCALE = 3;
    public static final String NAME = "Game";

    private JFrame frame;

    public boolean running = false;
    public int tickCount = 0;
    public int current_fps = 0;

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    private int[] colours = new int[6*6*6]; // 6 shades of colours

    private Screen screen;
    public InputHandler input;

    public Level level;
    public Player player;

    public Game() {
        setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));

        frame = new JFrame(NAME);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(this, BorderLayout.CENTER);

        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void init() {
        int index = 0;
        for (int r = 0; r < 6; r++) {
            for (int g = 0; g < 6; g++) {
                for (int b = 0; b < 6; b++) {
                    int rr = (r*255/5);
                    int gg = (g*255/5);
                    int bb = (b*255/5);

                    colours[index++] = rr << 16 | gg << 8 | bb;
                }
            }
        }

        screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/spritesheet.png"));
        input = new InputHandler(this);

        level = new Level("/levels/water_test_level.png");

        player = new Player(level, screen.xOffset+ (level.width << 3)/2, screen.yOffset + (level.height <<3) /2, input);

        level.addEntity(player);
    }

    public synchronized void start() {
        running = true;
        new Thread(this).start(); //Running "run" function
    }

    public synchronized void stop() {
        running = false;
    }

    public void run() {

        long lastTime = System.nanoTime();
        double nanoSecondesPerTick = 1000000000D / 60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        init();

        while(running) {
            long now = System.nanoTime();
            delta += (now-lastTime) / nanoSecondesPerTick;
            lastTime = now;

            boolean shouldRender = true; // false => ticks = frames = 60
            while (delta >= 1) {
                ticks++;
                tick();
                delta--;
                shouldRender = true;
            }

            /*try {
                Thread.sleep(2); // bloquer les frames pour passer de 600.000 frames à ~440
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (shouldRender){
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer > 1000) { // > 1 seconde
                lastTimer += 1000;
                current_fps = frames;
                System.out.println(ticks + " ticks, "+ frames + " frames");
                frames = 0;
                ticks = 0;
            }
        }
    }


    public void tick() {
        tickCount++;



        level.tick();

        //screen.xOffset++;
        //screen.yOffset++;
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        int xOffset = player.x - (screen.width /2);
        int yOffset = player.y - (screen.height / 2);
        level.renderTiles(screen, xOffset, yOffset);

        /*for (int x = 0; x < level.width; x++) {
            int colour = Colours.get(-1,-1,-1,000);
            if (x % 10 == 0 && x != 0) {
                colour = Colours.get(-1,-1,-1,500);
            }
            Font.render((x % 10) + "", screen, (x*8),0, colour, 1);
        }*/

        level.renderEntities(screen);
        Font.render("fps:" + current_fps + "", screen, screen.xOffset, screen.yOffset+ screen.height-9, Colours.get(-1, -1, -1, 000), 1);
        /*
        String msg = "Hello, world!";
        Font.render("Hello, world!", screen, screen.xOffset+screen.width/2 - (msg.length()*8/2), screen.yOffset+ screen.height/2, Colours.get(-1,-1,-1,000));
        */

        for (int y = 0; y < screen.height; y++) {
            for (int x = 0; x < screen.width; x++) {
                int colourCode = screen.pixels[x + y * screen.width];
                if (colourCode < 255) pixels[x+ y * WIDTH] = colours[colourCode];
            }
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        new Game().start();
    }

}
