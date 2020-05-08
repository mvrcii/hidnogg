package sample.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(30),
    PLAYER_IDLE_MEDIUM(20),
    PLAYER_IDLE_HIGH(20),
    PLAYER_IDLE_HOLD_UP(20),
    SWORD(8);

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