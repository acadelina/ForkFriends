package ubb.scs.map.lab6.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.FriendRequest;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.domain.validators.ValidationException;
import ubb.scs.map.lab6.service.ServiceFriendship;
import ubb.scs.map.lab6.service.ServiceRequest;
import ubb.scs.map.lab6.service.ServiceUser;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.util.Collection;

public class AddFriendController implements Observer {
    ServiceFriendship serviceFriendship;
    ServiceUser serviceUser;
    ServiceRequest serviceRequest;
    User user;
    Stage stage;
    private final ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> usersTable;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnEmail;
    @FXML
    Button addFriendButton;

    public void setService(ServiceFriendship service, ServiceUser serviceUser,ServiceRequest serviceRequest,User user,Stage stage) {
        this.serviceFriendship = service;
        this.serviceUser = serviceUser;
        this.serviceRequest = serviceRequest;
        this.user = user;
        this.stage = stage;
        initModel();
    }
    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        usersTable.setItems(model);
    }


    public void initModel()
    {
        Collection<User> all=(Collection<User>) serviceUser.getUsers();
        model.setAll(all);
    }

    public void handleAddFriend(javafx.event.ActionEvent event) {
        User user2=usersTable.getSelectionModel().getSelectedItem();
        if(user2==null)
            MessageAlert.showErrorMessage(null, "No user selected.");
        else{
        try{
            FriendRequest friendRequest=new FriendRequest();
            friendRequest.setId(new Tuple<>(user.getId(),user2.getId()));
            friendRequest.setStatus("PENDING");
            serviceRequest.addRequest(friendRequest);
        }catch(ValidationException e){
            MessageAlert.showErrorMessage(stage, e.getMessage());
        }}
    }
    @Override
    public void update() {
        initModel();
    }
}
