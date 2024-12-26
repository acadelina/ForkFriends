package ubb.scs.map.lab6.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.ServiceException;
import ubb.scs.map.lab6.service.ServiceUser;


public class AddUserController {
    private ServiceUser serviceUser;
    private Stage stage;

    @FXML
    private TextField username;
    @FXML

    private TextField password;

    @FXML
    private TextField email;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private Button submit;
    @FXML
    private Button cancel;

    public void setUserService(ServiceUser serviceUser,Stage stage) {
        this.serviceUser = serviceUser;
        this.stage = stage;
    }

    @FXML
    public void handleSubmit(javafx.event.ActionEvent event) {
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String email = this.email.getText();
        String password = this.password.getText();
        String username = this.username.getText();

        try{
            serviceUser.addUser(firstName,lastName,email,username,password);
        }
        catch (ServiceException e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR,"Create user error",e.getMessage());
            //stage.close();
            return;
        }
        MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Create user","User created successfully");
        stage.close();
    }

    public void handleCancel(javafx.event.ActionEvent event) {
        stage.close();
    }

}
