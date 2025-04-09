package com.ly.smartdoc.action;

import com.ly.doc.model.ApiConfig;

public interface BuildStrategy {

    void execute(ApiConfig apiConfig);
}
