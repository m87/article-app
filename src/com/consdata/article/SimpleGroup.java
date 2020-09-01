package com.consdata.article;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

public class SimpleGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(true);
        event.getPresentation().setVisible(ofNullable(CommonDataKeys.PSI_FILE.getData(event.getDataContext()))
                .map(this::implementsServiceAnnotation)
                .orElse(false));
    }

    private boolean implementsServiceAnnotation(PsiFile psiFile) {
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            return Arrays.stream(javaFile.getClasses()).filter(psiClass -> psiClass.hasModifier(JvmModifier.PUBLIC)).findFirst()
                    .map(psiClass -> psiClass.hasAnnotation("annotation.Service"))
                    .orElse(false);
        }
        return false;
    }
}
