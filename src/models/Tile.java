package models;

import java.util.ArrayList;
import java.util.List;
import models.Enums.ObstacleType;
import models.Enums.TileType;

public class Tile {
    private int row;
    private int column;
    private TileType type;
    private List<Plant> plants;
    private List<Zombie> zombies;
    private int tileHealth;
    private boolean hasCrater;
    private ObstacleType graveLoot;

    public Tile(int row, int column, TileType type) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.plants = new ArrayList<>();
        this.zombies = new ArrayList<>();
        this.hasCrater = false;
        if (type == TileType.GRAVE) {
            this.tileHealth = 700;
        } else if (type == TileType.ICE) {
            this.tileHealth = 600;
        } else {
            this.tileHealth = 0;
        }
    }

    /** Damages a grave or ice tile; returns true if it was destroyed by this hit. */
    public boolean takeTileDamage(int amount) {
        if (type != TileType.GRAVE && type != TileType.ICE) {
            return false;
        }
        tileHealth = Math.max(0, tileHealth - amount);
        if (tileHealth == 0) {
            if (type == TileType.ICE) {
                meltIce();
            } else {
                this.type = TileType.NORMAL;
            }
            return true;
        }
        return false;
    }

    public void meltIce() {
        if (type == TileType.ICE) {
            this.type = TileType.NORMAL;
            this.tileHealth = 0;
        }
    }

    public void addEntity(Entity entity) {
        if (entity instanceof Plant) {
            plants.add((Plant) entity);
        } else if (entity instanceof Zombie) {
            zombies.add((Zombie) entity);
        }
    }

    public void removeEntity(Entity entity) {
        if (entity instanceof Plant) {
            plants.remove(entity);
        } else if (entity instanceof Zombie) {
            zombies.remove(entity);
        }
    }

    public boolean isPlantable() {
        return (type == TileType.NORMAL || type == TileType.SHALLOW || type == TileType.NECROMANCY) && !hasCrater;
    }

    public boolean blocksProjectiles() {
        return type == TileType.GRAVE;
    }

    public Plant getTopPlant() {
        if (plants.isEmpty()) {
            return null;
        }
        return plants.get(plants.size() - 1);
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    public TileType getType() { return type; }
    public void setType(TileType type) {
        this.type = type;
        if (type == TileType.GRAVE) {
            this.tileHealth = 700;
        } else if (type == TileType.ICE) {
            this.tileHealth = 600;
        }
    }

    public List<Plant> getPlants() { return plants; }
    public List<Zombie> getZombies() { return zombies; }

    public int getTileHealth() { return tileHealth; }
    public void setTileHealth(int tileHealth) { this.tileHealth = tileHealth; }

    public boolean hasCrater() { return hasCrater; }
    public void setHasCrater(boolean hasCrater) { this.hasCrater = hasCrater; }

    public ObstacleType getGraveLoot() { return graveLoot; }
    public void setGraveLoot(ObstacleType graveLoot) { this.graveLoot = graveLoot; }
}
