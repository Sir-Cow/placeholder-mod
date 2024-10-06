package sircow.placeholder.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import sircow.placeholder.Placeholder;
import sircow.placeholder.block.entity.NewCauldronBlockData;
import sircow.placeholder.block.entity.NewLoomBlockData;

public class ModScreenHandlers {
    public static final ScreenHandlerType<NewCauldronBlockScreenHandler> NEW_CAULDRON_BLOCK_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Placeholder.MOD_ID, "new_cauldron"),
                    new ExtendedScreenHandlerType<>(NewCauldronBlockScreenHandler::new, NewCauldronBlockData.PACKET_CODEC));

    public static final ScreenHandlerType<NewLoomBlockScreenHandler> NEW_LOOM_BLOCK_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Placeholder.MOD_ID, "new_loom"),
                    new ExtendedScreenHandlerType<>(NewLoomBlockScreenHandler::new, NewLoomBlockData.PACKET_CODEC));

    public static void registerScreenHandlers() {
        Placeholder.LOGGER.info("Registering Screen Handlers for " + Placeholder.MOD_ID);
    }
}