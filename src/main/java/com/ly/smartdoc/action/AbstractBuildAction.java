package com.ly.smartdoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.ly.doc.model.ApiConfig;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public abstract class AbstractBuildAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            CommonConstant.showErrorMessage("Project not found.");
            return;
        }

        ApiConfig apiConfig = CommonConstant.getApiConfig();
        if (Objects.isNull(apiConfig)) {
            CommonConstant.showErrorMessage(ErrorCoedEnum.JSON_ERROR.getMessage());
            return;
        }

        new Task.Backgroundable(project, getTaskTitle(), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setText(getTaskTitle() + "...");
                    execute(apiConfig);
                    indicator.setText(getTaskTitle() + " completed successfully.");
                } catch (Exception ex) {
                    CommonConstant.showErrorMessage(ex.getMessage());
                }
            }

            @Override
            public void onSuccess() {
//                nothing
            }

            @Override
            public void onCancel() {
                CommonConstant.showMessage(CommonConstant.BUILD_CANCEL);
            }
        }.queue();
    }

    protected abstract String getTaskTitle();

    protected abstract void execute(ApiConfig apiConfig);


}
