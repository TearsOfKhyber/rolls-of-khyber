package com.tearsofkhyber.rolls.command;

import com.rpkit.characters.bukkit.character.RPKCharacter;
import com.rpkit.characters.bukkit.character.RPKCharacterProvider;
import com.rpkit.core.exception.UnregisteredServiceException;
import com.rpkit.players.bukkit.profile.RPKMinecraftProfile;
import com.rpkit.players.bukkit.profile.RPKMinecraftProfileProvider;
import com.tearsofkhyber.rolls.RollsOfKhyber;
import com.tearsofkhyber.rolls.roll.Roll;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.tearsofkhyber.rolls.roll.RollPartResult;

import java.util.List;

import static org.bukkit.ChatColor.*;

public final class RollCommand implements CommandExecutor {

    private final RollsOfKhyber plugin;

    public RollCommand(RollsOfKhyber plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String[] args) {
        Player commandSender = (Player) sender;
        RPKMinecraftProfileProvider minecraftProfileProvider;
        try {
            minecraftProfileProvider = plugin.core.getServiceManager().getServiceProvider(RPKMinecraftProfileProvider.class);
        } catch (UnregisteredServiceException exception) {
            sender.sendMessage(RED + "There is no Minecraft profile provider available.");
            return true;
        }
        RPKMinecraftProfile minecraftProfile = minecraftProfileProvider.getMinecraftProfile(commandSender);
        if (minecraftProfile == null) {
            sender.sendMessage(RED + "You do not have a Minecraft profile.");
            return true;
        }
        RPKCharacterProvider characterProvider;
        try {
            characterProvider = plugin.core.getServiceManager().getServiceProvider(RPKCharacterProvider.class);
        } catch (UnregisteredServiceException exception) {
            sender.sendMessage(RED + "There is no character provider available.");
            return true;
        }
        RPKCharacter character = characterProvider.getActiveCharacter(minecraftProfile);
        if (character == null) {
            sender.sendMessage(RED + "You do not have an active character.");
            return true;
        }
        if (args.length == 1) {
            try {
                Roll input = Roll.parse(args[0]);
                List<RollPartResult> partResultList = input.roll();
                for (Player player : commandSender.getWorld().getPlayers()) {
                    if (player.getLocation().distanceSquared(commandSender.getLocation()) <= 400) {
                        int result = partResultList.stream()
                                .mapToInt(RollPartResult::getResult)
                                .sum();
                        player.sendMessage(ChatColor.GOLD + character.getName() + ChatColor.WHITE + " rolled " + input.toDisplayString());
                        player.sendMessage(ChatColor.GOLD + "Result: " +
                                partResultList.stream()
                                        .map(rollPartResult -> {
                                            if (rollPartResult.getRollPart() instanceof Roll.Die) {
                                                return AQUA + rollPartResult.toString() + WHITE;
                                            } else if (rollPartResult.getRollPart() instanceof Roll.Modifier) {
                                                return YELLOW + rollPartResult.toString() + WHITE;
                                            } else {
                                                return rollPartResult.toString();
                                            }
                                        })
                                        .reduce((a, b) -> a + "+" + b)
                                        .orElse("")
                                + " = " + result);
                    }
                }
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(RED + "Please enter your dice rolls in the following example format: 1d10+3d10+5.");
                return true;
            }
        }
        else {
            sender.sendMessage(RED + "Please enter your dice rolls in the following example format: 1d10+3d10+5.");
            return true;
        }
    }
}
