package com.mon1tor.radiocraft.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.DirectionFinderItem;
import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import com.mon1tor.radiocraft.radio.client.DirectionFinderTempBuffer;
import com.mon1tor.radiocraft.radio.client.HistoryGUIItemData;
import com.mon1tor.radiocraft.radio.client.guidata.DirectionFinderAdditionalData;
import com.mon1tor.radiocraft.radio.history.DirectionFinderTextHistoryItem;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = Radiocraft.MOD_ID)
public class ModClientEvents {
    @SubscribeEvent
    public static void onClientLogout(final ClientPlayerNetworkEvent.LoggedOutEvent event) {
        HistoryGUIItemData.clearAllData();
    }

    @SubscribeEvent
    public static void onRenderOverlay(final RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.gameMode.getPlayerMode() != GameType.SPECTATOR && !mc.options.hideGui && !mc.options.renderDebug) {
            MatrixStack matrixStack = event.getMatrixStack();
            ItemStack dirFinderStack = ItemStack.EMPTY;

            if(mc.player.getMainHandItem().getItem() == ModItems.DIRECTION_FINDER.get())
                dirFinderStack = mc.player.getMainHandItem();
            else if(mc.player.getOffhandItem().getItem() == ModItems.DIRECTION_FINDER.get())
                dirFinderStack = mc.player.getOffhandItem();

            if(!dirFinderStack.isEmpty() && DirectionFinderItem.isActive(dirFinderStack)) {
                DirectionFinderAdditionalData data = DirectionFinderTempBuffer.getData(StackIdentifierNBT.getStackClientDataUUIDClient(dirFinderStack));
                if(data != null) {
                    IHistoryItem historyItem = data.getHistoryItem();
                    if(historyItem instanceof DirectionFinderTextHistoryItem) {
                        DirectionFinderTextHistoryItem item = (DirectionFinderTextHistoryItem) historyItem;
                        float playerAngle = mc.getCameraEntity().yRot + 180.0f;
                        float angleFrom = item.recieveDirection.angleFrom;
                        float angleTo = item.recieveDirection.angleTo;

                        float dist;
                        if(MathUtils.isAngleBetween(playerAngle, angleFrom, angleTo)){
                            dist = 0.0f;
                        }
                        else {
                            float d1 = MathUtils.distBetweenAngles(playerAngle, angleFrom);
                            float d2 = MathUtils.distBetweenAngles(playerAngle, angleTo);
                            dist = Math.min(d1, d2);
                        }
                        dist /= 180.0f;

                        mc.getTextureManager().bind(ModTextures.DIRECTION_FINDER_GUI_OVERLAY);
                        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 0, 196, 95, 256, 256);

                        RenderSystem.pushMatrix();
                        RenderSystem.disableTexture();

                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder buffer = tessellator.getBuilder();

                        RenderSystem.lineWidth(2);
                        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                        Matrix4f matrix = matrixStack.last().pose();

                        final float xPos = 11.5f;
                        final float yPos = 8.5f;
                        final float xSize = 113.5f;
                        final float ySize = 15.0f;
                        float deltaX = (mc.gui.getGuiTicks() + event.getPartialTicks()) * 20;

                        for(float x = -180.0f; x <= 180.0f; x += 0.1) {
                            float sin = (float) Math.sin(Math.toRadians(x / (dist + 0.1f) + deltaX));
                            buffer.vertex(matrix, xPos + (x / 360.0f) * xSize + xSize / 2.0f, yPos + (sin + 1.0f) * ySize, 0)
                                    .color((Math.abs(sin) * 130.0f) / 225.0f, 170.0f / 255.0f, (Math.abs(sin) * 130.0f) / 225.0f, 1.0f).endVertex();
                        }

                        tessellator.end();
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.enableTexture();
                        RenderSystem.popMatrix();
                    }
                }
            }
        }
    }
}
