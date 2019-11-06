package chatNode.helpers;

import chatNode.ChatNode;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleListener implements Runnable {
    private Scanner input;
    private ChatNode chatNode;

    public ConsoleListener(ChatNode chatNode){
        input = new Scanner(System.in);
        this.chatNode = chatNode;
    }

    @Override
    public void run() {
        try{
            if(input.hasNextLine()){
                chatNode.sendText(input.nextLine());
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
