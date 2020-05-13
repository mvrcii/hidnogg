package sample.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(200),
    PLAYER_IDLE_MEDIUM(200),
    PLAYER_IDLE_HIGH(200),
    PLAYER_IDLE_HOLD_UP(200),
    SWORD(200);

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