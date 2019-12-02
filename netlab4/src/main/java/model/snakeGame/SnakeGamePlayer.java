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
        this.id = id;
        this.name = name;
        this.score = score;
        this.ip = ip;
        this.port = port;
        this.role = role;
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
