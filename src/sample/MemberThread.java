package sample;

import javafx.fxml.FXML;
import sample.Member;

import java.io.IOException;
import java.net.*;

/**
 * Created by Alex on 27.01.2017.
 */
public class MemberThread {

    public static boolean isWorking;

    private DatagramPacket inPacket;
    private DatagramPacket outPacket;
    private DatagramSocket socket;
    private DatagramSocket receiveSocket;
    private MemberList memberList;
    private int port;

    byte [] inBuffer = new byte[1024];
    byte [] outBuffer = new byte[1024];

    public void run() {
        isWorking=true;
        try {
            socket = new DatagramSocket();
            receiveSocket = new DatagramSocket(port);
            //socket.connect(InetAddress.getByName("255.255.255.255"), port);
            socket.setBroadcast(true);
            receiveSocket.setBroadcast(true);
            receiveSocket.setSoTimeout(10000);
            socket.setSoTimeout(10000);
            sending();
            listen();
        } catch (SocketException e) {
            System.out.println(e);
            //e.printStackTrace();
        }
    }

    public void setPort(int port){
        this.port=port;
    }

    public void setMemberList(MemberList list){
        this.memberList=list;
    }

    public void sending(){
        System.out.println("Start hello");
        new Thread() {
            public void run() {
                byte buffer[] = new byte[1024];
                Member user = memberList.get(0);
                String hello = new String(user.getName()+" "+user.getAddress());
                System.out.println(hello);
                buffer = hello.getBytes();
                DatagramPacket packet = null;
                try {
                    packet = new DatagramPacket(buffer,buffer.length, InetAddress.getByName("255.255.255.255"),port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                while (true){
                    try {
                        socket.send(packet);
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isWorking) break;
                }
            }
        }.start();
    }

    public void listen(){
        System.out.println("Start listen");
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        byte[] buf = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buf,buf.length);
                        receiveSocket.receive(packet);
                        String message = new String(buf);
                        System.out.println("Recieved: " + message);
                        String msg [] = message.trim().split("\\s+");
                        String name = msg[0];
                        String addr[] = msg[1].trim().split("/");
                        String address = addr[1];
                        System.out.println(name+" "+address);
                        memberList.add(new Member(name,InetAddress.getByName(address)));
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    finally {
                        if (!isWorking)
                            break;
                    }
                }
            }
        }.start();
    }

}
