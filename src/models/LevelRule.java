package models;


public interface LevelRule {
    boolean checkWinCondition(MatchState state);
    boolean checkLossCondition(MatchState state);
    String getRuleInfo();
    void onTick(MatchState state);

    /** Called once when the match starts, before the first tick. */
    default void onMatchStart(MatchState state) {
    }
}

