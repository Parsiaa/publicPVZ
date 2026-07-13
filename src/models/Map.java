package models;

import java.util.ArrayList;
import java.util.List;
import models.Enums.TileType;

public class Map {
    private Tile[][] grid;
    private int currentTileColumn;
    private boolean[] lawnMowers;
    private int rows;
    private int columns;

    public Map() {
        this(5, 9);
    }

    public Map(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new Tile[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                grid[r][c] = new Tile(r, c, TileType.NORMAL);
            }
        }
        this.lawnMowers = new boolean[rows];
        for (int r = 0; r < rows; r++) {
            lawnMowers[r] = true;
        }
        this.currentTileColumn = 0;
    }

    public Tile getTile(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return null;
        }
        return grid[row][column];
    }

    public void setTileColumn(int currentTileColumn) {
        this.currentTileColumn = currentTileColumn;
    }

    public int getCurrentTileColumn() {
        return currentTileColumn;
    }

    public boolean hasLawnMower(int row) {
        return row >= 0 && row < rows && lawnMowers[row];
    }

    public boolean triggerLawnMower(int row) {
        if (row < 0 || row >= rows || !lawnMowers[row]) {
            return false;
        }
        lawnMowers[row] = false;
        List<Zombie> killed = new ArrayList<>(getZombiesInRow(row));
        System.out.println("The lawn mower in the row " + row + " is triggered and killed these zombies:");
        for (Zombie zombie : killed) {
            System.out.println("- " + zombie.getTypeName());
            zombie.setCurrentHealth(0);
            zombie.setHealth(0);
            removeZombie(zombie);
        }
        return true;
    }

    public void addPlant(Plant plant, int row, int column) {
        Tile tile = getTile(row, column);
        if (tile != null) {
            plant.setX(column);
            plant.setY(row);
            tile.addEntity(plant);
        }
    }

    public void addZombie(Zombie zombie, int row, double x) {
        zombie.setX(x);
        zombie.setY(row);
        Tile tile = getTile(row, (int) Math.floor(Math.min(columns - 1, Math.max(0, x))));
        if (tile != null) {
            tile.addEntity(zombie);
        }
    }

    public void removeZombie(Zombie zombie) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                grid[r][c].getZombies().remove(zombie);
            }
        }
    }

    public void removePlant(Plant plant) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                grid[r][c].getPlants().remove(plant);
            }
        }
    }

    public List<Zombie> getZombiesInRow(int row) {
        List<Zombie> result = new ArrayList<>();
        if (row < 0 || row >= rows) {
            return result;
        }
        for (int c = 0; c < columns; c++) {
            result.addAll(grid[row][c].getZombies());
        }
        return result;
    }

    public List<Plant> getPlantsInRow(int row) {
        List<Plant> result = new ArrayList<>();
        if (row < 0 || row >= rows) {
            return result;
        }
        for (int c = 0; c < columns; c++) {
            result.addAll(grid[row][c].getPlants());
        }
        return result;
    }

    public List<Zombie> getAllZombies() {
        List<Zombie> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            result.addAll(getZombiesInRow(r));
        }
        return result;
    }

    public List<Plant> getAllPlants() {
        List<Plant> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            result.addAll(getPlantsInRow(r));
        }
        return result;
    }

    public Zombie getFirstZombieAhead(int row, double x) {
        Zombie first = null;
        for (Zombie zombie : getZombiesInRow(row)) {
            if (zombie.getX() >= x && zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()) {
                if (first == null || zombie.getX() < first.getX()) {
                    first = zombie;
                }
            }
        }
        return first;
    }

    /** Nearest live enemy zombie behind the given x in a row (used by Split Pea / Starfruit). */
    public Zombie getFirstZombieBehind(int row, double x) {
        Zombie nearest = null;
        for (Zombie zombie : getZombiesInRow(row)) {
            if (zombie.getX() < x && zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()) {
                if (nearest == null || zombie.getX() > nearest.getX()) {
                    nearest = zombie;
                }
            }
        }
        return nearest;
    }

    /** Whether a plant marked as a pea-igniter (Torchwood) sits between the shooter and the target. */
    public boolean hasIgniterBetween(int row, double fromX, double toX) {
        for (Plant plant : getPlantsInRow(row)) {
            if (!plant.isDead() && plant.ignitesPeas() && plant.getX() > fromX && plant.getX() <= toX) {
                return true;
            }
        }
        return false;
    }

    public Plant getFrontPlantForZombie(int row, double zombieX) {
        Plant front = null;
        for (Plant plant : getPlantsInRow(row)) {
            if (!plant.isDead() && plant.getX() <= zombieX && zombieX - plant.getX() <= 0.5) {
                if (front == null || plant.getX() > front.getX()) {
                    front = plant;
                }
            }
        }
        return front;
    }

    public void moveZombieToTile(Zombie zombie) {
        removeZombie(zombie);
        int row = (int) zombie.getY();
        int col = (int) Math.floor(Math.min(columns - 1, Math.max(0, zombie.getX())));
        Tile tile = getTile(row, col);
        if (tile != null) {
            tile.addEntity(zombie);
        }
    }

    /** First grave tile strictly between fromX and toX in this row (blocks straight shots). */
    public Tile getBlockingTileAhead(int row, double fromX, double toX) {
        for (int c = (int) Math.floor(fromX) + 1; c < columns && c <= toX; c++) {
            Tile tile = getTile(row, c);
            if (tile != null && tile.blocksProjectiles()) {
                return tile;
            }
        }
        return null;
    }

    public void disableLawnMowers() {
        for (int r = 0; r < rows; r++) {
            lawnMowers[r] = false;
        }
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
}
