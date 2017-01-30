package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class Controller implements Initializable, Closeable {
    @FXML
    TableView contactTable;
    @FXML
    TableColumn tableNameColumn, tableAddressColumn;
    @FXML
    TextArea textArea, logArea;
    @FXML
    TextField inputTextField, inputNameField;
    @FXML
    Tab logTab, chatTab;



    byte buffer[] = new byte[1024];
    MemberList memberList = new MemberList();
    MemberThread memberThread;

    DatagramSocket socket;
    DatagramPacket inPacket, outPacket, memberPacket;
    int portChat = 9999;
    int portMember = 10000;

    @FXML
    public void enterMessage(){
        textArea.setText(inputTextField.getText());
        inputTextField.clear();
    }

    public void initialisation(){
        String name = inputNameField.getText();
        Member user = new Member();
        if (name!=null){
            try {
                InetAddress address = InetAddress.getLocalHost();
                user.setName(name);
                user.setAddress(address);
                memberList.add(user);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        addToLog("Username: "+user.getName());
        addToLog("Address:"+user.getAddress().toString());
        addToLog("Creating DatagramSocket...");
        createSocket();
        addToLog("Start hello thread...");
        startHelloThread();
    }

    @FXML
    public void addToLog(String msg){
        logArea.setText(logArea.getText()+"\n"+msg);
    }

    public void createSocket(){
//        try {
//            socket = new DatagramSocket();
//            addToLog("Socket Created!");
//        } catch (SocketException e) {
//            addToLog("Creating Socket Error!");
//            e.printStackTrace();
//        }
//
//        inPacket = new DatagramPacket(buffer,buffer.length);
//        inPacket.setPort(portChat);

        chatTab.setDisable(false);
    }



    public void tableInit(){
        contactTable.refresh();
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<Member,String>("Name"));
        tableAddressColumn.setCellValueFactory(new PropertyValueFactory<Member,String>("Address"));
        contactTable.setItems(memberList.getMembers());
    }

    public void startHelloThread(){
        memberThread = new MemberThread();
        memberThread.setPort(portMember);
        memberThread.setMemberList(memberList);
        memberThread.run();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableInit();
    }

    @Override
    public void close() throws IOException {
        memberThread.isWorking = false;
    }
}
