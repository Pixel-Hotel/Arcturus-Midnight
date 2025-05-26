package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class PackageLoggingCommand extends Command{
    public PackageLoggingCommand() {
        super("cmd_shutdown", new String[]{"plc"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        Emulator.enabledPackageLogging = !Emulator.enabledPackageLogging;
        return true;
    }
}
