package com.vykio.game.level.tiles;

public class BasicSolidTile extends BasicTile {

    public BasicSolidTile(int id, int x, int y, int tileColour, int levelColour) {
        super(id, x, y, tileColour, levelColour);
        this.solid = true;
    }

    public BasicSolidTile(int id, int x, int y, int tileColour, int levelColour, int[][] bitmask) {
        this(id, x,y,tileColour, levelColour);
        this.bitmask = bitmask;
    }
}
