package com.yangyang5214.teemo.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class TransformAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        String dlgMsg = event.getPresentation().getText() + " Selected!";
        Messages.showMessageDialog(project, dlgMsg, "Test", Messages.getInformationIcon());
    }

}
