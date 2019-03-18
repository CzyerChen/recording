package learningpattern.springdesignpattern.domain;

public class StartApplicationCommand extends ServerCommand {

    public StartApplicationCommand(Server server) {
        super(server);
    }

    @Override
    public void execute() {
        server.addCommand("java -jar app.jar");
    }
}
