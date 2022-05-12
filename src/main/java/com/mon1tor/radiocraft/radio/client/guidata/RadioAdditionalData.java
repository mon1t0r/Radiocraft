package com.mon1tor.radiocraft.radio.client.guidata;

public class RadioAdditionalData implements IAdditionalGUIItemData {
    private String writingMessage;

    public RadioAdditionalData(String writingMessage) {
        this.writingMessage = writingMessage;
    }

    public RadioAdditionalData() {
        this("");
    }

    public String getWritingMessage() {
        return writingMessage;
    }

    public void setWritingMessage(String writingMessage) {
        this.writingMessage = writingMessage;
    }
}
