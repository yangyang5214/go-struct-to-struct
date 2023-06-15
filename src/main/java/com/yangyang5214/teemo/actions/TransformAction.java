package com.yangyang5214.teemo.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TransformAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Document doc = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Get caretOffset
            // https://plugins.jetbrains.com/docs/intellij/coordinates-system.html#caret-visual-position
            CaretModel caretModel = editor.getCaretModel();
            Caret primaryCaret = caretModel.getPrimaryCaret();
            int caretOffset = primaryCaret.getOffset();

            //insert
            doc.insertString(caretOffset, "test");
        });
    }
}