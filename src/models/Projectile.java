package models;

import java.util.List;

import models.Enums.DamageType;
import models.Enums.PlantTag;

public class Projectile extends Entity{
    private int damage;
    private int isThrown;
    private int pierceCount;
    private List<PlantTag> effectTags;
    public Projectile(int damage, int isThrown, int pierceCount, List<PlantTag> effectTags) {
        this.damage = damage;
        this.isThrown = isThrown;
        this.pierceCount = pierceCount;
        this.effectTags = effectTags;
    }
    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getIsThrown() {
        return isThrown;
    }
    public void setIsThrown(int isThrown) {
        this.isThrown = isThrown;
    }
    public int getPierceCount() {
        return pierceCount;
    }
    public void setPierceCount(int pierceCount) {
        this.pierceCount = pierceCount;
    }
    public List<PlantTag> getEffectTags() {
        return effectTags;
    }
    public void setEffectTags(List<PlantTag> effectTags) {
        this.effectTags = effectTags;
    }
    public void move() {
        //Todo
    }
    public void hitTarget(Zombie zombie) {
        if (zombie == null || zombie.getCurrentHealth() <= 0) {
            return;
        }
        DamageType type = resolveDamageType();
        int finalDamage = (type == DamageType.FIRE) ? damage * 2 : damage;
        zombie.takeDamage(finalDamage, type);
        applyEffect(zombie);
    }
    public void applyEffect(Zombie zombie) {
        // Chill (ICE) and thaw (FIRE) are applied inside Zombie.takeDamage based on the DamageType.
        // Extra on-hit effects for future plants can be added here.
    }
    private DamageType resolveDamageType() {
        if (effectTags != null) {
            if (effectTags.contains(PlantTag.FIRE)) {
                return DamageType.FIRE;
            }
            if (effectTags.contains(PlantTag.ICE)) {
                return DamageType.ICE;
            }
            if (effectTags.contains(PlantTag.POISON)) {
                return DamageType.POISON;
            }
        }
        return DamageType.NORMAL;
    }
}
