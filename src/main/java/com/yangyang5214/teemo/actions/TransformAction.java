package com.yangyang5214.teemo.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TransformAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Document doc = editor.getDocument();

        String currentLine = getCurrentLine(editor, doc);

        String result = genResult();
        result = currentLine; //todo

        String finalResult = result;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            insertString(editor, doc, finalResult);
        });
    }


    public String genResult() {
        //todo
        return "";
    }

    public String getCurrentLine(Editor editor, Document doc) {
        int line = doc.getLineNumber(getCurrentOffset(editor));
        TextRange textRange = new TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line));
        return doc.getText(textRange);
    }


    /**
     * <a href="https://plugins.jetbrains.com/docs/intellij/coordinates-system.html#caret-visual-position">...</a>
     *
     * @param editor
     * @return offset
     */
    public int getCurrentOffset(Editor editor) {
        CaretModel caretModel = editor.getCaretModel();
        Caret primaryCaret = caretModel.getPrimaryCaret();
        return primaryCaret.getOffset();
    }


    /**
     * insert test
     *
     * @param editor
     * @param doc
     * @param result
     */
    public void insertString(Editor editor, Document doc, String result) {
        doc.insertString(getCurrentOffset(editor), result);
    }
}