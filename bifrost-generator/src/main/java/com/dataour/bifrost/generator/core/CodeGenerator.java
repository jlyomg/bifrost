package com.dataour.bifrost.generator.core;

import com.dataour.bifrost.common.module.GeneratorConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static com.dataour.bifrost.common.util.DataUtils.dataEmpty;
import static com.dataour.bifrost.common.util.DateUtils.formatDate;
import static com.dataour.bifrost.common.util.StringUtils.*;
import static java.io.File.separator;

/**
 * Code Generator
 *
 * @Author JASON
 * @Date 2024-12-01 00:14:18
 */
@Slf4j
public abstract class CodeGenerator {
    public static final String codeGeneratorDir = "codeGenerator";
    protected static final String keyLowerModuleName = "moduleName";
    protected static final String keyUpperModuleName = "ModuleName";
    protected static final String keyLowerModuleDOName = "moduleDOName";
    protected static final String keyUpperModuleDOName = "ModuleDOName";
    protected static final String configSplitChar = "=";
    /**
     * 当前正在执行的项目路径
     */
    public static final String targetProjectAbsolutePath = System.getProperty("user.dir");
    /**
     * 项目路径如：/Users/Jason/IdeaProjects/jly/bifrost/bifrost-generator
     */
    protected static final String projectAbsolutePath = CodeGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll(getFilePath("target", "classes") + separator, "");
    private static final String propertiesPath = targetProjectAbsolutePath + separator + "src/main/resources/codeGenerator/generatorConfig.properties";

    protected static final Properties properties = new Properties();

    public abstract void genCode(Class targetClass, String menuPath);

    public static void run(GeneratorConfiguration generatorConfiguration) {
        if (generatorConfiguration == null) {
            return;
        }
        GeneratorConfiguration.Module module = generatorConfiguration.getModule();
        if (module == null || dataEmpty(module.getDomainReferenceName())) {
            return;
        }
        if (properties.isEmpty()) {
            try (InputStream input = Files.newInputStream(Paths.get(propertiesPath))) {
                properties.load(input);
            } catch (IOException ex) {
                log.error("", ex);
            }
        }
        Class targetClass;
        try {
            targetClass = Class.forName(module.getDomainReferenceName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // 获取当前类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Reflections reflections = new Reflections(new org.reflections.util.ConfigurationBuilder().forPackages(properties.getProperty("projectBasePackage")).addUrls(ClasspathHelper.forClassLoader()));
        // 获取所有 BaseClass 的子类
        Set<Class<? extends CodeGenerator>> subClasses = reflections.getSubTypesOf(CodeGenerator.class);
        // 遍历子类并调用 genCode 方法
        for (Class<? extends CodeGenerator> subClass : subClasses) {
            try {
                // 创建子类实例
                CodeGenerator instance = subClass.getDeclaredConstructor().newInstance();
                // 调用 genCode 方法
                instance.genCode(targetClass, module.getMenuPath());
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
        String codeTemplate = readContent(templateFileName);
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

    protected static String getCodeClassPath() {
        return "jar:file:" + projectAbsolutePath + "!";
    }

    protected static String getTatgetAbsolutePath() {
        return targetProjectAbsolutePath + properties.getProperty("targetJavaProject");
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
        return getTatgetAbsolutePath() + getFilePath(fileList.toArray(new String[0]));
    }

    public static String readContent(String templateFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        // 文件在 JAR 中的路径
        String resourcePath = "template" + separator + templateFileName;
        // 使用 ClassLoader 加载资源
        try (InputStream inputStream = CodeGenerator.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return stringBuilder.toString();
            }
            // 读取文件内容
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            stringBuilder.append(content).append("\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}
