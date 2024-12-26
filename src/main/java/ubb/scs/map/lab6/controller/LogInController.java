package ubb.scs.map.lab6.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.*;


import java.io.IOException;

public class LogInController {
    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button login;

    @FXML
    private Button createAccount;

    private ServicePost servicePost;
    private ServiceRequest serviceRequest;
    private ServiceFriendship serviceFriendship;
    private ServiceMessage serviceMessage;
    ServiceUser service;
    Stage dialogStage;

    public void setService(ServiceUser service, ServiceFriendship serviceFriendship, ServiceRequest serviceRequest,ServiceMessage serviceMessage,ServicePost servicePost, Stage stage) {
        this.service = service;
        this.serviceFriendship = serviceFriendship;
        this.serviceRequest = serviceRequest;
        this.serviceMessage = serviceMessage;
        this.servicePost = servicePost;
        this.dialogStage=stage;
    }
    @FXML
    public void loginButtonClicked(javafx.event.ActionEvent event) {
        String email = username.getText();
        String password = this.password.getText();

        User user = service.login(email, password);
        System.out.println(user);
        if(user==null) {
            MessageAlert.showMessage(dialogStage, Alert.AlertType.ERROR,"Login error","User does not exists!");
            return;
        }
        //dialogStage.close();
       showMainPage(user);
    }

    public void createAccountButtonClicked(javafx.event.ActionEvent even) {
        showAddUserPage();
    }

    public void showMainPage(User user) {
        try {
            //this.dialogStage.close();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/Utilizatorview.fxml"));

            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage2 = new Stage();
            dialogStage2.setTitle("UtilizatorMain");
            dialogStage2.initModality(Modality.WINDOW_MODAL);
            dialogStage2.setResizable(true);
            Scene scene = new Scene(page, 670, 520);
            dialogStage2.setScene(scene);

            FeedController userController = loader.getController();
            userController.setUserService(service,serviceFriendship,serviceRequest,serviceMessage,servicePost,dialogStage2,user);

            dialogStage2.show();
            this.dialogStage.close();
        }catch(IOException e) {
            e.printStackTrace();
        }


    }

   public void showAddUserPage() {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add-user-view.fxml"));
           //loader.setLocation();

           AnchorPane page =loader.load();

           Stage dialogStage = new Stage();
           dialogStage.setTitle("CreateUser");
           dialogStage.initModality(Modality.WINDOW_MODAL);
           dialogStage.setResizable(true);
           Scene scene = new Scene(page, 500, 400);
           dialogStage.setScene(scene);

           AddUserController addUserController = loader.getController();
           addUserController.setUserService(service,dialogStage);

           dialogStage.show();
       }catch(IOException e) {
           e.printStackTrace();
       }
   }
}
