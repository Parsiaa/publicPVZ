package models.Rules;

import models.LevelRule;
import models.MatchState;

public class DeadLineRule implements LevelRule {
    private double deadLineX;

    public DeadLineRule(double deadLineX) {
        this.deadLineX = deadLineX;
    }
    public double getDeadLineX() {
        return deadLineX;
    }
    public void setDeadLineX(double deadLineX) {
        this.deadLineX = deadLineX;
    }
    public boolean checkLossCondition() { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public String getRuleInfo() { return null; }
    @Override
    public void onTick(MatchState state) {}
}