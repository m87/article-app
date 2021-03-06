package com.consdata.article;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

public class DocGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setVisible(true);
        event.getPresentation().setEnabled(ofNullable(CommonDataKeys.PSI_FILE.getData(event.getDataContext()))
                .map(this::hasDocAnnotation)
                .orElse(false));
    }

    private boolean hasDocAnnotation(PsiFile psiFile) {
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            return Arrays.stream(javaFile.getClasses())
                    .anyMatch(psiClass -> psiClass.hasAnnotation("com.consdata.doc.Doc")
                            || Arrays.stream(psiClass.getFields()).anyMatch(field -> field.hasAnnotation("com.consdata.doc.Doc"))
                            || Arrays.stream(psiClass.getAllMethods()).anyMatch(method -> method.hasAnnotation("com.consdata.doc.Doc")));
        }
        return false;
    }
}
