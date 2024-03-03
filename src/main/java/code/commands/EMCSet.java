package code.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import code.ui.TransmutationTable;

public class EMCSet extends ConsoleCommand {
    public EMCSet() {
        maxExtraTokens = 1;
        minExtraTokens = 1;
        requiresPlayer = true;
    }

    @Override
    protected void execute(String[] strings, int i) {
        int val;
        try{
            val = Integer.parseInt(strings[i]);
        } catch (NumberFormatException e) {
            errorMsg();
            return;
        }

        TransmutationTable.PLAYER_EMC = val;
        TransmutationTable.exchangeScreen.currentPanel.onChangeEMC();
    }

    @Override
    protected void errorMsg() {
        DevConsole.couldNotParse();
    }
}
