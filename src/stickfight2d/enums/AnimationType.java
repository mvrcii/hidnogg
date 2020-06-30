package stickfight2d.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(90),
    PLAYER_IDLE_MEDIUM(90),
    PLAYER_IDLE_HIGH(90),
    PLAYER_IDLE_HOLD_UP(90),

    PLAYER_JUMP_START(60),
    PLAYER_JUMP_PEAK(60),
    PLAYER_JUMP_END(60),

    PLAYER_STAB_LOW(60),
    PLAYER_STAB_MEDIUM(60),
    PLAYER_STAB_HIGH(60),

    PLAYER_DYING(60),

    PLAYER_WALK(30),
    PLAYER_STEP_LOW(30),
    PLAYER_STEP_MEDIUM(30),
    PLAYER_STEP_HIGH(30),

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