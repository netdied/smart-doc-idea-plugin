package com.ly.smartdoc.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.ui.components.JBScrollPane;
import com.ly.smartdoc.util.ConfigUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingConfigurable implements Configurable {

    private final SmartDocSettings settings = ApplicationManager.getApplication().getService(SmartDocSettings.class);
    private JPanel myPanel;
    private JTextArea jsonTextArea;

    @Override
    public @ConfigurableName String getDisplayName() {
        return "SmartDoc";
    }

    @Override
    public @Nullable JComponent createComponent() {
        myPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Enter JSON Content Below:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        myPanel.add(titleLabel, BorderLayout.NORTH);

        jsonTextArea = new JTextArea(10, 40);
        jsonTextArea.setLineWrap(true);
        jsonTextArea.setWrapStyleWord(true);

        JBScrollPane scrollPane = new JBScrollPane(jsonTextArea);
        myPanel.add(scrollPane, BorderLayout.CENTER);

        return myPanel;
    }

    @Override
    public boolean isModified() {
        return !jsonTextArea.getText().equals(settings.getJsonContent());
    }

    @Override
    public void apply() {
        settings.setJsonContent(jsonTextArea.getText());
        ConfigUtil.buildConfig(jsonTextArea.getText());
    }

    @Override
    public void reset() {
        jsonTextArea.setText(settings.getJsonContent());
    }

    @Override
    public void disposeUIResources() {
        myPanel = null;
        jsonTextArea = null;
    }


}

