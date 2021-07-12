package com.vykio.game.level.tiles;

import com.vykio.game.gfx.Screen;
import com.vykio.game.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BasicTile extends Tile {

    protected int tileId;
    protected int tileColour;

    protected int x;
    protected int y;

    protected int xOrigin;
    protected int yOrigin;

    protected int[][] bitmask;
    protected List<Tile> acceptedBitmaskTiles;
    public int bitmaskValue;

    public BasicTile(int id, int x, int y, int tileColour, int levelColour) {
        super(id, false, false, levelColour);
        this.x = x;
        this.y = y;
        this.xOrigin = x;
        this.yOrigin = y;
        this.tileId = x + y * 32; //32 tiles width on spritesheet
        this.tileColour = tileColour;
        this.acceptedBitmaskTiles = new ArrayList<Tile>();
        this.bitmaskValue = 0;
    }

    public BasicTile(int id, int x, int y, int tileColour, int levelColour, int[][] bitmask) {
        this(id, x,y, tileColour, levelColour);
        this.bitmask = bitmask;
    }

    public void addBitmaskLinkage(ArrayList<Tile> tilesToAdd) {
        this.acceptedBitmaskTiles.addAll(tilesToAdd);
    }

    public void addBitmaskValue(int bitmask) {
        this.bitmaskValue = bitmask;
    }

    @Override
    public void tick() {

    }

    public void setBitmaskValue(int bitmaskValue) {
        this.bitmaskValue = bitmaskValue;
        if (this.bitmaskValue > 0
                && (
                this.getId() == Tile.STONE.getId() ||
                        this.getId() == Tile.GRASS.getId()
        )) {

            this.x = this.bitmask[this.bitmaskValue][0];
            this.y = this.bitmask[this.bitmaskValue][1];

            //this.tileId = this.x + this.y * 32;
            //System.out.println("ID/ " +this.x + ":" + this.y);
        }
    }

    public void render(Screen screen, Level level, int x, int y) {

        screen.render(x,y, this.x+this.y*32, tileColour, 0x00, 1);
    }

    public List<Tile> getAcceptedLinkage() {
        return acceptedBitmaskTiles;
    }
}
