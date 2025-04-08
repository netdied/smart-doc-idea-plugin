package com.ly.smartdoc.common;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.config.SmartDocSettings;
import com.ly.smartdoc.util.ConfigUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CommonConstant {
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
        Messages.showMessageDialog(content, "error", Messages.getErrorIcon());
    }

    public static void showErrorMessage(ErrorCoedEnum errorCoedEnum) {
        Messages.showMessageDialog(errorCoedEnum.getMessage(), errorCoedEnum.getCode(), Messages.getErrorIcon());
    }
<<<<<<< HEAD

    public static void showMessage(String content) {
        Messages.showMessageDialog(content, "success", Messages.getInformationIcon());
    }
=======
>>>>>>> 164b6e8776a5f658649b474a3b393a72b8c046b1
}
