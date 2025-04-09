package com.ly.smartdoc.action.rest;

import com.ly.doc.builder.openapi.OpenApiBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.action.AbstractBuildAction;
import com.ly.smartdoc.common.CommonConstant;

public class RestOpenApiBuilder extends AbstractBuildAction {

    @Override
    protected String getTaskTitle() {
        return CommonConstant.BUILD_OPENAPI;
    }

    @Override
    public void execute(ApiConfig apiConfig) {
        OpenApiBuilder.buildOpenApi(apiConfig);
    }
}
