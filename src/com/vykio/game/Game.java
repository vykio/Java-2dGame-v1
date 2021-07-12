package com.vykio.game;

import com.vykio.game.entities.Player;
import com.vykio.game.entities.PlayerMP;
import com.vykio.game.gfx.Colours;
import com.vykio.game.gfx.Screen;
import com.vykio.game.gfx.SpriteSheet;
import com.vykio.game.gfx.Font;
import com.vykio.game.level.Level;
import com.vykio.game.level.tiles.Tile;
import com.vykio.game.net.GameClient;
import com.vykio.game.net.GameServer;
import com.vykio.game.net.packets.Packet00Login;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 300;
    public static final int HEIGHT = WIDTH / 16*9;
    public static final int SCALE = 3;
    public static final String NAME = "Game";
    public static Game game;

    public JFrame frame;

    public boolean running = false;
    public int tickCount = 0;
    public int current_fps = 0;
    public int current_ticks = 0;

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    private int[] colours = new int[6*6*6]; // 6 shades of colours

    private Screen screen;
    public InputHandler input;
    public WindowHandler windowHandler;

    public Level level;
    public Player player;


    public GameClient socketClient;
    public GameServer socketServer;

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
        game = this;
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

        windowHandler = new WindowHandler(this);

        Tile.initTiles();

        screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/spritesheet.png"));
        input = new InputHandler(this);

        level = new Level("/levels/water_test_level.png");

        player = new PlayerMP(level, 100, 100, input, JOptionPane.showInputDialog(this,"Please enter a username:"),
                null, -1);

        level.addEntity(player);

        Packet00Login loginPacket = new Packet00Login(player.getUsername(), player.x, player.y);

        if (socketServer != null) {
            socketServer.addConnection((PlayerMP) player, loginPacket);
        }

        //socketClient.sendData("ping".getBytes());
        loginPacket.writeData(socketClient);
    }

    public synchronized void start() {
        running = true;
        new Thread(this).start(); //Running "run" function

        if (JOptionPane.showConfirmDialog(this,"Do you want to run the server") == 0) {
            socketServer = new GameServer(this);
            socketServer.start();
        }

        socketClient = new GameClient(this, "localhost");
        socketClient.start();

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
                current_ticks = ticks;
                //System.out.println(ticks + " ticks, "+ frames + " frames");
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

    Graphics g;
    BufferStrategy bs;
    public void render() {
        bs = getBufferStrategy();
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
        Font.render("fps:" + current_fps + " ticks:"+ current_ticks, screen, screen.xOffset, screen.yOffset+ screen.height-9, Colours.get(-1, -1, -1, 000), 1);
        if (socketServer != null) {
            if (socketServer.isRunning) {
                Font.render("Server running ("+socketServer.getPlayerNumber()+" online)", screen, screen.xOffset, screen.yOffset+ screen.height-20, Colours.get(-1, -1, -1, 300), 1);
            }
        }
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

        g = bs.getDrawGraphics();

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        image.flush();
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        new Game().start();
    }

}
