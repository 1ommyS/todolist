package com.indistudia.repository;

import com.indistudia.config.DbConfig;
import com.indistudia.config.DbManager;
import com.indistudia.config.TxManager;
import com.indistudia.entity.Todo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TodoRepository {
    private final String ADD_TODO_QUERY = """
            INSERT INTO todos (id, title, user_id) VALUES (?::uuid, ?, ?)
            """;

    private final String GET_TODO_BY_ID_QUERY = """
            SELECT * FROM todos WHERE id = ?::uuid
            """;

    private final String GET_ALL_USERS_TODOS_QUERY = """
            SELECT * FROM todos WHERE user_id = ?
            """;

    private final String COMPLETE_TODO_QUERY = """
            UPDATE todos SET is_completed = TRUE WHERE id = ?::uuid AND user_id = ?
            """;
    private final TxManager txManager;

    public TodoRepository(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
        txManager = new TxManager(dbConfig);
    }

    private final DbConfig dbConfig;

    public void addTodo(String title, Long userId) {
        txManager.inTx(connection -> {
            addTodo(connection, title, userId);
            return null;
        });
    }

    private void addTodo(Connection con, String title, Long userId) {
        try {
            var preparedStatement = con.prepareStatement(ADD_TODO_QUERY);
            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, title);
            preparedStatement.setLong(3, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Todo findTodoById(UUID id) {
        try (var con = DbManager.openConnection(dbConfig)) {
            var preparedStatement = con.prepareStatement(GET_TODO_BY_ID_QUERY);
            preparedStatement.setString(1, id.toString());
            var result = preparedStatement.executeQuery();

            var isTodoExists = result.next();

            if (!isTodoExists) throw new RuntimeException("Todo not found");

            return new Todo(
                    UUID.fromString(result.getString("id")),
                    result.getString("title"),
                    result.getBoolean("is_completed"),
                    result.getLong("user_id")
            );
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

        return null;
    }

    public List<Todo> findAllUsersTodos(Long userId) {
        List<Todo> todos = new ArrayList<>();

        try (var con = DbManager.openConnection(dbConfig)) {
            var preparedStatement = con.prepareStatement(GET_ALL_USERS_TODOS_QUERY);
            preparedStatement.setLong(1, userId);
            var result = preparedStatement.executeQuery();

            while (result.next()) {
                todos.add(
                        Todo.builder()
                                .id(UUID.fromString(result.getString("id")))
                                .title(result.getString("title"))
                                .isCompleted(result.getBoolean("is_completed"))
                                .userId(result.getLong("user_id"))
                                .build()
                );
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        return todos;
    }

    public void completeTodo(UUID id, Long userId) {
        try (var con = DbManager.openConnection(dbConfig)) {
            var preparedStatement = con.prepareStatement(COMPLETE_TODO_QUERY);
            preparedStatement.setString(1, id.toString());
            preparedStatement.setLong(2, userId);
            preparedStatement.execute();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

}
