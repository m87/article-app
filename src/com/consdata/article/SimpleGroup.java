package com.consdata.article;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;

public class SimpleGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(true);
        event.getPresentation().setVisible(true);
    }
}
