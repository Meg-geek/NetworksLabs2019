package model.snakeGameNetwork;

import model.networkUtils.NetworkUser;
import model.networkUtils.NodeRole;

import java.util.Date;

public class SnakeNetworkUser implements NetworkUser {
    private int id, port;
    private Date lastActivity = new Date();
    private NodeRole nodeRole;
    private String ip, name;

    public SnakeNetworkUser(int id, NodeRole role, String ip, int port){
        this(ip, port);
        this.id = id;
        nodeRole = role;
    }

    public SnakeNetworkUser(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public SnakeNetworkUser(int port, String name){
        this.port = port;
        this.name = name;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
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
    public void setID(int newID) {
        id = newID;
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

    @Override
    public Date getLastActivity() {
        return lastActivity;
    }

    @Override
    public void refreshActivity() {
        lastActivity = new Date();
    }

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
