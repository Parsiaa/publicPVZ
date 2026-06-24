package models.Rules;

import models.LevelRule;
import models.MatchState;

public class PlantWhatYouGetRule implements LevelRule {
    private int startingSun;
    private int areZombiesReleased;

    public int getStartingSun() {
        return startingSun;
    }

    public void setStartingSun(int startingSun) {
        this.startingSun = startingSun;
    }

    public int getAreZombiesReleased() {
        return areZombiesReleased;
    }

    public void setAreZombiesReleased(int areZombiesReleased) {
        this.areZombiesReleased = areZombiesReleased;
    }

    @Override
    public String getRuleInfo() { return null; }
    
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    @Override
    public void onTick(MatchState state) {}
}
