package ubb.scs.map.lab6.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.Friendship;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.repository.file.Page;
import ubb.scs.map.lab6.repository.file.Pageable;
import ubb.scs.map.lab6.service.ServiceException;
import ubb.scs.map.lab6.service.ServiceFriendship;
import ubb.scs.map.lab6.service.ServiceRequest;
import ubb.scs.map.lab6.service.ServiceUser;
import ubb.scs.map.lab6.utils.events.Event;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;

public class FriendsController implements Observer {


    private int currentPageFriends=0;
    private int pageSize=2;
    private User user;
    private ServiceRequest serviceRequest;
    private ServiceFriendship serviceFriendship;
    private ServiceUser serviceUser;
    private final ObservableList<User> requests= FXCollections.observableArrayList();
    private final ObservableList<User> friendsUsers= FXCollections.observableArrayList();
    private Stage stage;
    @FXML
    TableView<User> friendsTable;

    @FXML
    private TableColumn<User, String> username;

    @FXML
    TableView<User> requestsTable;
    @FXML
    private TableColumn<User, String> request;

    @FXML
    Button acceptButton;
    @FXML
    Button declineButton;
    @FXML
    Button removeFriendButton;
    @FXML
    Button addFriendButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    @FXML
    public void initialize() {
        username.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        friendsTable.setItems(friendsUsers);
        friendsTable.getItems().clear();
        request.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        requestsTable.setItems(requests);
        //initModel();


    }



    public void setService(ServiceRequest serviceRequest, ServiceFriendship serviceFriendship, ServiceUser serviceUser,User user, Stage stage) {
        this.serviceRequest = serviceRequest;
        this.serviceFriendship = serviceFriendship;
        this.serviceUser = serviceUser;
        this.user = user;
        this.stage = stage;
        initModel();
    }
    @Override
    public void update() {
        initModel();
    }

    public void initModel()
    {
        friendsUsers.clear();
        Collection<User> req=(Collection<User>) serviceRequest.pendingRequests(user.getId());
        requests.setAll(req);
        Page<Friendship> pageFriendships = serviceFriendship.findAllUserFriends(new Pageable(currentPageFriends, pageSize), this.user);

        int maxPageFriendships = (int) Math.ceil((double) pageFriendships.getTotalElementsCount() / pageSize) - 1;

        if (maxPageFriendships <0) maxPageFriendships = 0;
        if (currentPageFriends > maxPageFriendships) {
            currentPageFriends = maxPageFriendships;
            pageFriendships = serviceFriendship.findAllUserFriends(new Pageable(currentPageFriends, pageSize),user);
        }

        int totalNumberOfElementsFriendships = pageFriendships.getTotalElementsCount();
        System.out.println(totalNumberOfElementsFriendships);
        prevButton.setDisable(currentPageFriends == 0);
        nextButton.setDisable((currentPageFriends+1) * pageSize >= totalNumberOfElementsFriendships);



        for (Friendship friendship : pageFriendships.getElementsOnPage()) {
            Long id;
            if(friendship.getId().getE2().equals(user.getId()))
                id=friendship.getId().getE1();
            else
                id=friendship.getId().getE2();
            User user1=serviceUser.findUser(id);
            friendsUsers.add(user1);
        }


    }

    public void nextButtonClicked(javafx.event.ActionEvent actionEvent) {
        currentPageFriends++;
        initModel();
    }
    public void prevButtonClicked(javafx.event.ActionEvent actionEvent) {
        currentPageFriends--;
        initModel();
    }

    public void handleRemoveFriendButtonAction(javafx.event.ActionEvent event) {
        User user2=friendsTable.getSelectionModel().getSelectedItem();
        try{
            Long id1=user2.getId();
            Long id2=user.getId();
            serviceFriendship.removeFriendship(new Tuple<>(id1,id2));
            serviceFriendship.removeFriendship(new Tuple<>(id2,id1));
            MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Remove friend","Friend removed succesfully");
            update();
        }
        catch (ServiceException e)
        {
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR,e.getMessage(),e.getMessage());
        }
    }

    public void handleAddButtonAction(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/add-friend-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,670,520);
            dialogStage.setScene(scene);

            AddFriendController addFriendController = loader.getController();
            addFriendController.setService(serviceFriendship,serviceUser,serviceRequest,user,dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeclineButtonAction(javafx.event.ActionEvent event) {
        User user2=requestsTable.getSelectionModel().getSelectedItem();
        Long id1=user2.getId();
        Long id2=user.getId();
        serviceRequest.respondRequest(new Tuple<>(id1,id2),"DECLINED");
        MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Decline request","Friend request declined succesfully");
        update();
    }

    public void handleAcceptFriendButtonAction(javafx.event.ActionEvent event) {
        User user2=requestsTable.getSelectionModel().getSelectedItem();
        Long id1=user2.getId();
        Long id2=user.getId();
        serviceRequest.respondRequest(new Tuple<>(id1,id2),"APPROVED");
        MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Approve request","Friend request approved succesfully");
        update();

    }


}
