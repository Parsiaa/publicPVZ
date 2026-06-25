package models;

import java.util.ArrayList;
import java.util.List;

public class Wave {
    private List<Zombie> waveZombies;
    private boolean isStarted;

    public Wave() {
        this.waveZombies = new ArrayList<>();
        this.isStarted = false;
    }

    public void addZombie(Zombie zombie) {
        this.waveZombies.add(zombie);
    }
    public void startWave() {
        this.isStarted = true;
    }
    public boolean isCompletelyDefeated() {
        if (!isStarted) {
            return false;
        }
        for (Zombie z : waveZombies) {
            if (z.currentHealth > 0) {
                return false;
            }
        }
        return true;
    }

    public List<Zombie> getWaveZombies() { return waveZombies; }
    public void setWaveZombies(List<Zombie> waveZombies) { this.waveZombies = waveZombies; }
    public boolean isStarted() { return isStarted; }
}
