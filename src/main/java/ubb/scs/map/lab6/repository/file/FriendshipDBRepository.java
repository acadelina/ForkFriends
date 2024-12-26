package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.Friendship;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.domain.validators.FriendshipValidator;
import ubb.scs.map.lab6.repository.Repository;

import java.sql.*;
import java.util.*;

public class FriendshipDBRepository implements FriendshipRepoPage<Tuple<Long,Long>, Friendship> {
    private FriendshipValidator validator;
    private String url;
    private String username;
    private String password;

    public FriendshipDBRepository(FriendshipValidator validator, String  url, String username, String password) {
        this.validator=validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> tuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE id1 = ? AND id2 = ?;")) {
            statement.setLong(1, tuple.getE1());
            statement.setLong(2, tuple.getE2());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id1 = resultSet.getLong("id1");
                    Long id2 = resultSet.getLong("id2");


                    Friendship friendship = new Friendship();
                    friendship.setId(new Tuple<>(id1, id2));


                    return Optional.of(friendship);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();


    }




    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships ;");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");


                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<>(id1, id2));


                friendships.add(friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendships;
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long,Long> tuple) {
        Optional<Friendship> friendship = findOne(tuple);

        if (friendship.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id1 = ? AND id2 = ?;")) {

                statement.setLong(1, tuple.getE1());
                statement.setLong(2, tuple.getE2());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return friendship;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }

        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (id1, id2) VALUES (?, ?)")) {

            statement.setLong(1, entity.getId().getE1());
            statement.setLong(2, entity.getId().getE2());


            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);

    }




    @Override
    public Optional<Friendship> update(Friendship entity) {
        //TO DO: for now we don't use update
        return Optional.empty();
    }

    @Override
    public Page<Friendship> findAllFriendships(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Friendship> findAllUserFriends(Pageable pageable, Long userId) {
        List<Friendship> friendshipList=new ArrayList<>();
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement pagePreparedStatement=connection.prepareStatement("SELECT * FROM friendships WHERE (id1 = ? or id2 = ?)"+"LIMIT ? OFFSET ?");
            PreparedStatement countPreparedStatement =connection.prepareStatement("SELECT COUNT(*) AS count FROM friendships WHERE (id1 = ? or id2 = ?) "))
        {
            pagePreparedStatement.setInt(3,pageable.getPageSize());
            pagePreparedStatement.setInt(4,pageable.getPageNumber()*pageable.getPageSize());
            pagePreparedStatement.setLong(1,userId);
            pagePreparedStatement.setLong(2,userId);
            countPreparedStatement.setLong(1,userId);
            countPreparedStatement.setLong(2,userId);
            try(ResultSet pageResultSet= pagePreparedStatement.executeQuery();
                ResultSet countResultSet=countPreparedStatement.executeQuery();){
                while(pageResultSet.next())
                {
                    Long id1 = pageResultSet.getLong("id1");
                    Long id2 = pageResultSet.getLong("id2");


                    Friendship friendship = new Friendship();
                    friendship.setId(new Tuple<>(id1, id2));


                    friendshipList.add(friendship);
                }
                int totalCount=0;
                if(countResultSet.next()){
                    totalCount=countResultSet.getInt("count");
                }
                return new Page<>(friendshipList,totalCount);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Friendship> findAllUserFriendRequests(Pageable pageable, Long userId) {
        return null;
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {

        List<Friendship> friendshipList=new ArrayList<>();
        try(Connection connection=DriverManager.getConnection(url,username,password);
        PreparedStatement pagePreparedStatement=connection.prepareStatement("SELECT * FROM friendships "+"LIMIT ? OFFSET ?");
        PreparedStatement countPreparedStatement =connection.prepareStatement("SELECT COUNT(*) AS count FROM friendships "))
        {
            pagePreparedStatement.setInt(1,pageable.getPageSize());
            pagePreparedStatement.setInt(2,pageable.getPageNumber()*pageable.getPageSize());
            try(ResultSet pageResultSet= pagePreparedStatement.executeQuery();
            ResultSet countResultSet=countPreparedStatement.executeQuery();){
                while(pageResultSet.next())
                {
                    Long id1 = pageResultSet.getLong("id1");
                    Long id2 = pageResultSet.getLong("id2");


                    Friendship friendship = new Friendship();
                    friendship.setId(new Tuple<>(id1, id2));


                    friendshipList.add(friendship);
                }
                int totalCount=0;
                if(countResultSet.next()){
                    totalCount=countResultSet.getInt("count");
                }
                return new Page<>(friendshipList,totalCount);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
