package com.ly.smartdoc.action.single;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.helper.JsonBuildHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.DocJavaMethod;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.utils.JavaClassUtil;
import com.ly.doc.utils.JsonUtil;
import com.ly.smartdoc.common.CommonConstant;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReturnAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getDataContext().getData(CommonDataKeys.PROJECT);
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            CommonConstant.showErrorMessage("no project");
            return;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            CommonConstant.showErrorMessage("no project file");
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        if (elementAtCaret == null) {
            CommonConstant.showErrorMessage("no project file elementAtCaret1");
            return;
        }
        PsiMethod method = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);
        if (method == null) {
            CommonConstant.showErrorMessage("No method found at caret position.");
            return;
        }
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            CommonConstant.showErrorMessage("No class found for the method.");
            return;
        }
        String qualifiedClassName = containingClass.getQualifiedName();
        if (qualifiedClassName == null) {
            CommonConstant.showErrorMessage("Qualified class name is not available.");
            return;
        }
        String methodName = method.getName();
        String methodTypeParamsString = getMethodTypeParamsString(method.getParameterList().getParameters());
        String fullMethodPath = methodName + methodTypeParamsString;
        PsiType returnType = method.getReturnType();
        if (returnType == null) {
            CommonConstant.showErrorMessage("Return Type: void");
            return;
        }
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(CommonConstant.getApiConfig(), javaProjectBuilder);
        JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(qualifiedClassName);
        List<DocJavaMethod> javaMethods = javaClass.getMethods().stream()
                .map(s1 -> convertToDocJavaMethod(CommonConstant.getApiConfig(), configBuilder, s1, null))
                .collect(Collectors.toList());
        for (DocJavaMethod javaMethod : javaMethods) {
            if (javaMethod.getJavaMethod().getCallSignature().equals(fullMethodPath)) {
                String json = JsonBuildHelper.buildReturnJson(javaMethod, configBuilder);
                CommonConstant.showMessage(json);
            }
        }
    }

    private String getMethodTypeParamsString(JvmParameter[] typeParameters) {
        if (typeParameters.length == 0) {
            return "()";
        }
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < typeParameters.length; i++) {
            sb.append(typeParameters[i].getName());
            if (i < typeParameters.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public DocJavaMethod convertToDocJavaMethod(ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder,
                                                 JavaMethod method, Map<String, JavaType> actualTypesMap) {
        JavaClass cls = method.getDeclaringClass();
        String clzName = cls.getCanonicalName();
        if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
            throw new RuntimeException(
                    "Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
        }
        String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR, Boolean.TRUE);
        DocJavaMethod docJavaMethod = DocJavaMethod.builder().setJavaMethod(method).setActualTypesMap(actualTypesMap);
        if (Objects.nonNull(method.getTagByName(DocTags.DOWNLOAD))) {
            docJavaMethod.setDownload(true);
        }
        DocletTag pageTag = method.getTagByName(DocTags.PAGE);
        if (Objects.nonNull(method.getTagByName(DocTags.PAGE))) {
            String pageUrl = projectBuilder.getServerUrl() + DocGlobalConstants.PATH_DELIMITER + pageTag.getValue();
            docJavaMethod.setPage(UrlUtil.simplifyUrl(pageUrl));
        }

        DocletTag docletTag = method.getTagByName(DocTags.GROUP);
        if (Objects.nonNull(docletTag)) {
            docJavaMethod.setGroup(docletTag.getValue());
        }
        docJavaMethod.setParamTagMap(DocUtil.getCommentsByTag(method, DocTags.PARAM, clzName));
        docJavaMethod.setParamsComments(DocUtil.getCommentsByTag(method, DocTags.PARAM, null));

        Map<String, String> authorMap = DocUtil.getCommentsByTag(method, DocTags.AUTHOR, cls.getName());
        String authorValue = String.join(", ", new ArrayList<>(authorMap.keySet()));
        if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
            docJavaMethod.setAuthor(JsonUtil.toPrettyFormat(authorValue));
        }
        if (apiConfig.isShowAuthor() && StringUtil.isEmpty(authorValue)) {
            docJavaMethod.setAuthor(classAuthor);
        }

        String comment = DocUtil.getEscapeAndCleanComment(method.getComment());
        docJavaMethod.setDesc(comment);
        String version = DocUtil.getNormalTagComments(method, DocTags.SINCE, cls.getName());
        docJavaMethod.setVersion(version);

        String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
        if (StringUtil.isEmpty(apiNoteValue)) {
            apiNoteValue = method.getComment();
        }
        docJavaMethod.setDetail(apiNoteValue != null ? apiNoteValue : "");

        // set jsonViewClasses
        method.getAnnotations()
                .stream()
                .filter(annotation -> DocAnnotationConstants.SHORT_JSON_VIEW.equals(annotation.getType().getValue()))
                .findFirst()
                .ifPresent(annotation -> docJavaMethod
                        .setJsonViewClasses(JavaClassUtil.getJsonViewClasses(annotation, projectBuilder, false)));

        return docJavaMethod;
    }
}
