package model.snakeGame;

import model.game.SnakeGamePlayerI;
import model.networkUtils.MasterNode;
import model.networkUtils.NetworkUser;
import model.networkUtils.NodeRole;

import java.util.Date;

public class MasterPlayer extends MasterNode {
    private SnakeGamePlayerI master, deputy;

   MasterPlayer(SnakeGamePlayerI master){
       this.master = master;
   }

    MasterPlayer(NetworkUser user){
        this.master = new SnakeGamePlayer(user.getID(), user.getName(), 0,
                user.getIP(), user.getPort(), NodeRole.MASTER);
    }

    //create ourself when we are master and starting a new game
    MasterPlayer(NetworkUser user, int id){
        this.master = new SnakeGamePlayer(id, user.getName(), 0,
                user.getIP(), user.getPort(), NodeRole.MASTER);
    }

    @Override
    public int getID() {
        return master.getID();
    }

    @Override
    public void setID(int newID) {
        master.setID(newID);
    }

    @Override
    public String getName() {
        return master.getName();
    }

    @Override
    public void setName(String name) {
        master.setName(name);
    }

    @Override
    public int getScore() {
        return master.getScore();
    }

    @Override
    public void increaseScore(int points) {
        master.increaseScore(points);
    }

    @Override
    public void setDeputy(SnakeGamePlayerI deputy) {
        this.deputy = deputy;
    }

    @Override
    public void replaceMaster() {
        if(deputy != null){
            master = deputy;
            deputy = null;
        }
    }

    @Override
    public NetworkUser getMaster() {
        return master;
    }

    @Override
    public NetworkUser getDeputy() {
        return deputy;
    }

    @Override
    public String getIP() {
        return master.getIP();
    }

    @Override
    public void setIp(String ip) {
        master.setIp(ip);
    }

    @Override
    public int getPort() {
        return master.getPort();
    }

    @Override
    public NodeRole getRole() {
        return NodeRole.MASTER;
    }

    @Override
    public void changeRole(NodeRole nodeRole) {
    }



    @Override
    public Date getLastActivity() {
        return master.getLastActivity();
    }

    @Override
    public void refreshActivity() {
        master.refreshActivity();
    }
}
