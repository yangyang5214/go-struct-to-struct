package com.yangyang5214.teemo.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TransformAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Document doc = e.getRequiredData(CommonDataKeys.EDITOR).getDocument();
        WriteCommandAction.runWriteCommandAction(project, () ->
                doc.setText("beer test")
        );
    }
}
