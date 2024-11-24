package ru.mirea.pkmn.dolgovra;

import ru.mirea.pkmn.Card;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CardExport {
    public CardExport(Card card) {
        Serialize(card);
    }

    private void Serialize(Card card) {
        try {
            File file = new File("src\\main\\resources\\" + card.getName() + ".crd");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(card);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
