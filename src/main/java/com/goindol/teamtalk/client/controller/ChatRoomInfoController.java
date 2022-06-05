package com.goindol.teamtalk.client.controller;

import com.goindol.teamtalk.client.model.UserDTO;
import com.goindol.teamtalk.client.service.ChatRoomListDAO;
import com.goindol.teamtalk.client.service.ChatRoomUserListDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatRoomInfoController implements Initializable {

    public UserDTO userDTO;
    public int chatId;
    public MainController mainController;
    public ChatRoomListDAO chatRoomListDAO = ChatRoomListDAO.getInstance();
    public ChatRoomUserListDAO chatRoomUserListDAO = ChatRoomUserListDAO.getInstance();
    @FXML private ListView chatRoomUserList;
    @FXML private TextField userInput;
    @FXML private Button invite;
    @FXML private Button exitRoom;


    public void showChatRoomUserList() {
       List<String> strings = new ArrayList<>();
        ArrayList<String> chatRoomUsers = chatRoomUserListDAO.getChatRoomUser(chatId);
        for(String users : chatRoomUsers) {
            strings.add(users);
        }
        ObservableList<String> chatRoomObservableUserList = FXCollections.observableList(strings);

        chatRoomUserList.setItems(chatRoomObservableUserList);
    }

    public void inviteFriend() {
        System.out.println("chatId = " + chatId);
        if(chatRoomUserListDAO.overlapUser(chatId,userInput.getText())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("warning");
            alert.setHeaderText("채팅방 친구 오류");
            alert.setContentText("이미 채팅방에 존재하는 친구입니다.");
            alert.show();
        }else {
            if(chatRoomListDAO.checkFriend(userDTO.getNickName(),userInput.getText())) {
                chatRoomListDAO.inviteChatRoom(chatId, userInput.getText());
                ObservableList<String> chatRoomUserListItems = chatRoomUserList.getItems();
                chatRoomUserListItems.add(userInput.getText());
                chatRoomUserList.setItems(chatRoomUserListItems);
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("warning");
                alert.setHeaderText("친구 오류");
                alert.setContentText("친구 목록에 존재하지 않습니다.");
                alert.show();
            }
        }
    }

    public void existRoom() {
        chatRoomUserListDAO.existRoom(chatId, userDTO.getNickName());

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       chatRoomUserList.setEditable(false);
        invite.setOnMouseClicked(event-> {
            inviteFriend();
            mainController.send("chatRoom/"+ chatId + "/" + userInput.getText());
            userInput.setText("");
        });

        exitRoom.setOnMouseClicked(event-> {
            existRoom();
            //info 창 닫고 채팅방 페이지를 메인페이지로 이동

        });
    }

    public void setChatRoomId(int chatId) {
        this.chatId = chatId;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public void setMainController(MainController mainController) { this.mainController = mainController; }
}
