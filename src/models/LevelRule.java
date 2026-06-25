package models;


public interface LevelRule {
    boolean checkWinCondition(MatchState state);
    boolean checkLossCondition(MatchState state);
    String getRuleInfo();
    void onTick(MatchState state);
}

