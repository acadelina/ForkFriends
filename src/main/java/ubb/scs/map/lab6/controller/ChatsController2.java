package ubb.scs.map.lab6.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.Message;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.*;
import ubb.scs.map.lab6.utils.observer.Observer;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public class ChatsController2 implements Observer {

    private User user;
    private ServiceRequest serviceRequest;
    private ServiceFriendship serviceFriendship;
    private ServiceUser serviceUser;
    private ServiceMessage serviceMessage;
    private Stage stage;
    private Message selectedMessageToReply;
    @FXML
    TableView<User> chatFriendsTable;

    @FXML
    TableColumn<User, String> chatFriendsTableNameColumn;

    @FXML
    TextField messageTextField;

    @FXML
    Button sendButton;

    @FXML
    ScrollPane chatScrollPane;

    @FXML
    VBox chatVBox;

    User chatUser = null;
    ObservableList<Message> messages = FXCollections.observableArrayList();
    private final ObservableList<User> friends = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initChatFriendsTable();
    }

    void setService(ServiceMessage serviceMessage, ServiceFriendship serviceFriendship, Stage stage, User user) {
        this.serviceMessage = serviceMessage;
        this.serviceFriendship = serviceFriendship;
        this.stage = stage;
        this.user = user;
        initModel();
    }

    public void initModel() {
        Collection<User> all = serviceFriendship.getFriends(user);
        friends.setAll(all);
        Collection<Message> all2 = (Collection<Message>) serviceMessage.getMessages();
        messages.setAll(all2);
    }

    void loadChatListView() {
        messages.clear();
        if (chatUser == null) return;
        messages.addAll(serviceMessage.getConversation(user.getId(), chatUser.getId()));
    }

    void initChatFriendsTable() {
        chatFriendsTableNameColumn.setCellValueFactory(p -> {
            User u = p.getValue();
            return new SimpleStringProperty(u.getFirstName());
        });
        chatFriendsTable.setItems(friends);
    }

    public void chatFriendsTableClicked() {
        chatUser = chatFriendsTable.getSelectionModel().getSelectedItem();
        if (chatUser == null) return;
        chatVBox.getChildren().clear();
        chatVBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        chatScrollPane.setStyle("-fx-background-color: #fffef4 ;");

        Collection<Message> conversation = serviceMessage.getConversation(user.getId(), chatUser.getId());
        for (Message message : conversation) {
            selectedMessageToReply=serviceMessage.findById(message.getReply());
            Message replyingTo=serviceMessage.findById(message.getReply());
            HBox messageBox = createMessageBox(message);
            chatVBox.getChildren().add(messageBox);
        }

        chatScrollPane.setVvalue(1.0);
    }

    public void sendButtonClicked() {
        if (chatUser == null) {
            MessageAlert.showMessage(stage, Alert.AlertType.WARNING, "No user selected", "Please select a friend to chat with.");
            return;
        }
        try {

            Message message = new Message(messageTextField.getText(), user.getId(), chatUser.getId(), LocalDateTime.now());
            //System.out.println(message.getMessage());
            serviceMessage.addMessage(message);
            List<Message> conv=serviceMessage.getConversation(user.getId(), chatUser.getId());
            Message message1=conv.get(conv.size()-1);

            HBox messageBox = createMessageBox(message);
            chatVBox.getChildren().add(messageBox);
            if(selectedMessageToReply!=null)
                serviceMessage.setReply(message1,selectedMessageToReply.getId());

            messageTextField.setText("");
            selectedMessageToReply = null;
            chatScrollPane.setVvalue(1.0);
        } catch (ServiceException e) {
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, e.getMessage(), e.getMessage());
        }
    }


    private HBox createMessageBox(Message message) {
        HBox messageBox = new HBox();
        HBox styleBox = new HBox();
        messageBox.setSpacing(10);
        Text messageText = new Text(message.getMessage());

        messageBox.setOnMouseClicked(event -> {
            selectedMessageToReply= message;
            messageTextField.setPromptText("Replying to: " + message.getMessage());
        });

        if(selectedMessageToReply!= null) {
            HBox styleBoxR = new HBox();
            Text messageTextR = new Text(selectedMessageToReply.getMessage());
            messageBox.setAlignment(Pos.CENTER_LEFT);
            styleBoxR.setStyle("-fx-background-color: #fffef4; " +
                    "-fx-text-fill: blue; " +
                    "-fx-padding: 10px; " +
                    "-fx-border-color:  #b32e2e;" +
                    "-fx-border-radius: 15px;" +
                    "-fx-background-radius: 15px;");
            styleBoxR.getChildren().add(messageTextR);
            messageBox.getChildren().add(styleBoxR);

        }


        if (message.getIdSender().equals(user.getId())) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            styleBox.setStyle("-fx-background-color: rgba(179,46,81,0.86); " +
                    "-fx-text-fill: blue; " +
                    "-fx-padding: 10px; " +
                    "-fx-background-radius: 15px;");
            messageText.setStyle("-fx-text-fill: #fffef4; ");
        }
        else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            styleBox.setStyle("-fx-background-color: rgba(76,97,50,0.61); " +
                    "-fx-text-fill: black; " +
                    "-fx-padding: 10px; " +
                    "-fx-background-radius: 15px;");
            messageText.setStyle("-fx-text-fill: #fffef4; ");
        }
        styleBox.setPrefWidth(messageBox.getWidth());
        styleBox.getChildren().add(messageText);
        messageBox.getChildren().add(styleBox);
        return messageBox;
    }

    @Override
    public void update() {
        initModel();
    }
}
