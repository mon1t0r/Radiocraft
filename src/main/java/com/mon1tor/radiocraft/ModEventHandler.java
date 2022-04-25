package com.mon1tor.radiocraft;

import com.mon1tor.radiocraft.radio.client.RadioGUIData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEventHandler {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        RadioGUIData.clearAllData();
    }
}
