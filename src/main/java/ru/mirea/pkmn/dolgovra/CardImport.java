package ru.mirea.pkmn.dolgovra;

import ru.mirea.pkmn.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CardImport {
    public Card card = new Card();

    public CardImport(String fileName) {
        if (fileName.endsWith("txt")) {
            SetInfo(new File("src\\main\\resources\\" + fileName));
        }
        else {
            Deserialize("src\\main\\resources\\" + fileName);
        }
    }

    private void SetInfo(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            card.setPokemonStage(PokemonStage.valueOf(reader.readLine()));
            card.setName(reader.readLine());
            card.setHp(Integer.parseInt(reader.readLine()));
            card.setPokemonType(EnergyType.valueOf(reader.readLine()));
            String line;
            if (!(line = reader.readLine()).equals("-")) {
                CardImport Evolution = new CardImport(line);
                card.setEvolvesFrom(Evolution.card);
            }
            card.setSkills(Attacks(reader.readLine()));
            if (!(line = reader.readLine()).equals("-")) {
                card.setWeaknessType(EnergyType.valueOf(line));
            }
            if (!(line = reader.readLine()).equals("-")) {
                card.setResistanceType(EnergyType.valueOf(line));
            }
            card.setRetreatCost(reader.readLine());
            card.setGameSet(reader.readLine());
            card.setRegulationMark(reader.readLine().charAt(0));
            card.setPokemonOwner(Owner(reader.readLine()));
            card.setNumber(reader.readLine());
            reader.close();
            fr.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private List<AttackSkill> Attacks(String line) {
        String[] attacklist = line.split(",");
        List<AttackSkill> Attacks = new ArrayList<AttackSkill>();

        for (int i = 0; i < attacklist.length; i++) {
            String[] keywords = attacklist[i].split(" / ");
            AttackSkill attack = new AttackSkill();
            attack.setCost(keywords[0]);
            attack.setName(keywords[1]);
            attack.setDamage(Integer.parseInt(keywords[2]));
            Attacks.add(attack);
        }

        return Attacks;
    }
    private Student Owner(String line) {
        String[] keywords = line.split(" / ");
        return new Student(keywords[0], keywords[1], keywords[2], keywords[3]);
    }

    private void Deserialize(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.card = (Card) objectInputStream.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}