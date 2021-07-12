package com.vykio.game.level.tiles;

import java.util.HashMap;

public class CMap {

    HashMap<String, Layer> map;

    class Layer {
        public byte tiles[];
        public int bitmask[];

        public Layer(byte[] tiles, int[] bitmask) {
            this.tiles = tiles;
            this.bitmask = bitmask;
        }
    }


    public CMap(int size) {
        this.map = new HashMap<>();

        Layer tilesLayer = new Layer(new byte[size], new int[size]);
        this.map.put("Tiles", tilesLayer);

    }

    public void addTileValue(String layerName, int position, byte addedByte) {
        if (position < this.map.get(layerName).tiles.length) {
            this.map.get(layerName).tiles[position] = addedByte;
        }
    }

    public void addBitmaskValue(String layerName, int position, int addedBitmaskValue) {
        if (position < this.map.get(layerName).bitmask.length) {
            this.map.get(layerName).bitmask[position] = addedBitmaskValue;
        }
    }

    public byte getTile(String layerName, int position) {
        return this.map.get(layerName).tiles[position];
    }

    public int getBitmask(String layerName, int position) {
        return this.map.get(layerName).bitmask[position];
    }

    public byte[] getTiles(String layerName) {
        return this.map.get(layerName).tiles;
    }

    public int[] getBitmask(String layerName) {
        return this.map.get(layerName).bitmask;
    }
}
