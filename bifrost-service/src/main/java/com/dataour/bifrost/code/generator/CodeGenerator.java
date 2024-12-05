package com.dataour.bifrost.code.generator;

import com.dataour.bifrost.common.util.DateUtils;
import com.dataour.bifrost.domain.TenantDO;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import static com.dataour.bifrost.common.util.DataUtils.dataEmpty;
import static com.dataour.bifrost.common.util.StringUtils.firstLowerCase;
import static com.dataour.bifrost.common.util.StringUtils.firstUpperCase;
import static java.io.File.separator;

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
        Reflections reflections = new Reflections("com.dataour.bifrost.code.generator.impl");
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
     * @param codeTemplate 代码模版
     * @param fileFullName 包含文件路径的文件名称
     */
    public void genCodeByTemplate(String codeTemplate, String fileFullName, String targetClassName, String menuPath) {
        if (dataEmpty(properties)) {
            return;
        }
        codeTemplate = codeTemplate.replaceAll(keyLowerModuleName, firstLowerCase(targetClassName));
        codeTemplate = codeTemplate.replaceAll(keyUpperModuleName, firstUpperCase(targetClassName));
        codeTemplate = codeTemplate.replaceAll(keyLowerModuleDOName, firstLowerCase(targetClassName + "DO"));
        codeTemplate = codeTemplate.replaceAll(keyUpperModuleDOName, firstUpperCase(targetClassName + "DO"));
        codeTemplate = codeTemplate.replaceAll("#targetModulePackage#", properties.getProperty("targetModulePackage"));
        codeTemplate = codeTemplate.replaceAll("#menuPath#", menuPath);
        codeTemplate = codeTemplate.replaceAll("#author#", properties.getProperty("author"));
        codeTemplate = codeTemplate.replaceAll("#dateTime#", DateUtils.formatDate(new Date()));
        codeTemplate = codeTemplate.replaceAll("##", DateUtils.formatDate(new Date()));
        try {
            Path path = Paths.get(fileFullName);
            // 确保父目录存在
            Files.createDirectories(path.getParent());
            Files.write(path, codeTemplate.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("{} is generated success.", fileFullName);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    protected static String getCodeAbsolutePath() {
        return projectAbsolutePath + separator + "src" + separator + "main" + separator + "java" + separator + properties.getProperty("projectBasePath").replaceAll("\\.", separator) + separator + "code";
    }

    protected static String getJavaAbsolutePath() {
        return projectAbsolutePath + properties.getProperty("targetJavaProject");
    }

    protected static String getTargetClassName(Class targetClass) {
        String targetClassName = targetClass.getSimpleName();
        if (targetClassName.endsWith("DO")) {
            targetClassName = targetClassName.substring(0, targetClassName.lastIndexOf("DO"));
        }
        return targetClassName;
    }
}
