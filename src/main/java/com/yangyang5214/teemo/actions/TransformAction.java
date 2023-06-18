package com.yangyang5214.teemo.actions;

import com.goide.psi.*;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransformAction extends AnAction {

    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getProject();
        if (project == null) {
            return;
        }
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        Document doc = editor.getDocument();

        int offset = getCurrentOffset(editor);

        int line = doc.getLineNumber(offset);
        TextRange textRange = new TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line));
        String currentLineText = doc.getText(textRange);

        GoFile psiFile = (GoFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        PsiElement psiElement = PsiUtilCore.getElementAtOffset(psiFile, offset);

        GoFunctionDeclaration func = PsiTreeUtil.getParentOfType(psiElement, GoFunctionDeclaration.class);
        if (func == null) {
            alterMsg("No func found");
            return;
        }

        GoSignature signature = func.getSignature();
        if (signature == null) {
            return;
        }

        GoType paramType = getParam(signature);
        if (paramType == null) {
            alterMsg("param type is missing");
            return;
        }

        GoType returnType = getReturn(signature);
        if (returnType == null) {
            alterMsg("return type is missing");
            return;
        }

        String result = genResult();
        alterMsg(result);
    }


    public GoType getParam(GoSignature signature) {
        GoParameters parameters = signature.getParameters();
        if (parameters.getParameterDeclarationList().size() != 1) {
            alterMsg("params length is not equals 1");
            return null;
        }
        GoParameterDeclaration parameter = parameters.getParameterDeclarationList().get(0);
        return parameter.getType();
    }

    public GoType getReturn(GoSignature signature) {
        GoResult result = signature.getResult();
        if (result == null) {
            return null;
        }
        GoType returnType = result.getType(); //single return
        if (result.getParameters() != null) {
            returnType = result.getParameters().getTypeByIndex(0); // multi return .  use first
        }
        return returnType;
    }


    public void alterMsg(String msg) {
        Messages.showMessageDialog(project, msg, "Test", Messages.getInformationIcon());
    }


    public String genResult() {
        return "";
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
}