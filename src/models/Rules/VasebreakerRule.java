package models.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.LevelRule;
import models.MatchState;
import models.SeedPacket;
import models.Vase;
import models.Zombie;
import utils.PlantFactory;
import utils.ZombieFactory;

/**
 * Vasebreaker mini-game: break every vase on the yard while surviving the
 * zombies hidden inside them. Some vases hold one-shot seed packets instead.
 */
public class VasebreakerRule implements LevelRule {

    private static final double PACKET_LIFETIME_SECONDS = 15;

    private final int stage;
    private final Random random;
    private final List<Vase> vases = new ArrayList<>();
    private boolean anyVaseBroken;

    public VasebreakerRule(int stage, Random random) {
        this.stage = stage;
        this.random = random;
    }

    @Override
    public void onMatchStart(MatchState state) {
        state.getMap().disableLawnMowers();
        int count = 4 + stage * 4;
        while (vases.size() < count) {
            int row = random.nextInt(state.getMap().getRows());
            int column = 4 + random.nextInt(state.getMap().getColumns() - 4);
            if (vaseAt(column, row) == null) {
                vases.add(createVase(column, row));
            }
        }
        System.out.println(vases.size() + " vases stand on your yard. Break them all!");
    }

    private Vase createVase(int column, int row) {
        String vaseType = "normal";
        String content = rollContent();
        double specialRoll = random.nextDouble();
        if (specialRoll < 0.1) {
            vaseType = "plant";
            content = "packet:" + randomPlantName();
        } else if (specialRoll < 0.15 + stage * 0.05) {
            vaseType = "gargantuar";
            content = "zombie:Gargantuar";
        }
        Vase vase = new Vase(content, vaseType);
        vase.setX(column);
        vase.setY(row);
        return vase;
    }

    private String rollContent() {
        double roll = random.nextDouble();
        if (roll < 0.55) {
            return "zombie:" + (random.nextBoolean() ? "Normal" : "ConeHead");
        }
        if (roll < 0.85) {
            return "packet:" + randomPlantName();
        }
        return "empty";
    }

    private String randomPlantName() {
        List<String> pool = PlantFactory.getAllPlantNames();
        return pool.get(random.nextInt(Math.min(10, pool.size())));
    }

    /** Breaks the vase on the given 0-based tile; returns a message or null if no vase is there. */
    public String breakVase(MatchState state, int column, int row) {
        Vase vase = vaseAt(column, row);
        if (vase == null) {
            return null;
        }
        vases.remove(vase);
        anyVaseBroken = true;
        String content = vase.getContentType();
        if (content.startsWith("zombie:")) {
            String type = content.substring("zombie:".length());
            Zombie zombie = ZombieFactory.createZombie(type, state.getDifficultyLevel());
            state.getMap().addZombie(zombie, row, column);
            return "The vase held a " + type + " zombie!";
        }
        if (content.startsWith("packet:")) {
            String plant = content.substring("packet:".length());
            SeedPacket packet = new SeedPacket(plant, 0, 0);
            packet.setExpirationTimer(PACKET_LIFETIME_SECONDS);
            state.getSeedPackets().add(packet);
            return "The vase held a free " + plant + " seed packet! Plant it before it vanishes.";
        }
        return "The vase was empty.";
    }

    public Vase vaseAt(int column, int row) {
        for (Vase vase : vases) {
            if ((int) vase.getX() == column && (int) vase.getY() == row) {
                return vase;
            }
        }
        return null;
    }

    public List<Vase> getVases() {
        return vases;
    }

    @Override
    public void onTick(MatchState state) {
        List<SeedPacket> packets = state.getSeedPackets();
        for (SeedPacket packet : new ArrayList<>(packets)) {
            if (packet.getExpirationTimer() > 0) {
                packet.setExpirationTimer(packet.getExpirationTimer() - 0.1);
                if (packet.getExpirationTimer() <= 0) {
                    packets.remove(packet);
                    System.out.println("The seed packet of " + packet.getPlantName() + " vanished!");
                }
            }
        }
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return anyVaseBroken && vases.isEmpty() && state.getMap().getAllZombies().isEmpty();
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        return false;
    }

    @Override
    public String getRuleInfo() {
        return "Vasebreaker (stage " + stage + "): break every vase with 'break vase -l (x, y)'"
                + " and survive what is inside.";
    }
}
