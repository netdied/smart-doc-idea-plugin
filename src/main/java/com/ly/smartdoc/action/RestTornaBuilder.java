package com.ly.smartdoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.ly.doc.builder.TornaBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;

import java.util.Objects;


public class RestTornaBuilder extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        ApiConfig apiConfig = CommonConstant.getApiConfig();
        if (Objects.isNull(apiConfig)) {
            CommonConstant.showErrorMessage(ErrorCoedEnum.JSON_ERROR);
        }
        try {
            TornaBuilder.buildApiDoc(apiConfig);
            CommonConstant.showMessage("build success");
        } catch (Exception ex) {
            CommonConstant.showErrorMessage(ex.getMessage());
        }
    }
}
