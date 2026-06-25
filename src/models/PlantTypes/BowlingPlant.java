package models.PlantTypes;

import models.Plant;
import models.MatchState;
import models.Zombie;

public class BowlingPlant extends Plant {
    private int moveAngle;
    private boolean isGiant;
    private boolean isExplosive;


    @Override
    public void act(MatchState state) {
        //TODO
    }
    public void handleCollision(Zombie zombie){
        //TODO
    }

    public int getMoveAngle() {return moveAngle;}
    public void setMoveAngle(int moveAngle) {this.moveAngle = moveAngle;}
    public boolean isGiant() {return isGiant;}
    public void setGiant(boolean giant) { isGiant = giant;}
    public boolean isExplosive() {return isExplosive;}
    public void setExplosive(boolean explosive) {isExplosive = explosive;}
}
