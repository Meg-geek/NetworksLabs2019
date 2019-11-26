package model.snakeGameNetwork;

import model.networkUtils.NetworkUser;
import model.networkUtils.NodeRole;

import java.util.Date;

public class SnakeNetworkUser implements NetworkUser {
    private int id, port;
    private Date lastActivity = new Date();
    private NodeRole nodeRole;
    private String ip;

    public SnakeNetworkUser(int id, NodeRole role, String ip, int port){
        this.id = id;
        nodeRole = role;
        this.ip = ip;
        this.port = port;
    }
    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public NodeRole getRole() {
        return nodeRole;
    }

    @Override
    public void changeRole(NodeRole nodeRole) {
        if(nodeRole != null){
            this.nodeRole = nodeRole;
        }
    }
/*
    @Override
    public void sendMessage(Message message, List<NetworkUser> usersList) {

    }

 */

    @Override
    public Date getLastActivity() {
        return lastActivity;
    }

    @Override
    public void refreshActivity() {
        lastActivity = new Date();
    }

    /*@Override
    public void recieveMessage(Message message) {

    }

     */

    @Override
    public boolean equals(Object obj){
        if(! (obj instanceof NetworkUser)){
            return false;
        }
        if(obj == this){
            return true;
        }
        NetworkUser user = (NetworkUser)obj;
        return (user.getIP().equals(ip) && user.getPort() == port && user.getID() == id);
    }
}
