package model.snakeGameNetwork;

import me.ippolitov.fit.snakes.SnakesProto;
import model.networkUtils.NetworkUser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Sender{
    private DatagramSocket socket;

    public Sender(DatagramSocket socket){
        this.socket = socket;
    }

    public void send(SnakesProto.GameMessage message, List<NetworkUser> usersList){
        for(NetworkUser user : usersList) {
            try {
                socket.send(new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                        InetAddress.getByName(user.getIP()), user.getPort()));
            } catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
    }
}
