package ubb.scs.map.lab6.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.ServiceFriendship;
import ubb.scs.map.lab6.service.ServiceMessage;
import ubb.scs.map.lab6.service.ServiceRequest;
import ubb.scs.map.lab6.service.ServiceUser;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.io.IOException;
import java.util.Collection;

public class UserController implements Observer {


    ServiceUser serviceUser;
    ServiceRequest serviceRequest;
    ServiceFriendship serviceFriendship;
    ServiceMessage serviceMessage;
    Stage stage;
    User user;
    private final ObservableList<User> model = FXCollections.observableArrayList();
    private final ObservableList<User> model2 = FXCollections.observableArrayList();


    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,String> tableColumnId;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnEmail;

    @FXML
    Button profile;

    @FXML
    Button friends;

    @FXML
    TextField RequestsNumber;

    public void setUserService(ServiceUser service, ServiceFriendship serviceFriendship, ServiceRequest serviceRequest, ServiceMessage serviceMessage, Stage stage, User user) {
        this.serviceUser = service;
        this.serviceFriendship = serviceFriendship;
        this.serviceRequest = serviceRequest;
        this.serviceMessage= serviceMessage;
        this.stage = stage;
        this.user = user;
        service.addObserver(this);
        initModel();
    }

    public void initialize()
    {
        //tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableView.setItems(model);
    }

    public void handleProfile(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/profile-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,670,520);
            dialogStage.setScene(scene);

            ProfileController profileController = loader.getController();
            //profileController.setService(serviceUser,serviceFriendship,serviceRequest,serviceMessage,dialogStage,stage,user);

            dialogStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleFriends(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friends-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friends");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,670,520);
            dialogStage.setScene(scene);

            FriendsController loginController = loader.getController();
            loginController.setService(serviceRequest,serviceFriendship,serviceUser,user,dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleChats(javafx.event.ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/chat-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chats");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,538,400);
            dialogStage.setScene(scene);

            ChatsController2 chatsController = loader.getController();
            chatsController.setService(serviceMessage,serviceFriendship,dialogStage,user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void initModel()
    {
        Collection<User> all=(Collection<User>) serviceUser.getUsers();
        model.setAll(all);
        int nr=serviceRequest.pendingRequests(user.getId()).size();
        RequestsNumber.setText(String.valueOf(nr));
    }
    @Override
    public void update() {
        initModel();
    }
}
