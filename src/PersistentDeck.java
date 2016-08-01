import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PersistentDeck
{
    private LinkedList<Card> deck;
    private LinkedList<Card> hand;
    private LinkedList<Card> discard;
    private LinkedList<Card> character;

    private String filepath;

    public PersistentDeck(String filepath) throws IOException
    {
        this.filepath = filepath;
        File saveFile = new File(filepath);
        deck = new LinkedList<>();
        hand = new LinkedList<>();
        discard = new LinkedList<>();
        character = new LinkedList<>();

        if(saveFile.exists())
        {
            // deserialize from save
            String serializedSave = "";
            FileInputStream saveFileStream = new FileInputStream(saveFile);

            while(saveFileStream.available() > 0)
            {
                serializedSave += (char)saveFileStream.read();
            }

            // manually split because String.split() doesn't include empty chunks
            String[] saveChunks = {"","","",""};
            int nextChunkIndex = 0;
            int chunkStart = 0;
            for(int i = 0; i < serializedSave.length(); i++)
            {
                if(nextChunkIndex == 3)
                {
                    saveChunks[nextChunkIndex] = serializedSave.substring(chunkStart);
                    break;
                }
                if(serializedSave.charAt(i) == ';')
                {
                    saveChunks[nextChunkIndex] = serializedSave.substring(chunkStart, i);
                    chunkStart = i + 1;
                    nextChunkIndex++;
                }
            }

            deserializeCardsToList(saveChunks[0].split(","), deck);
            deserializeCardsToList(saveChunks[1].split(","), hand);
            deserializeCardsToList(saveChunks[2].split(","), discard);
            deserializeCardsToList(saveChunks[3].split(","), character);

            int totalCards = deck.size() + hand.size() + discard.size() + character.size();
            if(totalCards != 52)
            {
                throw new IOException(String.format("bad save file, found %d cards", totalCards));
            }
        }
        else
        {
            for(int i = 0; i < 52; i++)
            {
                deck.add(new Card(i));
            }
            shuffle();
        }
    }

    public List<Card> draw(int count)
    {
        for(int i = 0; i < count; i++)
        {
            if(deck.size() == 0)
                break;
            hand.add(deck.remove(0));
        }

        return hand;
    }

    public List<Card> play(int index)
    {
        discard.add(hand.remove(index));
        return hand;
    }

    public void shuffle()
    {
        LinkedList<Card> cardsToAdd = new LinkedList<>();
        cardsToAdd.addAll(deck);
        cardsToAdd.addAll(discard);
        discard.clear();
        deck.clear();

        int cardsLeft = cardsToAdd.size();
        Random rand = new Random();
        while(cardsLeft > 0)
        {
            Card next = cardsToAdd.remove(rand.nextInt(cardsLeft));
            deck.add(next);
            cardsLeft = cardsToAdd.size();
        }
    }

    public void saveToFile() throws IOException
    {
        // back up the save file in case something goes wrong
        File saveFile = new File(filepath);
        File oldSaveFile = new File(filepath + ".bak");
        if(oldSaveFile.exists())
            oldSaveFile.delete();
        saveFile.renameTo(oldSaveFile);

        FileWriter writer = new FileWriter(filepath, false);

        writer.write(serialize());
        writer.flush();
    }

    public void setCharacter(List<Card> characterCards)
    {
        while(character.size() > 0)
        {
            deck.add(character.remove());
        }

        for(Card c: characterCards)
        {
            if(hand.remove(c))
            {
                character.add(c);
            }
            else if(deck.remove(c))
            {
                character.add(c);
            }
            else if(discard.remove(c))
            {
                character.add(c);
            }
        }
    }

    public void playToCharacter(int index)
    {
        character.add(hand.remove(index));
    }

    public int getDeckSize()
    {
        return deck.size();
    }

    public List<Card> getHand()
    {
        return hand;
    }

    public List<Card> getDiscard()
    {
        return discard;
    }

    public List<Card> getCharacter()
    {
        return character;
    }

    private String serialize()
    {
        String result = "";

        result += serializeCardList(deck);
        result += ";";
        result += serializeCardList(hand);
        result += ";";
        result += serializeCardList(discard);
        result += ";";
        result += serializeCardList(character);

        return result;
    }

    private String serializeCardList(List<Card> list)
    {
        String result = "";
        if(list.size() > 0)
        {
            result += list.get(0).serialize();
            for (int i = 1; i < list.size(); i++)
            {
                result += ",";
                result += list.get(i).serialize();
            }
        }
        return result;
    }

    private void deserializeCardsToList(String[] strArr, List<Card> list)
    {
        for(String s: strArr)
        {
            if(s.isEmpty())
                continue;
            Card card = new Card(Integer.parseInt(s));
            if(!list.contains(card))
            {
                list.add(card);
            }
            else
            {
                throw new IllegalArgumentException("bad card list, contains duplicate of " + card.toString());
            }
        }
    }
}
