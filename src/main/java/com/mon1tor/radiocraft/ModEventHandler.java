package com.mon1tor.radiocraft;

import com.mon1tor.radiocraft.radio.client.RadioGUIData;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEventHandler {
    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) { //TODO: Does not fire
        RadioGUIData.clearAllData();
    }
}
