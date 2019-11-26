package model.networkUtils;

import java.util.List;

public interface PingSender {
    void setNewPingList(List<NetworkUser> usersList);
    void setNewPingDelay(int newPingDelay);
}
