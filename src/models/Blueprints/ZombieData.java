package models.Blueprints;

import java.util.List;
import java.util.Map;

public class ZombieData {
    private String alias;
    private String objClass;
    private int hitpoints;
    private double speed;
    private int eatDps;
    private List<String> armorAliases;
    private Map<String, Object> specialProps;

    public ZombieData(String alias, String objClass, int hitpoints, double speed, int eatDps,
                      List<String> armorAliases, Map<String, Object> specialProps) {
        this.alias = alias;
        this.objClass = objClass;
        this.hitpoints = hitpoints;
        this.speed = speed;
        this.eatDps = eatDps;
        this.armorAliases = armorAliases;
        this.specialProps = specialProps;
    }

    public String getAlias() {
        return alias;
    }

    public String getObjClass() {
        return objClass;
    }

    public List<String> getArmorAliases() {
        return armorAliases;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public int getEatDps() {
        return eatDps;
    }

    public double getSpeed() {
        return speed;
    }

    public Object getSpecialProps(String key) {
        return specialProps != null ? specialProps.get(key) : null;
    }
}
