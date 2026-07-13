package models.Rules;

import models.LevelRule;
import models.MatchState;

/**
 * Timed War level: kill the target number of zombies before the timer ends.
 */
public class TimedWarRule implements LevelRule {

    private final int durationSeconds;
    private final int targetKills;

    public TimedWarRule(int durationSeconds, int targetKills) {
        this.durationSeconds = durationSeconds;
        this.targetKills = targetKills;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getTargetKills() {
        return targetKills;
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return state.getKilledZombiesCount() >= targetKills;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        return state.getCurrentTick() / 10.0 >= durationSeconds
                && state.getKilledZombiesCount() < targetKills;
    }

    @Override
    public void onTick(MatchState state) {
        if (state.getCurrentTick() % 100 == 0) {
            int remaining = durationSeconds - state.getCurrentTick() / 10;
            System.out.println("Timed War: " + state.getKilledZombiesCount() + "/" + targetKills
                    + " zombies killed, " + remaining + "s left.");
        }
    }

    @Override
    public String getRuleInfo() {
        return "Timed War level: kill " + targetKills + " zombies within " + durationSeconds + " seconds.";
    }
}
