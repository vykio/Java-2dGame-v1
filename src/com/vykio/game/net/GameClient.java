package com.vykio.game.net;

import com.vykio.game.Game;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Game game;

    public GameClient(Game game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String msg = new String(packet.getData());
            System.out.println("Server ["+ packet.getAddress().getHostAddress() +":"+ packet.getPort() +"] > " + msg);
        }
    }

    public void sendData(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
