package com.yangyang5214.sts.actions;

import com.goide.psi.*;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.yangyang5214.sts.model.Field;
import org.eclipse.sisu.space.SpaceScanner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (getParamVarName(signature) == null) {
            alterMsg("Need declare param var. \n\n Example: func test(certInfo *CertInfo) (output *SiteInfo)");
            return;
        }

        GoStructType paramType = getParam(signature);
        if (paramType == null) {
            alterMsg("param type is not a pointer type");
            return;
        }


        GoStructType returnType = getReturn(signature);
        if (returnType == null) {
            alterMsg("return type is not a pointer type");
            return;
        }

        String result = genResult(signature, paramType, returnType);

//        alterMsg(result); // alert debug
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                int insertLine = line;
                if (Strings.startsWith(currentLineText, 0, "func")) {
                    insertLine = insertLine + 1;
                }
                doc.insertString(doc.getLineStartOffset(insertLine), result);
            }
        });

        autoFormat(e);
    }


    /**
     * https://stackoverflow.com/questions/28294413/how-to-programmatically-use-intellij-idea-code-formatter
     */
    public void autoFormat(@NotNull AnActionEvent e) {
        CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
        PsiElement psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        styleManager.reformat(psiFile);
    }


    /**
     * Reference: <a href="https://stackoverflow.com/questions/76561470/how-to-get-gostructtype-object-by-gofunctiondeclaration-idea-plugin-for-goland">...</a>
     */
    public GoStructType getParam(GoSignature signature) {
        for (GoParamDefinition paramDef : signature.getParameters().getDefinitionList()) {
            GoType paramType = paramDef.getGoType(null);
            GoTypeSpec goTypeSpec = GoTypeUtil.findTypeSpec(paramType, signature, true);
            if (goTypeSpec == null) {
                return null;
            }
            return (GoStructType) goTypeSpec.getSpecType().getType();
        }
        return null;
    }

    public GoStructType getReturn(GoSignature signature) {
        GoResult result = signature.getResult();
        if (result == null) {
            return null;
        }
        GoParameters parameters = result.getParameters();
        GoType type;
        if (parameters != null) {
            type = parameters.getTypeByIndex(0);
        } else {
            type = result.getType();
        }
        if (type == null) {
            return null;
        }
        return (GoStructType) GoTypeUtil.unwrapPointerAndParTypes(type).getUnderlyingType(signature);
    }


    public void alterMsg(String msg) {
        Messages.showMessageDialog(project, msg, "Test", Messages.getInformationIcon());
    }


    public String getReturnType(GoSignature signature) {
        GoParameters parameters = signature.getResult().getParameters();
        String text;
        if (parameters != null) {
            text = parameters.getTypeByIndex(0).getText();
        } else {
            text = signature.getResult().getText();
        }
        return text.substring(1);
    }


    /**
     * getParamVarName
     * <p>
     * func test(certInfo *CertInfo) (output *SiteInfo) {
     * //need certInfo var name
     * }
     *
     * @param signature
     * @return certInfo
     */
    public String getParamVarName(GoSignature signature) {
        String text = signature.getParameters().getParameterDeclarationList().get(0).getText();
        String[] arrs = text.split(" ");
        if (arrs.length == 1) {
            return null;
        }
        return arrs[0];
    }

    public String genResult(GoSignature signature, GoStructType paramType, GoStructType returnType) {
        StringBuilder builder = new StringBuilder();

        //for return
        builder.append(wrapString("return &", 1)).append(getReturnType(signature)).append("{");
        builder.append(wrapString("\n", 2));

        List<Field> returnFields = getAllField(returnType);
        List<Field> paramFields = getAllField(paramType);

        Map<String, String> fieldMap = genMap(paramFields, returnFields);

        String paramVal = getParamVarName(signature);

        List<Field> notMatchs = new ArrayList<>();

        for (Field f : returnFields) {
            String returnField = f.getName();
            if (!fieldMap.containsKey(returnField)) {
                notMatchs.add(f);
                continue;
            }
            builder.append(wrapString(returnField, 2));
            builder.append(wrapString(":", 0));

            //map field
            builder.append(paramVal);
            builder.append(".");
            builder.append(fieldMap.get(returnField));
            builder.append(wrapString(",", 0));
            builder.append(wrapString("\n", 2));
        }

        //set default values
        for (Field f : notMatchs) {
            builder.append(wrapString(f.getName(), 2));
            builder.append(wrapString(":", 0));
            // default value

            builder.append(wrapString(genDefaultVal(f.getType()), 0));
            builder.append(wrapString(",", 0));
            builder.append(wrapString("\n", 2));
        }

        //for last line
        builder.append(wrapString("}", 1));
        return builder.toString();
    }


    public String genDefaultVal(String type) {
        if (type.startsWith("int")) {
            return "0";
        } else if (type.equals("string")) {
            return "\"\"";
        } else if (type.equals("bool")) {
            return "false";
        }
        return "nil";
    }

    /**
     * gen map<returnField, paramField>
     * <p>
     *
     * @param paramFields
     * @param returnFields
     * @return
     */
    public Map<String, String> genMap(List<Field> paramFields, List<Field> returnFields) {
        Map<String, String> resultMap = new HashMap<>();
        for (Field f : paramFields) {
            String paramField = f.getName();
            String matchingReturnField = findMatchingReturnField(paramField, returnFields);
            if (matchingReturnField != null) {
                resultMap.put(paramField, matchingReturnField);
            }
        }
        return resultMap;
    }

    private String trim(String str) {
        str = str.replace("_", ""); //eg: CertSha1 <-> CertSha_1
        return str;
    }

    private String findMatchingReturnField(String paramField, List<Field> returnFields) {
        for (Field f : returnFields) {
            String returnField = f.getName();
            if (trim(paramField).equalsIgnoreCase(trim(returnField))) {
                return returnField;
            }
        }
        return null;
    }


    public List<Field> getAllField(GoStructType structType) {
        List<Field> fieldList = new ArrayList<>();
        List<GoFieldDeclaration> fieldDeclarations = structType.getFieldDeclarationList();
        for (int i = 0; i < fieldDeclarations.size(); i++) {
            GoFieldDeclaration declaration = fieldDeclarations.get(i);

            String text = declaration.getText();

            if (text.contains("protoimpl.")) {
                continue; // skip proto base fields
            }

            String[] arrs = text.split(" ");
            if (arrs.length <= 1) {
                continue; //  skip extends struct
            }

            fieldList.add(new Field(arrs[0], arrs[1]));
        }
        return fieldList;
    }


    public String wrapString(String str, int countTab) {
        StringBuilder builder = new StringBuilder();
        builder.append("    ".repeat(Math.max(0, countTab)));
        builder.append(str);
        return builder.toString();
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