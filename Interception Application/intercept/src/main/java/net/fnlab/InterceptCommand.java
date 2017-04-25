package net.fnlab;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
/**
 * Created by zzy on 17-4-17.
 */
@Command(scope = "onos", name = "intercept",
        description = "Configure the interception")
public class InterceptCommand extends AbstractShellCommand {

    private InterceptService interceptService;

    @Argument(index = 0, name = "tcpOrUdp", description = "Set the IP protocol(TCP/UDP)",
            required = true, multiValued = false)
    private String tcpOrUdp = null;

    @Argument(index = 1, name = "ipAddress", description = "Set the IP address",
            required = true, multiValued = false)
    private String ipAddress = null;

    @Argument(index = 2, name = "portNumber", description = "Set the port number of TCP/UDP",
            required = true, multiValued = false)
    private String portNumber = null;

    @Override
    protected void execute() {

        interceptService = get(InterceptService.class);

        if (tcpOrUdp.equals("TCP")) {
            interceptService.startIntercept(true, ipAddress, Integer.parseInt(portNumber));
        } else if (tcpOrUdp.equals("UDP")) {
            interceptService.startIntercept(false, ipAddress, Integer.parseInt(portNumber));
        } else {
            print("Input error!");
        }
    }
}
