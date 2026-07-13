package models.Rules;

import models.LevelRule;
import models.MatchState;
import models.Zombie;

/**
 * Deadline level: the moment any zombie crosses the vertical line, the level is lost.
 */
public class DeadLineRule implements LevelRule {

    private final int deadlineColumn;

    public DeadLineRule(int deadlineColumn) {
        this.deadlineColumn = deadlineColumn;
    }

    public int getDeadlineColumn() {
        return deadlineColumn;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && zombie.getX() < deadlineColumn) {
                System.out.println("A zombie crossed the deadline at column " + (deadlineColumn + 1) + "!");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return false;
    }

    @Override
    public void onTick(MatchState state) {
    }

    @Override
    public String getRuleInfo() {
        return "Deadline level: you lose instantly if a zombie passes column " + (deadlineColumn + 1) + ".";
    }
}
