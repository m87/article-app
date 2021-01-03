package com.consdata.article;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Optional;

public class DocListener implements DocSaved {

    private final Project project;

    public DocListener(Project project) {
        this.project = project;
    }

    @Override
    public void onSave(VirtualFile virtualFile) {
        Optional.ofNullable(virtualFile).ifPresent(vFile -> new OpenFileDescriptor(project, vFile).navigate(true));
    }
}