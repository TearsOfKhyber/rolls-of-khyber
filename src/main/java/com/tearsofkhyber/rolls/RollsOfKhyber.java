package com.tearsofkhyber.rolls;

import com.rpkit.core.bukkit.plugin.RPKBukkitPlugin;
import com.tearsofkhyber.rolls.command.RollCommand;

public final class RollsOfKhyber extends RPKBukkitPlugin {

    @Override
    public void onEnable() {
        getCommand("roll").setExecutor(new RollCommand(this));
    }

}
