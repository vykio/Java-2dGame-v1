package com.vykio.game.level;

import com.vykio.game.entities.Entity;
import com.vykio.game.entities.PlayerMP;
import com.vykio.game.gfx.Screen;
import com.vykio.game.level.tiles.BasicTile;
import com.vykio.game.level.tiles.CMap;
import com.vykio.game.level.tiles.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Level {

    CMap map;

    public int width;
    public int height;

    public List<Entity> entities = new ArrayList<Entity>();

    private String imagePath;
    private BufferedImage image;

    public Level(String imagePath) {

        if (imagePath != null) {
            this.imagePath = imagePath;
            this.loadLevelFromFile();
        } else {
            // Default level
            this.width = 64;
            this.height = 64;
            //tiles = new byte[width * height];
           //bitmaskArray = new int[width * height];
            map = new CMap(width * height);
            this.generateLevel();
        }


    }

    private void loadLevelFromFile() {
        try {
            this.image = ImageIO.read(Level.class.getResource(this.imagePath));
            this.width = image.getWidth();
            this.height = image.getHeight();
            //tiles = new byte[width * height];
            //bitmaskArray = new int [width * height];
            map = new CMap(width * height);

            this.loadTiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadTiles() {
        int tempBitmask;
        int[] tileColours = this.image.getRGB(0,0,width, height,null,0,width);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tileCheck: for (Tile t : Tile.tiles) {
                    if (t != null && t.getLevelColour() == tileColours[x + y * width]) {

                        this.map.addTileValue("Tiles",x + y * width, t.getId());

                        break tileCheck;
                    }
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tempBitmask = getBitmaskValue(this.map.getTiles("Tiles"), x, y);
                this.map.addBitmaskValue("Tiles",x + y * width, tempBitmask);
            }
        }
        //System.out.println(Arrays.toString(this.map.getTiles()));
        //System.out.println(Arrays.toString(this.map.getBitmask()));
    }

    public int coordToValue(int x, int y) {
        if (0 > x || x >= width || 0 > y || y >= height) return 0;
        return x + y * width;
    }

    public boolean ifTileAtIndexIsSameTypeAsLinkage(int extremiteTileId, int centerTileId) {
        //System.out.println("ID/:"+ ((BasicTile)Tile.tiles[this.map.getTile(centerTileId)]).getId());
        List<Tile> tiles = ((BasicTile)Tile.tiles[this.map.getTile("Tiles",centerTileId)]).getAcceptedLinkage();
        if (tiles.size() == 0) return false;
        for (Tile t : tiles) {
            if (t.getId() != Tile.tiles[this.map.getTile("Tiles",extremiteTileId)].getId()) {
                return false;
            }
        }
        return true;
    }

    public int getBitmaskValue(byte[] tiles, int x, int y) {
        int indexAtXY = coordToValue(x, y);
        int northIndex = coordToValue(x, y-1);
        int eastIndex = coordToValue(x + 1, y);
        int westIndex = coordToValue(x-1, y);
        int southIndex = coordToValue(x, y + 1);
        int northFactor = 0, eastFactor = 0, southFactor = 0, westFactor = 0;

        if ((tiles[indexAtXY] == Tile.GRASS.getId()) || (tiles[indexAtXY] == Tile.STONE.getId())) {
            if ((tiles[indexAtXY] == tiles[northIndex]) || ifTileAtIndexIsSameTypeAsLinkage(northIndex, indexAtXY)) {
                northFactor = 1;
            }
            if ((tiles[indexAtXY] == tiles[eastIndex]) || ifTileAtIndexIsSameTypeAsLinkage(eastIndex, indexAtXY)) {
                eastFactor = 1;
            }
            if ((tiles[indexAtXY] == tiles[southIndex]) || ifTileAtIndexIsSameTypeAsLinkage(southIndex, indexAtXY)) {
                southFactor = 1;
            }
            if ((tiles[indexAtXY] == tiles[westIndex]) || ifTileAtIndexIsSameTypeAsLinkage(westIndex, indexAtXY)) {
                westFactor = 1;
            }
        }

        return 1*northFactor + 2*westFactor + 4*eastFactor + 8*southFactor;
    }

    private void saveLevelToFile() {
        try {
            ImageIO.write(image, "png", new File(Level.class.getResource(this.imagePath).getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alterTile(int x, int y, Tile newTile) {
        this.map.addTileValue("Tiles",x + y * width, newTile.getId());
        image.setRGB(x,y, newTile.getLevelColour());
    }

    public void generateLevel() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x * y % 10 < 7) {
                    this.map.addTileValue("Tiles",x + y * width, Tile.GRASS.getId());
                } else {
                    this.map.addTileValue("Tiles",x + y * width, Tile.STONE.getId());
                }

            }
        }
    }

    public void tick() {
        for (Entity e : entities) {
            e.tick();
        }

        for (Tile t : Tile.tiles) {
            if (t == null) {
                break;
            } else {
                t.tick();
            }
        }
    }

    /*public boolean ifTileIsSameTypeAs(Tile tile, List<Tile> tiles){
        if (tiles.size() == 0) return false;
        //System.out.println("Length:"+tiles.size());
        for (Tile t : tiles) {
            //System.out.println("ID/"+t);
            if (t.getId() != tile.getId()) {
                return false;
            }
        }
        return true;
    }

    public int getBitmaskValue(Tile center, Tile north, Tile east, Tile south, Tile west) {
        int northFactor = 0, eastFactor = 0, southFactor = 0, westFactor = 0;
        if ( ( center.getId() == Tile.GRASS.getId()) || (center.getId() == Tile.STONE.getId())) {
            if (north.getLevelColour() == center.getLevelColour() || ifTileIsSameTypeAs(north, ((BasicTile)center).getAcceptedLinkage())) {
                northFactor = 1;
            }
            if (east.getLevelColour() == center.getLevelColour() || ifTileIsSameTypeAs(east, ((BasicTile)center).getAcceptedLinkage())) {
                eastFactor = 1;
            }
            if (south.getLevelColour() == center.getLevelColour() || ifTileIsSameTypeAs(south, ((BasicTile)center).getAcceptedLinkage())) {
                southFactor = 1;
            }
            if (west.getLevelColour() == center.getLevelColour() || ifTileIsSameTypeAs(west, ((BasicTile)center).getAcceptedLinkage())) {
                westFactor = 1;
            }
        }

        // See https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
        return 1*northFactor + 2*westFactor + 4*eastFactor + 8*southFactor;
    }

    int bitmask;*/

    public void renderTiles(Screen screen, int xOffset, int yOffset) {
        if (xOffset < 0) xOffset = 0;
        if (xOffset > ((width << 3) - screen.width)) xOffset = ((width << 3) - screen.width);
        if (yOffset < 0) yOffset = 0;
        if (yOffset > ((height << 3) - screen.height)) yOffset = ((height << 3) - screen.height);

        if(screen.width > width * 8)
            xOffset = (screen.width - (width * 8)) / 2 * -1;
        if(screen.height > height * 8)
            yOffset = (screen.height - (height * 8)) / 2 * -1;
        screen.setOffset(xOffset, yOffset);

        for (int y = (yOffset >> 3); y < (yOffset + screen.height >> 3) + 1; y++) {
            for (int x = (xOffset >> 3); x < (xOffset + screen.width >> 3) + 1; x++) {
                // = getBitmaskValue(getTile(x,y) ,getTile(x,y-1), getTile(x+1,y), getTile(x,y+1), getTile(x-1,y));
                //((BasicTile)getTile(x,y)).addBitmaskValue(bitmask);
                //System.out.println("X:"+x + " Y:" + y + " Colour:" + getTile(x,y).getLevelColour() + " Bitmask:"+bitmask);
                //System.out.println(Arrays.toString(((BasicTile) getTile(x, y)).getAcceptedBitmaskTiles()));
                //((BasicTile)Tile.tiles[this.map.getTile(x+y*width)]).setBitmaskValue(this.map.getBitmask(x+y*width));
                getTile("Tiles",x,y).render(screen, this, x << 3 , y << 3);
            }
        }
    }

    public void renderEntities(Screen screen) {
        for (Entity e : entities) {
            e.render(screen);
        }
    }

    public Tile getTile(String layerName, int x, int y) {
        if (0 > x || x >= width || 0 > y || y >= height) return Tile.VOID;
        ((BasicTile)Tile.tiles[this.map.getTile(layerName,x+y*width)]).setBitmaskValue(this.map.getBitmask(layerName,x+y*width));
        return Tile.tiles[this.map.getTile(layerName,x+y*width)];
    }

    public synchronized void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public synchronized void removePlayerMP(String username) {
        int index = 0;

        for (Entity e : entities) {
            if (e instanceof PlayerMP && ((PlayerMP) e).getUsername().equals(username)) {
                break;
            }
            index++;
        }
        this.entities.remove(index);
    }

    private int getPlayerMPIndex(String username) {
        int index = 0;
        for (Entity e : this.entities) {
            if (e instanceof PlayerMP && ((PlayerMP) e).getUsername().equals(username)) {
                break;
            }
            index++;
        }
        return index;
    }

    public synchronized void movePlayer(String username, int x, int y, int numSteps, boolean isMoving, int movingDir) {
        int index = getPlayerMPIndex(username);
        PlayerMP player = (PlayerMP) this.entities.get(index);
        player.x = x;
        player.y = y;
        player.setMoving(isMoving);
        player.setNumSteps(numSteps);
        player.setMovingDir(movingDir);
    }
}
