package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.FriendRequest;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.validators.FriendRequestValidator;
import ubb.scs.map.lab6.domain.validators.ValidationException;
import ubb.scs.map.lab6.repository.Repository;

import java.sql.*;
import java.util.*;

public class RequestDBRepository implements Repository<Tuple<Long,Long>, FriendRequest> {

    private final FriendRequestValidator validator;
    private final String url;
    private final String username;
    private final String password;

    public RequestDBRepository(FriendRequestValidator validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> longLongTuple) {
       try(Connection connection= DriverManager.getConnection(url,username,password);
           PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE id1 = ? AND id2 = ?;")){
           statement.setLong(1,longLongTuple.getE1());
           statement.setLong(2,longLongTuple.getE2());
           try(ResultSet resultSet = statement.executeQuery()){
               if(resultSet.next()){
                   long id1 = resultSet.getLong(1);
                   long id2 = resultSet.getLong(2);
                   String status = resultSet.getString(3);
                   Timestamp timestamp = resultSet.getTimestamp(4);

                   FriendRequest request = new FriendRequest();
                   request.setId(new Tuple<>(id1,id2));
                   request.setStatus(status);
                   request.setDate(timestamp.toLocalDateTime());

                   return Optional.of(request);
               }}catch (SQLException e)
               {
                   throw new RuntimeException(e);
               }

       }catch (SQLException e)
       {
           throw new RuntimeException(e);
       }
       return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll(){
        HashMap<Tuple<Long,Long>,FriendRequest> requests = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement= connection.prepareStatement("SELECT * FROM requests ;");
        ResultSet resultSet =statement.executeQuery()){
            while(resultSet.next())
            {
                Long id1= resultSet.getLong("id1");
                Long id2= resultSet.getLong("id2");
                String status= resultSet.getString("status");
                Timestamp timestamp  = resultSet.getTimestamp("date");

                FriendRequest request = new FriendRequest();
                request.setId(new Tuple<>(id1,id2));
                request.setStatus(status);
                request.setDate(timestamp.toLocalDateTime());
                requests.put(request.getId(),request);
            }
        }catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return requests.values();
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        if(entity==null)
            throw new IllegalArgumentException("FriendRequest cannot be null");

        if(findOne(entity.getId()).isPresent())
        {
            String status=entity.getStatus();
            if(Objects.equals(status,"PENDING"))
                throw new ValidationException("FriendRequest already exists");
        }
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("INSERT INTO requests (id1,id2,status,date) values (?,?,?,?)")){
            statement.setLong(1,entity.getId().getE1());
            statement.setLong(2,entity.getId().getE2());
            statement.setString(3, entity.getStatus());
            statement.setTimestamp(4,Timestamp.valueOf(entity.getDate()));

            statement.executeUpdate();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> longLongTuple) {
        Optional<FriendRequest> optional = findOne(longLongTuple);
        if(optional.isPresent()){
            try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM requests WHERE id1 = ? AND id2 = ?;")){
                statement.setLong(1,longLongTuple.getE1());
                statement.setLong(2,longLongTuple.getE2());

                statement.executeUpdate();
            }catch(SQLException e)
            {
                throw new RuntimeException(e);
            }
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
         if(entity==null)
             throw new IllegalArgumentException("FriendRequest cannot be null");

         if(findOne(entity.getId()).isEmpty())
             throw new ValidationException("FriendRequest does not exist");

         validator.validate(entity);

         try(Connection connection = DriverManager.getConnection(url,username,password);
         PreparedStatement statement = connection.prepareStatement("UPDATE requests SET status = ?, date = ? WHERE id1=? AND id2=?"))
         {
             statement.setString(1, entity.getStatus());
             statement.setTimestamp(2,Timestamp.valueOf(entity.getDate()));
             statement.setLong(3, entity.getId().getE1());
             statement.setLong(4,entity.getId().getE2());

             int rowsAffected =statement.executeUpdate();
             if(rowsAffected>0){
                 return Optional.of(entity);
             }
         }
         catch (SQLException e)
         {
             throw new RuntimeException(e);
         }
         return Optional.empty();
    }
}
