package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.Message;
import ubb.scs.map.lab6.domain.validators.MessageValidator;
import ubb.scs.map.lab6.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MessageDBRepository implements Repository<Long, Message> {
    private final MessageValidator validator;
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepository(MessageValidator messageValidator, String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        validator = messageValidator;
    }

    @Override
    public Optional<Message> findOne(Long aLong) {
        try(Connection connection= DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM messages WHERE id = ?"))
        {
            preparedStatement.setLong(1,aLong);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                Long id =resultSet.getLong("id");
                Long idSender= resultSet.getLong("sender");
                Long idReceiver= resultSet.getLong("receiver");
                String message=resultSet.getString("message");
                Timestamp timestamp= resultSet.getTimestamp("date");
                Long reply=resultSet.getLong("reply");
//                Optional<Message> replyMessage=findOne(reply);
//                Message replyMes;
//                if(replyMessage.isPresent())
//                    replyMes=replyMessage.get();
//                else
//                    replyMes=null;

                Message message2=new Message(message,idSender,idReceiver,timestamp.toLocalDateTime(),reply);
                message2.setId(id);

                return Optional.of(message2);

            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }


    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages=new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
        Statement statement=connection.createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT * FROM messages")){
                while(resultSet.next())
                {
                    Long id= resultSet.getLong("id");
                    Long idSender=resultSet.getLong("idSender");
                    Long idReceiver= resultSet.getLong("idReceiver");
                    String text=resultSet.getString("message");
                    Timestamp date=resultSet.getTimestamp("date");
                    Long reply=resultSet.getLong("reply");
                    Message message=new Message(text,idSender,idReceiver,date.toLocalDateTime(),reply);
                    message.setId(id);
                    messages.add(message);

                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        if(entity==null)
            throw new IllegalArgumentException("Entity cannot be null");
        validator.validate(entity);
        try(Connection connection=DriverManager.getConnection(url,username,password);
        PreparedStatement preparedStatement= connection.prepareStatement("INSERT INTO messages (idSender,idReceiver,message,date) VALUES (?,?,?,?)")){

            preparedStatement.setLong(1,entity.getIdSender());
            preparedStatement.setLong(2,entity.getIdReceiver());
            preparedStatement.setString(3,entity.getMessage());
            preparedStatement.setDate(4,Date.valueOf(entity.getDate().toLocalDate()));
            //preparedStatement.setLong(5,entity.getReply().getId());

            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        if(entity==null){
            throw new IllegalArgumentException("Message can't be null!");
        }
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE messages SET reply = ? WHERE id = ?"
            )){
            preparedStatement.setLong(2,entity.getId());
            preparedStatement.setLong(1, entity.getReply());


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
