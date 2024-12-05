package com.dataour.bifrost.code.generator.impl;

import com.dataour.bifrost.code.generator.CodeGenerator;

import static com.dataour.bifrost.common.file.FileUtils.readContent;
import static java.io.File.separator;

public class ModuleGenerator extends CodeGenerator {
    private static final String filePath = separator + "template" + separator + "ModuleNameSearchReq.tmp";

    @Override
    public void genCode(Class targetClass, String menuPath) {
        genModuleNameSearchReq(targetClass, menuPath);
    }

    private void genModuleNameSearchReq(Class targetClass, String menuPath) {
        String codeTemplate = readContent(getCodeAbsolutePath() + filePath);
        String targetClassName = getTargetClassName(targetClass);
        String targetModulePath = separator + properties.getProperty("targetModulePackage").replaceAll("\\.", separator);
        String fileFullName = getJavaAbsolutePath() + targetModulePath + separator + "request" + separator + "search" + separator + targetClassName + "SearchReq.java";
        genCodeByTemplate(codeTemplate, fileFullName, targetClassName, menuPath);
    }
}
