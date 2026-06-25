package models.Rules;

import models.LevelRule;
import models.MatchState;

public class TimedWarRule implements LevelRule {
    private double timeRemaining;
    private int requiredKills;
    private int currentKills;

    public TimedWarRule(double timeRemaining, int requiredKills, int currentKills) {
        this.timeRemaining = timeRemaining;
        this.requiredKills = requiredKills;
        this.currentKills = currentKills;
    }
    public double getTimeRemaining() {
        return timeRemaining;
    }
    public void setTimeRemaining(double timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
    public int getRequiredKills() {
        return requiredKills;
    }
    public void setRequiredKills(int requiredKills) {
        this.requiredKills = requiredKills;
    }
    public int getCurrentKills() {
        return currentKills;
    }
    public void setCurrentKills(int currentKills) {
        this.currentKills = currentKills;
    }
    public boolean checkWinCondition() { return false; }
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    
    public boolean checkLossCondition() { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    
    @Override
    public String getRuleInfo() { return null; }
    @Override
    public void onTick(MatchState state) {}
}
