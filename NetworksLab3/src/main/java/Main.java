import node.ChatNode;
import node.Node;

public class Main {
    public static void main(String[] args){
        try{
            Node node = new ChatNode("Петя", 2001, 30);
            node.start();
        } catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
