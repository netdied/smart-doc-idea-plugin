package com.ly.smartdoc.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.SourceCodePath;
import com.ly.smartdoc.common.CommonConstant;
import com.ly.smartdoc.common.ErrorCoedEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ConfigUtil {
    public static void buildConfig(String json) {
        if (checkJson(json)) {
            ApiConfig apiConfig = new Gson().fromJson(json, ApiConfig.class);
            // 获取当前项目路径
            Project[] project = ProjectManager.getInstance().getOpenProjects();
            if (project.length > 0) {
                apiConfig.setClassLoader(ConfigUtil.class.getClassLoader());
                String basePath = project[0].getBasePath();
                if (StringUtils.isBlank(apiConfig.getBaseDir())) {
                    apiConfig.setBaseDir(basePath);
                }
                if (StringUtils.isBlank(apiConfig.getCodePath())) {
                    apiConfig.setCodePath(basePath);
                }
                if (CollectionUtils.isEmpty(apiConfig.getSourceCodePaths())) {
                    apiConfig.setSourceCodePaths(SourceCodePath.builder().setPath(basePath));
                } else {
                    apiConfig.getSourceCodePaths().add(new SourceCodePath().setPath(basePath));
                }
            } else {
                CommonConstant.showErrorMessage(ErrorCoedEnum.PROJECT_ERROR);
            }
            CommonConstant.setApiConfig(apiConfig);
        }
    }

    private static boolean checkJson(String str) {
        if (StringUtils.isBlank(str)) {
            CommonConstant.showErrorMessage(ErrorCoedEnum.JSON_ERROR);
            return false;
        }
        JsonElement jsonElement;
        try {
            jsonElement = JsonParser.parseString(str);
        } catch (Exception e) {

            return false;
        }
        return jsonElement.isJsonObject();
    }


    public static ClassLoader getModuleClassLoader(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
                return createClassLoaderForModule(module);
        }
        return null;
    }

    private static ClassLoader createClassLoaderForModule(Module module) {
        try {
            String[] outputDir = ModuleRootManager.getInstance(module).getContentRootUrls();
            if (outputDir != null) {
                System.out.println(outputDir);
//                URL url = new URL("file://" + outputDir.getPath());
//                return new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
