package sample.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(90),
    PLAYER_IDLE_MEDIUM(90),
    PLAYER_IDLE_HIGH(90),
    PLAYER_IDLE_HOLD_UP(90),

    PLAYER_JUMP_START(60),
    PLAYER_JUMP_PEAK(60),
    PLAYER_JUMP_END(60),

    PLAYER_STAB_LOW(100),
    PLAYER_STAB_MEDIUM(100),
    PLAYER_STAB_HIGH(100),

    PLAYER_WALK(30),
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