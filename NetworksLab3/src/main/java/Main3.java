import chatNode.ChatNode;
import node.Node;

public class Main3 {
    public static void main(String[] args){
        try{
            Node node = new ChatNode("Петя", 2000, 40);
            node.start();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
