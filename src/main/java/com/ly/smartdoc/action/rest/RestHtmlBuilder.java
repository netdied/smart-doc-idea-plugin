package com.ly.smartdoc.action.rest;

import com.ly.doc.builder.HtmlApiDocBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.action.AbstractBuildAction;
import com.ly.smartdoc.common.CommonConstant;


public class RestHtmlBuilder extends AbstractBuildAction {

    @Override
    public void execute( ApiConfig apiConfig) {
        HtmlApiDocBuilder.buildApiDoc(apiConfig);
    }

    @Override
    protected String getTaskTitle() {
        return CommonConstant.BUILD_HTML;
    }
}