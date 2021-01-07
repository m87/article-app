package com.consdata.article;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.documentation.ExternalDocumentationProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DocProvider implements DocumentationProvider, ExternalDocumentationProvider {
    @Nullable
    @Override
    public String fetchExternalDocumentation(Project project, PsiElement psiElement, List<String> list) {
        return getDoc((PsiClass) psiElement);
    }

    @Override
    public boolean hasDocumentationFor(PsiElement psiElement, PsiElement psiElement1) {
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;
            return psiClass.hasAnnotation("com.consdata.doc.Doc")
                    || Arrays.stream(psiClass.getFields()).anyMatch(field -> field.hasAnnotation("com.consdata.doc.Doc"))
                    || Arrays.stream(psiClass.getMethods()).anyMatch(field -> field.hasAnnotation("com.consdata.doc.Doc"));
        }
        return false;
    }

    @Override
    public boolean canPromptToConfigureDocumentation(PsiElement psiElement) {
        return false;
    }

    @Override
    public void promptToConfigureDocumentation(PsiElement psiElement) {
    }

    private String getDoc(PsiClass psiClass) {
        Map<String, String> doc = new HashMap<>();
        doc.put(psiClass.getName(), evaulate(psiClass.getProject(), psiClass.getAnnotation("com.consdata.doc.Doc").findAttributeValue("value")));

        doc.putAll(Arrays.stream(psiClass.getFields())
                .filter(psiField -> psiField.hasAnnotation("com.consdata.doc.Doc"))
                .collect(Collectors.toMap(PsiField::getName, psiField -> evaulate(psiField.getProject(), psiField.getAnnotation("com.consdata.doc.Doc").findAttributeValue("value")))));

        doc.putAll(Arrays.stream(psiClass.getMethods())
                .filter(psiMethod -> psiMethod.hasAnnotation("com.consdata.doc.Doc"))
                .collect(Collectors.toMap(PsiMethod::getName, psiMethod -> evaulate(psiMethod.getProject(), psiMethod.getAnnotation("com.consdata.doc.Doc").findAttributeValue("value")))));

        return doc.entrySet().stream().map(e -> "<b>" + e.getKey() + "</b>: " + e.getValue()).collect(Collectors.joining("<br/>"));

    }

    private String evaulate(Project project, PsiAnnotationMemberValue expression) {
        return JavaPsiFacade.getInstance(project).getConstantEvaluationHelper().computeConstantExpression(expression).toString();
    }


    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return Collections.singletonList("");
    }
}
