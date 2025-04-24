package com.ly.smartdoc.action.single;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.DocJavaMethod;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.utils.JavaClassUtil;
import com.ly.doc.utils.JsonUtil;
import com.ly.smartdoc.common.CommonConstant;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractSingleAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getProject(e);
        Editor editor = getEditor(e);

        if (project == null || editor == null) {
            showError("No project or editor available.");
            return;
        }

        PsiFile psiFile = getPsiFile(project, editor);
        if (psiFile == null) {
            showError("No project file.");
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = getElementAtOffset(psiFile, offset);
        if (elementAtCaret == null) {
            showError("No element found at caret position.");
            return;
        }

        PsiMethod method = getParentMethod(elementAtCaret);
        if (method == null) {
            showError("No method found at caret position.");
            return;
        }

        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            showError("No class found for the method.");
            return;
        }

        String qualifiedClassName = containingClass.getQualifiedName();
        if (qualifiedClassName == null) {
            showError("Qualified class name is not available.");
            return;
        }
        new Task.Backgroundable(project, getTaskTitle(), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setText(getTaskTitle() + "...");
                    execute(method, qualifiedClassName);
                    indicator.setText(getTaskTitle() + " completed successfully.");
                } catch (Exception ex) {
                    CommonConstant.showErrorMessage(ex.getMessage());
                }
            }

            @Override
            public void onSuccess() {
                //nothing
            }

            @Override
            public void onCancel() {
                CommonConstant.showMessage(CommonConstant.BUILD_CANCEL);
            }
        }.queue();


    }

    protected abstract void execute(PsiMethod method, String qualifiedClassName);

    protected abstract String getTaskTitle();


    private Project getProject(AnActionEvent e) {
        return e.getDataContext().getData(CommonDataKeys.PROJECT);
    }

    private Editor getEditor(AnActionEvent e) {
        return e.getDataContext().getData(CommonDataKeys.EDITOR);
    }

    private PsiFile getPsiFile(Project project, Editor editor) {
        return ReadAction.compute(() -> PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument()));
    }

    private PsiElement getElementAtOffset(PsiFile psiFile, int offset) {
        return ReadAction.compute(() -> psiFile.findElementAt(offset));
    }

    private PsiMethod getParentMethod(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    }

    private void showError(String message) {
        CommonConstant.showErrorMessage(message);
    }


    public String getMethodTypeParamsString(JvmParameter[] typeParameters) {
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
