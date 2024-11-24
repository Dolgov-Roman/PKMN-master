package ru.mirea.pkmn.dolgovra.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.mirea.pkmn.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseServiceImpl implements DatabaseService {

    private final Connection connection;
    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {

        // Загружаем файл database.properties
        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src\\main\\resources\\database.properties"));

        // Подключаемся к базе данных
        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));
    }

    @Override
    public Card getCardFromDatabase(String cardName) throws SQLException, JsonProcessingException {
        Card card = new Card();

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM card WHERE \"name\" = '" + cardName + "'");
        String id_student = null;
        String id_evolution = null;
        while (result.next()) {
            card.setName(result.getString("name"));
            card.setHp(result.getInt("hp"));
            card.setGameSet(result.getString("game_set"));
            card.setPokemonStage(PokemonStage.valueOf(result.getString("stage")));
            card.setRetreatCost(result.getString("retreat_cost"));
            card.setWeaknessType(EnergyType.valueOf(result.getString("weakness_type")));
            try {
                if ((!result.getString("resistance_type").equals("null")) && (result.getString("resistance_type") != null)) {
                    card.setResistanceType(EnergyType.valueOf(result.getString("resistance_type")));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            card.setPokemonType(EnergyType.valueOf(result.getString("pokemon_type")));
            card.setRegulationMark(result.getString("regulation_mark").charAt(0));
            card.setNumber(result.getString("card_number"));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode attack_json = mapper.readTree(result.getString("attack_skills"));
            List<AttackSkill> attacksList = new ArrayList<>();
            for (JsonNode node : attack_json) {
                AttackSkill attack = new AttackSkill(
                        node.path("name").asText(),
                        node.path("description").asText(),
                        node.path("cost").asText(),
                        node.path("damage").asInt()
                );
                attacksList.add(attack);
            }
            card.setSkills(attacksList);

            if (result.getString("pokemon_owner") != null) {
                id_student = result.getString("pokemon_owner");
            }
            if (result.getString("evolves_from") != null) {
                id_evolution = result.getString("evolves_from");
            }
        }
        result.close();

        if (id_student != null) {
            ResultSet studentInfo = statement.executeQuery("SELECT * FROM student WHERE id = '" + id_student + "'");
            while (studentInfo.next()) {
                Student student = new Student(
                        studentInfo.getString("familyName"),
                        studentInfo.getString("firstName"),
                        studentInfo.getString("patronicName"),
                        studentInfo.getString("group")
                );
                card.setPokemonOwner(student);
            }
            studentInfo.close();
        }
        if (id_evolution != null) {
            ResultSet evolution = statement.executeQuery("SELECT \"name\" FROM card WHERE id = '" + id_evolution + "'");
            evolution.next();
            card.setEvolvesFrom(getCardFromDatabase(evolution.getString("name")));
        }
        statement.close();

        return card;
    }

    @Override
    public Student getStudentFromDatabase(String studentFullName) throws SQLException {
        Student student = new Student();

        String[] studentInfo = studentFullName.split(" ");
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM student WHERE \"familyName\" = '" + studentInfo[0] + "' AND \"firstName\" = '" + studentInfo[1] + "' AND \"patronicName\" = '" + studentInfo[2] + "'");
        while (result.next()) {
            student.setSurName(result.getString("familyName"));
            student.setFirstName(result.getString("firstName"));
            student.setFamilyName(result.getString("patronicName"));
            student.setGroup(result.getString("group"));
        }
        result.close();
        statement.close();
        return student;
    }

    @Override
    public void saveCardToDatabase(Card card) throws SQLException {
        Gson gson = new GsonBuilder().create();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO card VALUES(gen_random_uuid(), '"
                + card.getName() + "', '"
                + card.getHp() + "', "
                + ( (card.getEvolvesFrom() != null) ? "(SELECT id FROM card WHERE name = '"+ card.getEvolvesFrom().getName() + "' limit 1), '" : "null, '" )
                + card.getGameSet() + "', "
                + "(SELECT id FROM student WHERE \"familyName\" = '" + card.getPokemonOwner().getSurName() + "' limit 1), '"
                + card.getPokemonStage() + "', '"
                + card.getRetreatCost() + "', '"
                + card.getWeaknessType() + "', '"
                + card.getResistanceType() + "', '"
                + gson.toJson(card.getSkills()) + "', '"
                + card.getPokemonType() + "', '"
                + card.getRegulationMark() + "', '"
                + card.getNumber() + "')");
        statement.close();
        System.out.println("New card saved");
    }

    @Override
    public void createPokemonOwner(Student owner) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO student VALUES(gen_random_uuid(), '"
                + owner.getSurName() + "', '"
                + owner.getFirstName() + "', '"
                + owner.getFamilyName() + "', '"
                + owner.getGroup() + "')");
        statement.close();
        System.out.println("New pokemon owner added");
    }
}