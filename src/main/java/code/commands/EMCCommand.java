package code.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public class EMCCommand extends ConsoleCommand {
    public EMCCommand() {
        maxExtraTokens = 2;
        minExtraTokens = 2;
        requiresPlayer = true;

        followup.put("set", EMCSet.class);
    }

    @Override
    protected void execute(String[] strings, int i) {
        errorMsg();
    }

    @Override
    protected void errorMsg() {
        DevConsole.couldNotParse();
    }
}
