public class Card
{
    private Suit suit;
    private int value;

    public Card(Suit suit, int value)
    {
        this.suit = suit;
        this.value = value;
    }

    public Card(int serializedValue)
    {
        value = (serializedValue % 13) + 1;
        int suitInt = serializedValue/13;

        switch(suitInt)
        {
            case 0:
                suit = Suit.CLUBS;
                break;
            case 1:
                suit = Suit.DIAMONDS;
                break;
            case 2:
                suit = Suit.HEARTS;
                break;
            case 3:
                suit = Suit.SPADES;
                break;
            default:
                throw new IllegalArgumentException("serialized card values must be 0-51");
        }
    }

    public Suit getSuit()
    {
        return suit;
    }

    public int getValue()
    {
        return value;
    }

    public String serialize()
    {
        switch(suit)
        {
            case CLUBS:
                return Integer.toString(value + 13 * 0 - 1);
            case DIAMONDS:
                return Integer.toString(value + 13 * 1 - 1);
            case HEARTS:
                return Integer.toString(value + 13 * 2 - 1);
            case SPADES:
                return Integer.toString(value + 13 * 3 - 1);
            default:
                throw new IllegalArgumentException("card has no suit");
        }
    }

    @Override
    public String toString()
    {
        if(value == 1)
        {
            return "ACE of " + suit.name();
        }

        if(value == 11)
        {
            return "JACK of " + suit.name();
        }

        if(value == 12)
        {
            return "QUEEN of " + suit.name();
        }

        if(value == 13)
        {
            return "KING of " + suit.name();
        }

        return Integer.toString(value) + " of " + suit.name();
    }

    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Card))
        {
            return false;
        }

        Card that = (Card)other;
        return this.getValue() == that.getValue() && this.getSuit() == that.getSuit();
    }
}
