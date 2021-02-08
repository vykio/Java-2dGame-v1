package com.vykio.game.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {
    public String path;
    public int width;
    public int height;

    public int[] pixels;

    public SpriteSheet(String path) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image == null) {
            return;
        }

        this.path = path;
        this.width = image.getWidth();
        this.height = image.getHeight();

        pixels = image.getRGB(0,0,width, height, null, 0, width);

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (pixels[i] & 0xff) / 64; //Remove alpha channel and transpose from [0;255] -> [0;3] <== 64 ~= 255/4

            /*
            * Black =                   0 -> 0
            * Dark Grey = 255/3*1 =    85 -> 1
            * Light Grey = 255/3*2 =  170 -> 2
            * White =                 255 -> 3
            * */
        }

        for (int i = 0; i < 8; i++) {
            System.out.println(pixels[i]);
        }

    }
}
