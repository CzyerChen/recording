package learningpattern.springdesignpattern.domain;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<String> commands= new ArrayList<>();

    public void addCommand(String command){
        System.out.println("add command:"+command);
        commands.add(command);
    }

    public List<String> getCommands(){
        return this.commands;
    }
 }
