package dev.mg95.xungeontimer;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.*;

public class XungeonTimer implements ModInitializer {
    public Map<UUID, StopWatch> timers = new HashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(StartPayload.ID, StartPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SplitPayload.ID, SplitPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EndPayload.ID, EndPayload.CODEC);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            timers.remove(handler.player.getUuid());
        });


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("xungeon")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("start").then(argument("player", EntityArgumentType.player()).executes(context -> {
                            final var player = EntityArgumentType.getPlayer(context, "player");
                            var timer = StopWatch.create();
                            timer.start();
                            timers.put(player.getUuid(), timer);

                            ServerPlayNetworking.send(player, new StartPayload());

                            return 1;
                        }))
                )
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("xungeon")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("end").then(argument("player", EntityArgumentType.player()).executes(context -> {
                            final var player = EntityArgumentType.getPlayer(context, "player");

                            timers.get(player.getUuid()).stop();

                            ServerPlayNetworking.send(player, new EndPayload(timers.get(player.getUuid()).getDuration().toMillis()));

                            timers.remove(player.getUuid());

                            return 1;
                        }))
                ))
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("xungeon")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("split").then(argument("player", EntityArgumentType.player())
                                .then(argument("split", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            final var player = EntityArgumentType.getPlayer(context, "player");
                                            final var name = StringArgumentType.getString(context, "split");

                                            ServerPlayNetworking.send(player, new SplitPayload(timers.get(player.getUuid()).getDuration().toMillis(), name));

                                            return 1;
                                        }))
                        )
                ))
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("xungeon")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(literal("list").executes(context -> {
                            for (Map.Entry<UUID, StopWatch> set : timers.entrySet()) {
                                context.getSource().sendFeedback(() -> Text.literal(String.format("%s: %s", set.getKey(), set.getValue())), false);
                            }
                            return 1;
                        }))
                )
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("xungeon")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(literal("clear").executes(context -> {
                            timers.clear();
                            return 1;
                        }))
                )
        );


    }
}
