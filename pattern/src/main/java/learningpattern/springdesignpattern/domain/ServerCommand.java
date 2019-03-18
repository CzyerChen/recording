package learningpattern.springdesignpattern.domain;

public abstract class ServerCommand {
    public Server server;

    public ServerCommand(Server server){
        this.server = server;
    }

    public abstract void execute();
}
