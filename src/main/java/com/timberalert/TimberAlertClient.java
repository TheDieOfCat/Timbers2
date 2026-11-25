package com.timberalert;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class TimberAlertClient implements ClientModInitializer {

    private static final String TRIGGER_TEXT = "TIMBER!";
    private static final long DISPLAY_DURATION_MS = 2000L;

    private static long lastProcTime = 0L;
    private static int timberCount = 0;
    private static boolean showCounterHud = true;

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String rawText = message.getString();
            String upper = rawText.toUpperCase();

            int triggerIdx = upper.indexOf(TRIGGER_TEXT);
            if (triggerIdx == -1) {
                return;
            }

            // Anti-troll : ignore si un joueur ecrit "TIMBER!" dans le chat ("Pseudo: TIMBER!")
            int colonIdx = rawText.indexOf(": ");
            boolean looksLikePlayerChat = !overlay && colonIdx != -1 && colonIdx < triggerIdx;

            if (!looksLikePlayerChat) {
                triggerTimberAlert(true);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("timbertest")
                .executes(ctx -> {
                    triggerTimberAlert(true);
                    return 1;
                })
            );

            dispatcher.register(ClientCommands.literal("timberreset")
                .executes(ctx -> {
                    timberCount = 0;
                    ctx.getSource().sendFeedback(Component.literal("[TimberAlert] Compteur remis a 0 !"));
                    return 1;
                })
            );

            dispatcher.register(ClientCommands.literal("timberhud")
                .executes(ctx -> {
                    showCounterHud = !showCounterHud;
                    String etat = showCounterHud ? "active" : "cache";
                    ctx.getSource().sendFeedback(Component.literal("[TimberAlert] Compteur HUD " + etat + "."));
                    return 1;
                })
            );
        });

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("timberalert", "timber_overlay"),
            (graphics, tickCounter) -> renderHud(graphics)
        );
    }

    private static void triggerTimberAlert(boolean incrementCounter) {
        lastProcTime = System.currentTimeMillis();
        if (incrementCounter) {
            timberCount++;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        }
    }

    private static void renderHud(GuiGraphicsExtractor graphics) {
        Minecraft mc = Minecraft.getInstance();

        // Compteur persistant en haut a gauche
        if (showCounterHud) {
            Component counterText = Component.literal("Timbers: " + timberCount);
            int textWidth = mc.font.width(counterText);
            int x = 10;
            int y = 10;
            int padX = 6;
            int padY = 4;

            graphics.fill(x - padX, y - padY, x + textWidth + padX, y + 9 + padY, 0x90000000);
            graphics.text(mc.font, counterText, x, y, 0xFFFFAA00, true);
        }

        // Gros pop-up anime TIMBER! au centre
        long elapsed = System.currentTimeMillis() - lastProcTime;
        if (lastProcTime == 0L || elapsed >= DISPLAY_DURATION_MS) {
            return;
        }

        float progress = (float) elapsed / DISPLAY_DURATION_MS;

        float scale;
        if (progress < 0.12f) {
            scale = 1.5f + (progress / 0.12f) * 3.0f;
        } else if (progress < 0.22f) {
            scale = 4.5f - ((progress - 0.12f) / 0.10f) * 0.5f;
        } else {
            scale = 4.0f;
        }

        float alphaFloat = (progress > 0.7f) ? (1.0f - (progress - 0.7f) / 0.3f) : 1.0f;
        int alpha = Math.max(4, Math.min(255, (int) (alphaFloat * 255)));
        int color = (alpha << 24) | 0x00FFAA00;

        int centerX = graphics.guiWidth() / 2;
        int centerY = graphics.guiHeight() / 3;

        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX, centerY);
        graphics.pose().scale(scale, scale);

        graphics.centeredText(mc.font, Component.literal("TIMBER!"), 0, -4, color);

        graphics.pose().popMatrix();
    }
}
