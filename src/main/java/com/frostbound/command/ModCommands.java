package com.frostbound.command;

import com.frostbound.registry.ModAttachments;
import com.frostbound.temperature.TemperatureManager;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 *  /frostbound info          — диагностика температуры (для всех)
 *  /frostbound set <temp>    — задать температуру тела (op)
 */
public final class ModCommands {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("frostbound")
                .then(Commands.literal("info").executes(ModCommands::info))
                .then(Commands.literal("set")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("temp", FloatArgumentType.floatArg(TemperatureManager.MIN, TemperatureManager.NORMAL))
                                .executes(ModCommands::set))));
    }

    private static int info(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TemperatureManager.Snapshot s = TemperatureManager.sample(player);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.frostbound.info",
                fmt(s.body()), fmt(s.air()), fmt(s.heat()), fmt(s.insulation()),
                fmt((float) s.worldDrop()), fmt(s.storm() * 100.0F)), false);
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        float temp = FloatArgumentType.getFloat(ctx, "temp");
        player.setData(ModAttachments.BODY_TEMP.get(), temp);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.frostbound.set", fmt(temp)), true);
        return 1;
    }

    private static String fmt(float value) {
        return String.format("%.1f", value);
    }

    private ModCommands() {}
}
