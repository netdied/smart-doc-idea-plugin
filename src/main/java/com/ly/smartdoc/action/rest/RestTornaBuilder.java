package com.ly.smartdoc.action.rest;

import com.ly.doc.builder.TornaBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.action.AbstractBuildAction;
import com.ly.smartdoc.common.CommonConstant;


public class RestTornaBuilder extends AbstractBuildAction {


    @Override
    protected String getTaskTitle() {
        return CommonConstant.BUILD_TORNA;
    }

    @Override
    public void execute(ApiConfig apiConfig) {
        TornaBuilder.buildApiDoc(apiConfig);
    }
}
