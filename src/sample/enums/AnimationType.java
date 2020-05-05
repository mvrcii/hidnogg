package sample.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(12),
    PLAYER_IDLE_MEDIUM(12),
    PLAYER_IDLE_HIGH(12),
    SWORD(12);

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