package com.vykio.game.entities;

import com.vykio.game.level.Level;

public abstract class Mob extends Entity {

    protected String name;
    public int speed;
    protected int numSteps = 0;
    protected boolean isMoving;
    protected int movingDir = 1; // 0 : top, 1: down, 2: left, 3: right
    protected int scale = 1;

    public Mob(Level level, String name, int x, int y, int speed) {
        super(level);
        this.name = name;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move(int xa, int ya) {
        /*if (xa != 0 && ya != 0) {
            System.out.println("Move: " + xa + " : " + ya);
            move(xa,0);
            move(ya, 0);
            numSteps--;
            return;
        }*/
        numSteps++;
        if (ya < 0) movingDir = 0;
        if (ya > 0) movingDir = 1;
        if (xa < 0) movingDir = 2;
        if (xa > 0) movingDir = 3;
        if (!hasCollided(xa,ya)) {
            int modifier = (int) Math.round(speed / Math.sqrt(2*speed));
            //System.out.println(modifier);
            x += xa * modifier;
            y += ya * modifier;
        }
    }

    public abstract boolean hasCollided(int xa, int ya);
    public String getName() {
        return name;
    }

}
