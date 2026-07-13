package models;

/**
 * A timed status effect on a zombie (chill, freeze, butter, poison, ...).
 * The remaining time is tracked in in-game seconds so it can be shown in
 * the {@code zombies info} output and decays as ticks advance.
 */
public class Effect {

    private final String name;
    private double remainingSeconds;
    private final double magnitude;

    public Effect(String name, double durationSeconds, double magnitude) {
        this.name = name;
        this.remainingSeconds = durationSeconds;
        this.magnitude = magnitude;
    }

    /** Advances the effect by one tick (0.1s). */
    public void tick() {
        remainingSeconds -= 0.1;
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

    /** Keeps the longer of the current and the newly applied duration. */
    public void refresh(double durationSeconds) {
        remainingSeconds = Math.max(remainingSeconds, durationSeconds);
    }

    public String getName() {
        return name;
    }

    public double getRemainingSeconds() {
        return remainingSeconds;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
