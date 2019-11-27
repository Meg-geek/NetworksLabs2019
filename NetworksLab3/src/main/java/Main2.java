import chatNode.ChatNode;
import node.Node;

public class Main2 {
    public static void main(String[] args){
        try{
            Node node = new ChatNode("Коля", 2001, 50, "192.168.56.1", 2000);
            node.start();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
