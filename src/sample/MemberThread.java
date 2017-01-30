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

    DatagramPacket inPacket, outPacket;
    DatagramSocket socket;
    MemberList memberList;
    int port;

    byte [] inBuffer = new byte[1024];
    byte [] outBuffer = new byte[1024];

    public void run() {
        isWorking=true;
        try {
            socket = new DatagramSocket(port);
            socket.connect(InetAddress.getByName("255.255.255.255"), port);
            socket.setBroadcast(true);
            socket.setSoTimeout(10000);
            sending();
            listen();
        } catch (SocketException e) {
            System.out.println(e);
            //e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
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
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        byte[] buf = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buf,buf.length);
                        socket.receive(packet);
                        String message = new String(buf);
                        System.out.println("Recieved: " + message);
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
