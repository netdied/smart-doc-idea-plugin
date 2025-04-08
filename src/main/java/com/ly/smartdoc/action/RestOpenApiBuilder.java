package com.ly.smartdoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.ly.doc.builder.openapi.OpenApiBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;

import java.util.Objects;

public class RestOpenApiBuilder extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        ApiConfig apiConfig = CommonConstant.getApiConfig();
        if (Objects.isNull(apiConfig)) {
            CommonConstant.showErrorMessage(ErrorCoedEnum.JSON_ERROR);
        }
        try {
            OpenApiBuilder.buildOpenApi(apiConfig);
<<<<<<< HEAD
            CommonConstant.showMessage("build success ,file path :  "
                    + ApiConfig.getInstance().getOutPath());
        } catch (Exception ex) {
=======
>>>>>>> 164b6e8776a5f658649b474a3b393a72b8c046b1
        } catch (Exception ex) {
            CommonConstant.showErrorMessage(ex.getMessage());
        }
    }
}
