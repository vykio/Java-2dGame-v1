package com.vykio.game.level.tiles;

import com.vykio.game.gfx.Colours;
import com.vykio.game.gfx.Screen;
import com.vykio.game.level.Level;

import java.util.ArrayList;

public abstract class Tile {

    public static final Tile[] tiles = new Tile[256];

    public static Tile VOID;
    public static Tile STONE;
    public static Tile WATER;
    public static Tile GRASS;

    public static void initTiles() {
        VOID = new BasicSolidTile(0, 0,0, Colours.get(000,-1,-1,-1), 0xFF000000);
        STONE = new BasicSolidTile(1, 1,0, Colours.get(-1,333,222,131), 0xFF555555, new int [][] {{1,1}, {1,2}, {1,3}, {1,4}, {1,5}, {1,6}, {1,7}, {1,8}, {1,9}, {1,10}, {1,11}, {1,12}, {1,13}, {1,14}, {1,15}, {1,16}});
        GRASS = new BasicTile(2, 2, 0, Colours.get(-1,131,141,004), 0xFF00FF00, new int [][] {{2,1}, {2,2}, {2,3}, {2,4}, {2,5}, {2,6}, {2,7}, {2,8}, {2,9}, {2,10}, {2,11}, {2,12}, {2,13}, {2,14}, {2,15}, {2,16}});
        WATER = new AnimatedTile(3, new int[][] {{0,17}, {1,17}, {2,17}, {1,17}}, Colours.get(-1,004,115,-1), 0xFF0000FF,500);


        ((BasicTile)GRASS).addBitmaskLinkage(new ArrayList<>() { {add(STONE);} }); // la GRASS le lie à STONE
    }

    protected byte id;
    protected boolean solid;
    protected boolean emitter;

    private int levelColour;

    public Tile(int id, boolean isSolid, boolean isEmitter, int levelColour){
        this.id = (byte) id;
        if (tiles[id] != null) throw new RuntimeException("Duplicate Tile ID on : " + id);
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.levelColour = levelColour;
        tiles[id] = this;
    }

    public byte getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public int getLevelColour() { return levelColour; }

    public abstract void tick();

    public abstract void render(Screen screen, Level level, int x, int y);

}
