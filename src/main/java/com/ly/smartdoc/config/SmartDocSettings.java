package com.ly.smartdoc.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

@State(
        name = "SmartDocSettings",
        storages = @Storage(value = "smart_doc_settings.xml")
)
public class SmartDocSettings implements PersistentStateComponent<SmartDocSettings> {
    private volatile String jsonContent = "{\"outPath\":\"D:/md2\"}";

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    @Override
    public @Nullable SmartDocSettings getState() {
        return this;
    }

    @Override
    public void loadState(SmartDocSettings state) {
        if (state != null && state.jsonContent != null) {
            System.out.println("ApiConfig : " + state.jsonContent);
            this.jsonContent = state.jsonContent;
        }
    }
}
