package model.networkUtils;

import model.game.SnakeGamePlayerI;

public abstract class MasterNode implements SnakeGamePlayerI {
    public abstract void setDeputy(SnakeGamePlayerI deputy);
    public abstract void replaceMaster();
    public abstract NetworkUser getMaster();
    public abstract NetworkUser getDeputy();
}
