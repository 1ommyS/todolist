package com.indistudia.repository;

import com.indistudia.config.DbConfig;
import com.indistudia.config.DbManager;
import com.indistudia.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersRepository {
    private final String ADD_USER_QUERY = """
            INSERT INTO USERS (name, age) VALUES (?, ?)
            """;

    private final String GET_USER_BY_ID_QUERY = """
            SELECT * FROM USERS WHERE id = ?
            """;

    private final String GET_ALL_USERS_QUERY = """
            SELECT * FROM USERS
            """;

    public UsersRepository(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    private final DbConfig dbConfig;

    public void addUser(String name, int age) {
        try (var con = DbManager.getConnection(dbConfig)) {
            var preparedStatement = con.prepareStatement(ADD_USER_QUERY);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public User findUser(int id) {
        try (var con = DbManager.getConnection(dbConfig)) {
            var preparedStatement = con.prepareStatement(GET_USER_BY_ID_QUERY);
            preparedStatement.setInt(1, id);
            var result = preparedStatement.executeQuery();

            var isUserExists = result.next();

            if (!isUserExists) throw new RuntimeException("User not found");

            return new User(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getInt("age")
            );
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

        return null;
    }

    public List<User> findAllUsers(int id) {
        List<User> users = new ArrayList<>();

        try (var con = DbManager.getConnection(dbConfig)) {
            var preparedStatement = con.prepareStatement(GET_ALL_USERS_QUERY);
            var result = preparedStatement.executeQuery();

            while (result.next()) {
                users.add(new User(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getInt("age")
                ));
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

        return users;
    }
}
