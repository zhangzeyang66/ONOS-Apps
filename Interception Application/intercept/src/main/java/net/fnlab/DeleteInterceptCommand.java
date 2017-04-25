package net.fnlab;

import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

/**
 * Created by zzy on 17-4-19.
 */
@Command(scope = "onos", name = "del-intercept",
        description = "Delete the interception")
public class DeleteInterceptCommand extends AbstractShellCommand {

    private InterceptService interceptService;

    @Override
    protected void execute() {
        interceptService = get(InterceptService.class);
        interceptService.deleteIntercept();
    }
}
