package com.ly.smartdoc.action.single;

import com.ly.doc.builder.ApiDataBuilder;
import com.ly.doc.model.ApiAllData;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.action.AbstractBuildAction;

public class RequestAction extends AbstractBuildAction {


    @Override
    protected String getTaskTitle() {
        return "build Request";
    }

    @Override
    protected void execute(ApiConfig apiConfig) {
        ApiAllData apiAllData = ApiDataBuilder.getApiData(apiConfig);
        apiAllData.getApiDocList().forEach(s->s.getList().forEach(m->m.getMethodId()));
    }
}
