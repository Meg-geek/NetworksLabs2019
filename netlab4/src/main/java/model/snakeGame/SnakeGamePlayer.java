package model.snakeGame;

import model.game.SnakeGamePlayerI;
import model.networkUtils.NodeRole;

import java.util.Date;

public class SnakeGamePlayer implements SnakeGamePlayerI {
    private int score;
    private int id, port;
    private String name, ip;
    private NodeRole role;
    private Date lastActivity = new Date();

    public SnakeGamePlayer(int id, String name, int score, String ip, int port, NodeRole role){
        this(name, port);
        this.id = id;
        this.score = score;
        this.ip = ip;
        this.role = role;
    }

    SnakeGamePlayer(String name, int port){
        this.name = name;
        this.port = port;
        role = null;
        ip = null;
    }

    SnakeGamePlayer(String name){
        this.name = name;
    }

    @Override
    public String getIP() {
        if(ip == null){
            return "";
        }
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
        this.id = newID;
    }

    @Override
    public NodeRole getRole() {
        return role;
    }

    @Override
    public void changeRole(NodeRole nodeRole) {
        role = nodeRole;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void increaseScore(int points) {
        if(points > 0){
            score+=points;
        }
    }
}
