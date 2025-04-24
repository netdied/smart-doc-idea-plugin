package com.ly.smartdoc.action.single;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.helper.JsonBuildHelper;
import com.ly.doc.model.DocJavaMethod;
import com.ly.smartdoc.common.CommonConstant;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReturnAction extends AbstractSingleAction {

    @Override
    public void execute(PsiMethod method, String qualifiedClassName) {
        // 获取方法名和参数签名
        String methodName = method.getName();
        String methodTypeParamsString = getMethodTypeParamsString(method.getParameterList().getParameters());
        String fullMethodPath = methodName + methodTypeParamsString;

        // 检查返回类型
        PsiType returnType = method.getReturnType();
        if (returnType == null) {
            CommonConstant.showErrorMessage("Return Type: void");
            return;
        }
        // 初始化项目构建器和配置构建器
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(CommonConstant.getApiConfig(), javaProjectBuilder);

        // 获取 JavaClass 对象
        JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(qualifiedClassName);
        if (javaClass == null) {
            CommonConstant.showErrorMessage("Class not found in the project builder.");
            return;
        }
        // 转换方法列表并查找匹配的方法
        DocJavaMethod javaMethod = javaClass.getMethods().stream()
                .map(s1 -> convertToDocJavaMethod(CommonConstant.getApiConfig(), configBuilder, s1, null))
                .filter(m -> m.getJavaMethod().getCallSignature().equals(fullMethodPath))
                .findFirst().orElse(null);

        if (Objects.nonNull(javaMethod)) {
            // 构建 JSON 并显示
            String json = JsonBuildHelper.buildReturnJson(javaMethod, configBuilder);
            CommonConstant.showMessage(json);
        } else {
            CommonConstant.showErrorMessage("No matching method found in the class.");
        }
    }

    @Override
    protected String getTaskTitle() {
        return "build Return";
    }

}
