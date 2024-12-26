package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.Post;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.repository.Repository;

import java.sql.*;
import java.util.*;

public class PostDBRepository implements Repository <Long, Post> {

    private final String url;
    private final String username;
    private final String password;

    public PostDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<Long> getLikes(Post post) {
        List<Long> likes = new ArrayList<>();
        post.getLikes().forEach(l->
                likes.add(Long.valueOf(l)));
        return likes;
    }

    public void addLike(Post post, User user) {
        List<String> likes = new ArrayList<>();
        post.getLikes().forEach(l->likes.add(l));
        String userId=Long.toString(user.getId());
        if(!likes.contains(userId)) {
            likes.add(userId);
            post.setLikes(likes);
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE posts SET likes = ? WHERE id = ?")) {


                String updatedLikes = String.join(";", likes);
                statement.setString(1, updatedLikes);
                statement.setLong(2, post.getId());


                statement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Error while adding like to post: " + e.getMessage(), e);
            }
        }
    }
    @Override
    public Optional<Post> findOne(Long aLong) {
        try(Connection connection= DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM posts WHERE id = ?"))
        {
            preparedStatement.setLong(1,aLong);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                if(resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    byte[] image = resultSet.getBytes("image");
                    String description = resultSet.getString("description");
                    Timestamp timestamp = resultSet.getTimestamp("date");
                    //Long reply = resultSet.getLong("reply");
                    String author = resultSet.getString("author");
                    Long authorId = resultSet.getLong("author_id");
                    List<String> likes = Arrays.stream(resultSet.getString("likes").split(";")).toList();
                    Post post = new Post(image, description, author,authorId, likes, timestamp.toLocalDateTime());
                    post.setId(id);

                    return Optional.of(post);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Post> findAll() {
        Set<Post> posts = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM posts ;");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id =resultSet.getLong("id");
                byte[] image = resultSet.getBytes("image");
                String description=resultSet.getString("description");
                Timestamp timestamp= resultSet.getTimestamp("date");
                Long authorId=resultSet.getLong("author_id");
                String author=resultSet.getString("author");
                List<String> likes= Arrays.stream(resultSet.getString("likes").split(";")).toList();
                Post post=new Post(image,description,author,authorId,likes,timestamp.toLocalDateTime());
                post.setId(id);

                posts.add(post);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return posts;
    }

    @Override
    public Optional<Post> save(Post entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }



        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO posts ( image, description, author,author_id, date, likes) VALUES ( ?,?,?,?,?,?)")) {

            statement.setBytes(1, entity.getImage());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getAuthor());
            statement.setLong(4, entity.getAuthorId());
            statement.setTimestamp(5, Timestamp.valueOf( entity.getDate()));
            statement.setString(6, entity.getLikes().toString());


            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Post> delete(Long aLong) {
        Optional<Post> post = findOne(aLong);

        if (post.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM posts WHERE id = ? ;")) {

                statement.setLong(1, aLong);


                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return post;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Post> update(Post entity) {
        return Optional.empty();
    }
}
