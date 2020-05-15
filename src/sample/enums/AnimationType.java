package sample.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(150),
    PLAYER_IDLE_MEDIUM(150),
    PLAYER_IDLE_HIGH(150),
    PLAYER_IDLE_HOLD_UP(150),
    SWORD(150);

    private int amount;

    AnimationType(int amount)
    {
        this.amount = amount;
    }

    public int getDuration()
    {
        return amount;
    }
}