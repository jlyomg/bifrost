package com.dataour.bifrost.code.generator;

import com.dataour.bifrost.domain.TenantDO;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static com.dataour.bifrost.common.file.FileUtils.readContent;
import static com.dataour.bifrost.common.util.DataUtils.dataEmpty;
import static com.dataour.bifrost.common.util.DateUtils.formatDate;
import static com.dataour.bifrost.common.util.StringUtils.*;
import static java.io.File.separator;

/**
 * CodeGenerator
 *
 * @Author JASON
 * @Date 2024-12-01 00:14:18
 */
@Slf4j
public abstract class CodeGenerator {
    protected static final String keyLowerModuleName = "moduleName";
    protected static final String keyUpperModuleName = "ModuleName";
    protected static final String keyLowerModuleDOName = "moduleDOName";
    protected static final String keyUpperModuleDOName = "ModuleDOName";
    protected static final String configSplitChar = "=";
    /**
     * 项目路径如：/Users/Jason/IdeaProjects/jly/bifrost_new/bifrost-service
     */
    protected static final String projectAbsolutePath = CodeGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll(separator + "target" + separator + "classes" + separator, "");
    protected static final Properties properties = new Properties();

    public abstract void genCode(Class targetClass, String menuPath);

    public static void main(String[] args) {
        CodeGenerator.run(TenantDO.class, "system");
    }

    public static void run(Class targetClass, String menuPath) {
        if (properties.isEmpty()) {
            try (InputStream input = CodeGenerator.class.getClassLoader().getResourceAsStream("codeGenerator" + separator + "generatorConfig.properties")) {
                properties.load(input);
            } catch (IOException ex) {
                log.error("", ex);
            }
        }
        // 定义扫描的包名
        Reflections reflections = new Reflections(properties.getProperty("projectBasePackage") + ".code.generator.impl");
        // 获取所有 BaseClass 的子类
        Set<Class<? extends CodeGenerator>> subClasses = reflections.getSubTypesOf(CodeGenerator.class);
        // 遍历子类并调用 genCode 方法
        for (Class<? extends CodeGenerator> subClass : subClasses) {
            try {
                // 创建子类实例
                CodeGenerator instance = subClass.getDeclaredConstructor().newInstance();
                // 调用 genCode 方法
                instance.genCode(targetClass, menuPath);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    /**
     * @param templateFileName ModuleNameSearchReq.tmp
     */
    public void genCodeByTemplate(Class targetClass, String menuPath, String templateFileName, String... targetFileNames) {
        if (dataEmpty(properties)) {
            return;
        }
        String codeTemplate = readContent(getCodeAbsolutePath() + getFilePath("template", templateFileName));
        String targetClassName = getTargetClassName(targetClass);
        String moduleClassName = targetClass.getSimpleName();
        // 获取包名+类名
        String fullClassName = targetClass.getName();
        codeTemplate = codeTemplate.replaceAll(keyLowerModuleName, firstLowerCase(targetClassName));
        codeTemplate = codeTemplate.replaceAll(keyUpperModuleName, firstUpperCase(targetClassName));
        codeTemplate = codeTemplate.replaceAll(keyLowerModuleDOName, firstLowerCase(targetClassName + "DO"));
        codeTemplate = codeTemplate.replaceAll(keyUpperModuleDOName, firstUpperCase(targetClassName + "DO"));
        codeTemplate = codeTemplate.replaceAll("#targetModulePackage#", properties.getProperty("targetModulePackage"));
        codeTemplate = codeTemplate.replaceAll("#fullClassName#", fullClassName);
        codeTemplate = codeTemplate.replaceAll("#menuPath#", menuPath);
        codeTemplate = codeTemplate.replaceAll("#author#", properties.getProperty("author"));
        codeTemplate = codeTemplate.replaceAll("#dateTime#", formatDate(new Date()));
        codeTemplate = codeTemplate.replaceAll("#moduleClassName#", moduleClassName);
        try {
            Path path = Paths.get(getTargetFileFullName(targetClass, targetFileNames));
            // 确保父目录存在
            Files.createDirectories(path.getParent());
            Files.write(path, codeTemplate.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("{} is generated success.", path.getFileName());
        } catch (IOException e) {
            log.error("", e);
        }
    }

    protected static String getCodeAbsolutePath() {
        return projectAbsolutePath + separator + "src" + separator + "main" + separator + "java" + separator + properties.getProperty("projectBasePackage").replaceAll("\\.", separator) + separator + "code";
    }

    protected static String getJavaAbsolutePath() {
        return projectAbsolutePath + properties.getProperty("targetJavaProject");
    }

    /**
     * 有DO去掉DO
     *
     * @param targetClass
     * @return
     */
    protected static String getTargetClassName(Class targetClass) {
        String targetClassName = targetClass.getSimpleName();
        if (targetClassName.endsWith("DO")) {
            targetClassName = targetClassName.substring(0, targetClassName.lastIndexOf("DO"));
        }
        return targetClassName;
    }

    private String getTargetFileFullName(Class targetClass, String... moduleFileNames) {
        ArrayList<String> fileList = new ArrayList<>(Arrays.asList(moduleFileNames));
        fileList.add(0, properties.getProperty("targetModulePackage").replaceAll("\\.", separator));
        int lastIndex = fileList.size() - 1;
        String lastStr = getTargetClassName(targetClass) + fileList.get(lastIndex) + ".java";
        fileList.remove(lastIndex);
        fileList.add(lastIndex, lastStr);
        return getJavaAbsolutePath() + getFilePath(fileList.toArray(new String[0]));
    }
}
