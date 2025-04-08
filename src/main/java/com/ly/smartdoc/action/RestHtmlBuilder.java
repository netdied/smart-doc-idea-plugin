package com.ly.smartdoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
<<<<<<< HEAD
import com.ly.doc.builder.HtmlApiDocBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;

=======
import com.ly.doc.builder.DocBuilderTemplate;
import com.ly.doc.builder.HtmlApiDocBuilder;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.template.IDocBuildTemplate;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;
>>>>>>> 164b6e8776a5f658649b474a3b393a72b8c046b1
import java.util.Objects;


public class RestHtmlBuilder extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        ApiConfig apiConfig = CommonConstant.getApiConfig();
        if (Objects.isNull(apiConfig)) {
            CommonConstant.showErrorMessage(ErrorCoedEnum.JSON_ERROR);
        }
        try {
            HtmlApiDocBuilder.buildApiDoc(apiConfig);
<<<<<<< HEAD
            CommonConstant.showMessage("build success ,file path :  "
                    + ApiConfig.getInstance().getOutPath());
=======
>>>>>>> 164b6e8776a5f658649b474a3b393a72b8c046b1
        } catch (Exception ex) {
            CommonConstant.showErrorMessage(ex.getMessage());
        }
    }
<<<<<<< HEAD
=======

    public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, Boolean.TRUE);
        config.setParamsDataToTree(false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
                config.getClassLoader());
        Objects.requireNonNull(docBuildTemplate, "doc build template is null");
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder).getApiDatas();
        builderTemplate.copyJQueryAndCss(config);
        if (config.isAllInOne()) {
        }


    }
>>>>>>> 164b6e8776a5f658649b474a3b393a72b8c046b1
}
