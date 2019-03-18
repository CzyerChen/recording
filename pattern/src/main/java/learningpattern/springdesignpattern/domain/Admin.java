package learningpattern.springdesignpattern.domain;

public class Admin {
    private ServerCommand serverCommand;

    public void addCommand(ServerCommand command){
        this.serverCommand = command;
    }

    public void start(){
         serverCommand.execute();
    }
}
