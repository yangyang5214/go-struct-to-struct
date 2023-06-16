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

        int line = doc.getLineNumber(getCurrentOffset(editor));
        TextRange textRange = new TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line));
        String currentLineText = doc.getText(textRange);

        int startOffset = doc.getLineStartOffset(line);

        List<Integer> indexList = new ArrayList<>();
        for (int i = 1; i < currentLineText.length() - 1; i++) {
            if (currentLineText.charAt(i) == ')') {
                indexList.add(startOffset + i - 1);
            }

        }
        if (indexList.size() != 2) {
            return;
        }

        GoFile psiFile = (GoFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        for (Integer index : indexList) {
            PsiElement psiElement = psiFile.findElementAt(index);
            if (psiElement == null) {
                alterMsg("can't find  struct");
                return;
            }

            alterMsg(psiElement.getText());

            GoTypeDeclaration declaration = PsiTreeUtil.getContextOfType(psiElement, GoTypeDeclaration.class);
            if (declaration == null) {
                alterMsg("can't find  struct declaration");
                return;
            }
            GoTypeSpec goTypeSpec = PsiTreeUtil.getChildOfType(declaration, GoTypeSpec.class);
            if (goTypeSpec == null) {
                alterMsg("can't find  struct Spec");
                return;
            }

            GoStructType structType = PsiTreeUtil.getParentOfType(goTypeSpec, GoStructType.class);
            if (structType == null) {
                alterMsg("can't find  struct Type");
                return;
            }

            alterMsg(structType.toString());

            List<GoFieldDeclaration> fieldDeclarations = structType.getFieldDeclarationList();
            for (GoFieldDeclaration fieldDeclaration : fieldDeclarations) {
                List<GoFieldDefinition> fieldDefinitions = fieldDeclaration.getFieldDefinitionList();
                for (GoFieldDefinition fieldDefinition : fieldDefinitions) {
                    String attributeName = fieldDefinition.getName();
                    alterMsg(attributeName);
                }
            }
        }
    }


//        if (psiMethod == null) {
//            return;
//        }
//
//        PsiParameterList parameterList = psiMethod.getParameterList();
//        PsiParameter[] parameters = parameterList.getParameters();
//        if (parameters.length == 0) {
//            return;
//        }
//
//        PsiType parameterType = parameters[0].getType();
//        if (!(parameterType instanceof PsiClassType)) {
//            return;
//        }
//
//        PsiClassType classType = (PsiClassType) parameterType;
//        PsiClass parameterClass = classType.resolve();
//        if (parameterClass == null || !parameterClass.isRecord()) {
//            return;
//        }
//
//        String parameterClassName = parameterClass.getName();
//
//        // Generate the return struct code
//        StringBuilder structCode = new StringBuilder("type ReturnValue struct {\n");
//        structCode.append("\t").append(parameterClassName).append("\n");
//        structCode.append("}");
//
//
//        String result = structCode.toString();
//        doc.insertString(offset, result);
//
//        alterMsg(project, "111");


    public void alterMsg(String msg) {
        Messages.showMessageDialog(project, msg, "Test", Messages.getInformationIcon());
    }


    public String genResult() {
        //todo
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

    private List<String> getStructProperties(GoType goType) {
        List<String> properties = new ArrayList<>();

        if (goType instanceof GoPointerType pointerType) {
            goType = pointerType.getType();
        }

        if (goType instanceof GoStructType structType) {
            List<GoFieldDeclaration> fields = structType.getFieldDeclarationList();
            for (GoFieldDeclaration field : fields) {
                List<GoFieldDefinition> fieldDefinitions = field.getFieldDefinitionList();
                for (GoFieldDefinition fieldDefinition : fieldDefinitions) {
                    properties.add(fieldDefinition.getName());
                }
            }
        }
        return properties;
    }
}