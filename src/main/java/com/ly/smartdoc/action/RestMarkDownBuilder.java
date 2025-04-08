package com.ly.smartdoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.ly.doc.builder.ApiDocBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;

import java.util.Objects;


public class RestMarkDownBuilder extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        ApiConfig apiConfig = CommonConstant.getApiConfig();
        if (Objects.isNull(apiConfig)) {
            CommonConstant.showErrorMessage(ErrorCoedEnum.JSON_ERROR);
        }
        try {
            ApiDocBuilder.buildApiDoc(apiConfig);
            CommonConstant.showMessage("build success ,file path :  "
                    + ApiConfig.getInstance().getOutPath());
        } catch (Exception ex) {
        } catch (Exception ex) {
            CommonConstant.showErrorMessage(ex.getMessage());
        }
    }
}
