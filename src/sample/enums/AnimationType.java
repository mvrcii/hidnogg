package sample.enums;


public enum AnimationType {
    PLAYER_IDLE_LOW(10),
    PLAYER_IDLE_MEDIUM(10),
    PLAYER_IDLE_HIGH(10);


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