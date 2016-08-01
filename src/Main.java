import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main
{
    private static final String HELP_LINE = "OPTIONS:\n1: Show hand\n2: Play card\n3: Draw cards\n4: Shuffle\n5: Play card to character pool\n6: Get deck size\n7: Show discard pile\n8: Show character cards\n9: Set character cards\nQ: Quit";
    public static void main(String[] argv) throws IOException
    {
        if (argv.length != 1)
        {
            System.err.println("Usage: Main [path to deck save file]");
            return;
        }
        PersistentDeck deck = new PersistentDeck(argv[0]);
        try
        {
            String input = "";
            Scanner inputScanner = new Scanner(System.in);
            while (!input.equalsIgnoreCase("quit") && !input.equalsIgnoreCase("q"))
            {
                System.out.println("Input an option (0-9) or quit:");
                input = inputScanner.nextLine();
                switch (input)
                {
                    case "0":
                        System.out.println(HELP_LINE);
                        break;

                    case "1":
                        System.out.println("Hand:");
                        printCards(deck.getHand());
                        break;

                    case "2":
                        System.out.println("Choose a card to play");
                        System.out.println("Hand:");
                        List<Card> hand = deck.getHand();
                        printCards(hand);

                        try
                        {
                            int cardToPlay = Integer.parseInt(inputScanner.nextLine());
                            System.out.println("Playing " + hand.get(cardToPlay));
                            deck.play(cardToPlay);
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("could not parse int, returning to menu");
                            break;
                        }
                        break;

                    case "3":
                        System.out.println("Draw cards:");
                        try
                        {
                            int cardsToDraw = Integer.parseInt(inputScanner.nextLine());
                            System.out.println("Hand:");
                            printCards(deck.draw(cardsToDraw));
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("could not parse int, returning to menu");
                            break;
                        }
                        break;

                    case "4":
                        System.out.println("Shuffling together deck and discard");
                        deck.shuffle();
                        break;

                    case "5":
                        System.out.println("Choose a card to play to the character pool");
                        System.out.println("Hand:");
                        hand = deck.getHand();
                        printCards(hand);

                        try
                        {
                            int cardToPlay = Integer.parseInt(inputScanner.nextLine());
                            System.out.println("Playing " + hand.get(cardToPlay) + " to character pool");
                            deck.playToCharacter(cardToPlay);
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("could not parse int, returning to menu");
                            break;
                        }
                        break;

                    case "6":
                        System.out.printf("Deck size: %d\n", deck.getDeckSize());
                        break;

                    case "7":
                        System.out.println("Discard:");
                        printCards(deck.getDiscard());
                        break;

                    case "8":
                        System.out.println("Character:");
                        printCards(deck.getCharacter());
                        break;

                    case "9":
                        System.out.println("Current character:");
                        printCards(deck.getCharacter());
                        System.out.println("This operation will replace your current character. Continue? Y/N");
                        String answer = inputScanner.nextLine();
                        if(!(answer.startsWith("Y") || answer.startsWith("y")))
                        {
                            break;
                        }

                        List<Card> characterCards = new LinkedList<>();
                        System.out.println("How many cards?");
                        try
                        {
                            int cardsToAdd = Integer.parseInt(inputScanner.nextLine());
                            for(int i = 0; i < cardsToAdd; i++)
                            {
                                System.out.println("Rank:");
                                String rank = inputScanner.nextLine();
                                int value;
                                if(rank.equalsIgnoreCase("ACE"))
                                {
                                    value = 1;
                                }
                                else if(rank.equalsIgnoreCase("JACK"))
                                {
                                    value = 11;
                                }
                                else if(rank.equalsIgnoreCase("QUEEN"))
                                {
                                    value = 12;
                                }
                                else if(rank.equalsIgnoreCase("KING"))
                                {
                                    value = 13;
                                }
                                else
                                {
                                    value = Integer.parseInt(rank);
                                }
                                System.out.println("Suit:");
                                Suit suit = Suit.valueOf(inputScanner.nextLine());

                                Card card = new Card(suit, value);
                                System.out.println("Adding " + card.toString());
                                characterCards.add(card);
                            }

                            deck.setCharacter(characterCards);
                            System.out.println("New character:");
                            printCards(deck.getCharacter());
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("could not parse int, returning to menu");
                            break;
                        }
                        break;

                    default:
                        if (!input.equalsIgnoreCase("quit") && !input.equalsIgnoreCase("q"))
                        {
                            System.out.println("Unrecognized input: \"" + input + "\"");
                            System.out.println(HELP_LINE);
                        }
                        break;
                }
            }

            deck.saveToFile();
        }
        catch(Throwable t)
        {
            // try to save before dying
            deck.saveToFile();
            throw t;
        }
    }

    public static void printCards(List<Card> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            System.out.printf("%d: %s\n", i, list.get(i).toString());
        }
    }
}
