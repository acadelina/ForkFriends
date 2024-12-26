package ubb.scs.map.lab6.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.Message;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.*;
import ubb.scs.map.lab6.utils.Constants;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

public class ChatsController implements Observer {

    private User user;
    private ServiceRequest serviceRequest;
    private ServiceFriendship serviceFriendship;
    private ServiceUser serviceUser;
    private ServiceMessage serviceMessage;
    private Stage stage;

    @FXML
    TableView<User> chatFriendsTable;

    @FXML
    TableColumn<User, String> chatFriendsTableNameColumn;

    @FXML
    TextField messageTextField;

    @FXML
    Button sendButton;

    User chatUser = null;

    ObservableList<Message> messages = FXCollections.observableArrayList();
    private final ObservableList<User> friends = FXCollections.observableArrayList();

    @FXML
    ListView<Message> chatListView;

    void setService(ServiceMessage serviceMessage, ServiceFriendship serviceFriendship, Stage stage, User user) {
        this.serviceMessage = serviceMessage;
        this.serviceFriendship = serviceFriendship;
        this.stage = stage;
        this.user = user;
        initModel();
    }
    public void initModel()
    {
        Collection<User> all=(Collection<User>) serviceFriendship.getFriends(user);
        friends.setAll(all);
        Collection<Message> all2=(Collection<Message>) serviceMessage.getMessages();
        messages.setAll(all2);
    }
    void loadChatListView() {
        messages.clear();
        if(chatUser==null) return;
        messages.addAll(serviceMessage
                .getConversation(user.getId(), chatUser.getId()));
    }

    @FXML
    public void initialize()
    {
        //initChatListView();
        initChatFriendsTable();
    }
    void initChatFriendsTable() {
        chatFriendsTableNameColumn.setCellValueFactory(p-> {
            User u = p.getValue();
            return new SimpleStringProperty(u.getFirstName());
        });
        chatFriendsTable.setItems(friends);
    }

    void initChatListView() {
        chatListView.setCellFactory(cell->new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (!empty && message != null) {
                    String sender = "";

                    if(Objects.equals(message.getIdSender(), user.getId())) {
                        sender = user.getFirstName();
                    }
                    else {
                        sender = chatUser.getFirstName();
                    }

                    setText(sender + " : " +message.getMessage());
                    setTooltip(new Tooltip("Sent : "+message.getDate()
                            .format(Constants.DATE_TIME_FORMATTER)));

                    if (Objects.equals(message.getIdSender(), user.getId())) {
                        setStyle("-fx-font-weight: bold");
                    } else {
                        setStyle(null);
                    }
                } else {
                    setText(null);
                }
            }
        });
        chatListView.setItems(messages);
    }

    public void chatFriendsTableClicked() {
        chatUser = chatFriendsTable.getSelectionModel().getSelectedItem();
        initChatListView();
        loadChatListView();

    }

    public void sendButtonClicked() {
        if (chatUser == null) {
            MessageAlert.showMessage(stage, Alert.AlertType.WARNING, "No user selected", "Please select a friend to chat with.");
            return;
        }
        System.out.println(user.getId());
        try {
            Message message = new Message(messageTextField.getText(),user.getId(), chatUser.getId()
                    , LocalDateTime.now());
            serviceMessage.addMessage(message);
            messages.add(message);
            messageTextField.setText("");
            //update();
        }
        catch (ServiceException e) {
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR,e.getMessage(),e.getMessage());
        }
    }

    @Override
    public void update() {
            initModel();
    }
}
