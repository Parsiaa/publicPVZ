package models.Blueprints;
import java.util.List;

public class ArmorData {
    private String alias;
    private String armorType;
    private int baseHealth;
    private List<String> armorFlags;

    public boolean hasFlag(String flag) {
        return armorFlags != null && armorFlags.contains(flag);
    }

    public int getBaseHealth() { return baseHealth; }
    public String getAlias() { return alias; }
}
