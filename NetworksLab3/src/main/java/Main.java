import chatNode.ChatNode;
import node.Node;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
        try{
            if(checkArgs(args)){
                Node node = createNode(args);
                node.start();
            } else {
                System.out.println("Wrong arguments, try again");
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static Node createNode(String[] args) throws IOException {
        if(args.length == 3){
            return new ChatNode(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[2]));
        }
        if(args.length == 5){
            return new ChatNode(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[2]),
                    args[3], Integer.valueOf(args[4]));
        }
        return null;
    }

    private static boolean checkArgs(String[] args){
        if((args.length != 3 && args.length != 5)
            || args.length < 3){
            return false;
        }
        try {
            int port = Integer.valueOf(args[1]);
            int perc = Integer.valueOf(args[2]);
            if(args.length == 5){
                port = Integer.valueOf(args[4]);
            }
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }
}
