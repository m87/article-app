package com.consdata.article;

import com.intellij.openapi.vfs.VirtualFile;

public interface DocSaved {
    void onSave(VirtualFile virtualFile);
}
