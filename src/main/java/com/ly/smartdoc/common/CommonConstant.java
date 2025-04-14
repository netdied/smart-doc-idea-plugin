package com.ly.smartdoc.common;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.config.SmartDocSettings;
import com.ly.smartdoc.util.ConfigUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CommonConstant {
    public static final String BUILD_HTML = "build html ";
    public static final String BUILD_TORNA = "build torna ";
    public static final String BUILD_POSTMAN = "build postman ";
    public static final String BUILD_OPENAPI = "build openapi ";

    public static final String GET_CURL = "get curl openapi ";

    public static final String BUILD_SUCCESS = "smart-doc build success";
    public static final String BUILD_CANCEL = "Build canceled by user.";

    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    private static final AtomicReference<ApiConfig> API_CONFIG = new AtomicReference<>();

    public static ApiConfig getApiConfig() {
        if (Objects.isNull(API_CONFIG.get())) {
            SmartDocSettings settings = ServiceManager.getService(SmartDocSettings.class);
            if (settings != null) {
                String configValue = settings.getJsonContent();
                ConfigUtil.buildConfig(configValue);
            }
        }
        return API_CONFIG.get();
    }

    public static void setApiConfig(ApiConfig value) {
        API_CONFIG.set(value);
    }

    public static void showErrorMessage(String content) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                Messages.showMessageDialog(content, ERROR, Messages.getErrorIcon());
            });
        } else {
            Messages.showMessageDialog(content, ERROR, Messages.getErrorIcon());
        }
    }

    public static void showErrorMessage(ErrorCoedEnum errorCoedEnum) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                Messages.showMessageDialog(errorCoedEnum.getMessage(), errorCoedEnum.getCode(), Messages.getErrorIcon());
            });
        } else {
            Messages.showMessageDialog(errorCoedEnum.getMessage(), errorCoedEnum.getCode(), Messages.getErrorIcon());
        }


    }

    public static void showMessage(String content) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                Messages.showMessageDialog(content, SUCCESS, Messages.getInformationIcon());
            });
        } else {
            Messages.showMessageDialog(content, SUCCESS, Messages.getInformationIcon());
        }

    }
}
