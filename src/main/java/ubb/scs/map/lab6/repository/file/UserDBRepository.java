package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.domain.validators.UserValidator;
import ubb.scs.map.lab6.repository.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Optional;

public class UserDBRepository implements Repository<Long, User> {
    UserValidator validator;
    private String url;
    private String username;
    private String password;
    public UserDBRepository(UserValidator validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                byte[] photo = resultSet.getBytes("photo");

                User u = new User(firstName, lastName,email,username,password,photo);
                u.setId(id);
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        HashMap<Long, User> users =new HashMap<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from users");
                ResultSet resultSet=preparedStatement.executeQuery()){
            while(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                byte[] photo = resultSet.getBytes("photo");

                User user = new User(firstName, lastName,email,username,password,photo);
                user.setId(id);
                users.put(user.getId(),user);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users.values();
    }

    @Override
    public Optional<User> save(User entity) {

        if(entity==null){
            throw new IllegalArgumentException("User can't be null!");
        }
        validator.validate(entity);
        String query = "INSERT INTO users (\"first_name\", \"last_name\", \"email\", \"username\", \"password\",\"photo\") VALUES (?,?,?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement(query);){
            //preparedStatement.setLong(1,entity.getId());
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getUsername());
            preparedStatement.setString(5, entity.getPassword());
            preparedStatement.setBytes(6, entity.getPhoto());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Long aLong) {
        Optional<User> userToDelete = findOne(aLong); // First, find the user by ID
        if (userToDelete.isPresent()) {
            String query = "DELETE FROM users WHERE \"id\" = ?";
            try {Connection connection = DriverManager.getConnection(url, username, password);
                 Statement statement = connection.createStatement();
                 statement.executeUpdate("DELETE FROM users WHERE id =" + aLong);

                return userToDelete; // Return the deleted user if the deletion is successful
            } catch (SQLException e) {
                throw new RuntimeException("Error deleting user with ID: " + aLong, e);
            }
        }
        return Optional.empty(); // Return empty if the user was not found
    }

    @Override
    public Optional<User> update(User entity) {
        if(entity==null){
            throw new IllegalArgumentException("User can't be null!");
        }
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE users SET first_name = ?, last_name = ?, email=?, photo=? WHERE id = ?"
            )){
            preparedStatement.setLong(5,entity.getId());
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setBytes(4, entity.getPhoto());

            int rowsAffected =preparedStatement.executeUpdate();
            if(rowsAffected>0)
                return Optional.of(entity);

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

}
