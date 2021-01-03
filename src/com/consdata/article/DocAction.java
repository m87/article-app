package com.consdata.article;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.psi.*;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class DocAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String doc = ofNullable(CommonDataKeys.PSI_FILE.getData(event.getDataContext()))
                .map(this::getDoc)
                .orElse("");
        try {
            save(doc, event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(String doc, AnActionEvent event) throws IOException {
        final FileSaverDialog dialog = FileChooserFactory.getInstance().createSaveFileDialog(
                new FileSaverDescriptor("Generate to", "", "txt"), event.getProject());
        VirtualFileWrapper wrapper = dialog.save(event.getProject().getProjectFile(), "");
        VirtualFile vFile = wrapper.getVirtualFile(true);
        vFile.setCharset(StandardCharsets.UTF_8);
        vFile.setBinaryContent(doc.getBytes());

        event.getProject().getMessageBus().syncPublisher(Topic.create("com.consdata.article.DocSaved", DocSaved.class)).onSave(vFile);
    }


    private String getDoc(PsiFile psiFile) {
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;

            Map<String, String> doc = Arrays.stream(javaFile.getClasses())
                    .filter(psiClass -> psiClass.hasAnnotation("com.consdata.doc.Doc"))
                    .collect(Collectors.toMap(PsiClass::getName, psiClass -> evaulate(psiClass.getProject(), psiClass.getAnnotation("com.consdata.doc.Doc").findAttributeValue("value"))));

            doc.putAll(Arrays.stream(javaFile.getClasses())
                    .map(PsiClass::getFields)
                    .flatMap(Arrays::stream)
                    .filter(psiField -> psiField.hasAnnotation("com.consdata.doc.Doc"))
                    .collect(Collectors.toMap(PsiField::getName, psiField -> evaulate(psiField.getProject(), psiField.getAnnotation("com.consdata.doc.Doc").findAttributeValue("value")))));

            doc.putAll(Arrays.stream(javaFile.getClasses())
                    .map(PsiClass::getMethods)
                    .flatMap(Arrays::stream)
                    .filter(psiMethod -> psiMethod.hasAnnotation("com.consdata.doc.Doc"))
                    .collect(Collectors.toMap(PsiMethod::getName, psiMethod -> evaulate(psiMethod.getProject(), psiMethod.getAnnotation("com.consdata.doc.Doc").findAttributeValue("value")))));

            return String.join("\n", doc.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.toList()));
        } else {
            return "";
        }
    }

    private String evaulate(Project project, PsiAnnotationMemberValue expression) {
        return JavaPsiFacade.getInstance(project).getConstantEvaluationHelper().computeConstantExpression(expression).toString();
    }

}
