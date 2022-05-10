package com.mon1tor.radiocraft;

import com.mon1tor.radiocraft.radio.client.HistoryGUIItemData;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEventHandler {
    @SubscribeEvent
    public void onClientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        HistoryGUIItemData.clearAllData();
    }
}
