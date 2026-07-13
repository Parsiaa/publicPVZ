package models.Blueprints;
import java.util.List;

public class ArmorData {
    private String alias;
    private String armorType;
    private int baseHealth;
    private List<String> armorFlags;

    public ArmorData(String alias, String armorType, int baseHealth, List<String> armorFlags) {
        this.alias = alias;
        this.armorType = armorType;
        this.baseHealth = baseHealth;
        this.armorFlags = armorFlags;
    }

    public boolean hasFlag(String flag) {
        return armorFlags != null && armorFlags.contains(flag);
    }

    public int getBaseHealth() { return baseHealth; }
    public String getAlias() { return alias; }
    public String getArmorType() { return armorType; }
    public List<String> getArmorFlags() { return armorFlags; }
}
