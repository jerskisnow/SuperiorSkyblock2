package com.bgsoftware.superiorskyblock.commands.command.admin;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPermission;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.ICommand;
import com.bgsoftware.superiorskyblock.island.SPlayerRole;
import com.bgsoftware.superiorskyblock.utils.StringUtils;
import com.bgsoftware.superiorskyblock.wrappers.SSuperiorPlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CmdAdminSetPermission implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("setpermission", "setperm");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setpermission";
    }

    @Override
    public String getUsage() {
        return "island admin setpermission <player-name/island-name/*> <permission> <role>";
    }

    @Override
    public String getDescription() {
        return Locale.COMMAND_DESCRIPTION_ADMIN_SET_PERMISSION.getMessage();
    }

    @Override
    public int getMinArgs() {
        return 5;
    }

    @Override
    public int getMaxArgs() {
        return 5;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer targetPlayer = SSuperiorPlayer.of(args[2]);
        List<Island> islands = new ArrayList<>();

        if(args[2].equalsIgnoreCase("*")){
            islands = plugin.getGrid().getIslands();
        }

        else {
            Island island = targetPlayer == null ? plugin.getGrid().getIsland(args[2]) : targetPlayer.getIsland();

            if (island == null) {
                if (args[2].equalsIgnoreCase(sender.getName()))
                    Locale.INVALID_ISLAND.send(sender);
                else if (targetPlayer == null)
                    Locale.INVALID_ISLAND_OTHER_NAME.send(sender, args[2]);
                else
                    Locale.INVALID_ISLAND_OTHER.send(sender, targetPlayer.getName());
                return;
            }

            islands.add(island);
        }

        IslandPermission islandPermission;

        try{
            islandPermission = IslandPermission.valueOf(args[2].toUpperCase());
        }catch(IllegalArgumentException ex){
            Locale.INVALID_ISLAND_PERMISSION.send(sender, args[2], StringUtils.getPermissionsString());
            return;
        }

        PlayerRole playerRole;

        try{
            playerRole = SPlayerRole.of(args[3]);
        }catch(IllegalArgumentException ex){
            Locale.INVALID_ROLE.send(sender, args[2], SPlayerRole.getValuesString());
            return;
        }

        islands.forEach(island -> island.setPermission(playerRole, islandPermission, true));

        Locale.PERMISSION_CHANGED.send(sender);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if(args.length == 3){
            list.addAll(Arrays.stream(IslandPermission.values())
                    .map(islandPermission -> islandPermission.toString().toLowerCase())
                    .filter(islandPermissionName -> islandPermissionName.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList())
            );
        }
        else if(args.length == 4){
            list.addAll(plugin.getPlayers().getRoles().stream()
                    .map(playerRole -> playerRole.toString().toLowerCase())
                    .filter(playerRoleName -> playerRoleName.startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList())
            );
        }

        return list;
    }
}