package com.ly.smartdoc.action.rest;

import com.ly.doc.builder.PostmanJsonBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.action.AbstractBuildAction;
import com.ly.smartdoc.common.CommonConstant;


public class RestPostManBuilder extends AbstractBuildAction {

    @Override
    protected String getTaskTitle() {
        return CommonConstant.BUILD_POSTMAN;
    }

    @Override
    public void execute(ApiConfig apiConfig) {
        PostmanJsonBuilder.buildPostmanCollection(apiConfig);
    }
}
