package com.envyful.acaf.impl.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class ForgeCommand extends CommandBase {

    private final String name;
    private final String basePermission;
    private final List<String> aliases;

    public ForgeCommand(String name, String basePermission, List<String> aliases) {
        this.name = name;
        this.basePermission = basePermission;
        this.aliases = aliases;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.basePermission);
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    }
}
