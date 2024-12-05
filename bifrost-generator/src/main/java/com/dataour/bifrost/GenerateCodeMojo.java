package com.dataour.bifrost;

import com.dataour.bifrost.common.module.GeneratorConfiguration;
import com.dataour.bifrost.generator.core.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.dataour.bifrost.generator.core.CodeGenerator.targetProjectAbsolutePath;
import static java.io.File.separator;

@Slf4j
@Mojo(name = "generate")
public class GenerateCodeMojo extends AbstractMojo {
    private static final String configFilePath = targetProjectAbsolutePath + separator + "src/main/resources/codeGenerator/generatorConfig.xml";

    @Override
    public void execute() {
        // 获取当前工作目录
        String currentExecutionPath = System.getProperty("user.dir");

        // 打印路径信息
        getLog().info("Current execution path: " + currentExecutionPath);

        log.info("Running Bifrost Code Generator...");
        // 插件的代码生成逻辑
        CodeGenerator.run(getTargetDomainClassInfo());
        log.info("Code generation completed.");
    }

    private GeneratorConfiguration getTargetDomainClassInfo() {
        File configFile = new File(configFilePath);
        GeneratorConfiguration config;
        try (InputStream input = Files.newInputStream(Paths.get(configFilePath))) {
            JAXBContext context = JAXBContext.newInstance(GeneratorConfiguration.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // 从 InputStream 解析配置
            config = (GeneratorConfiguration) unmarshaller.unmarshal(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading generator configuration", e);
        }
        return config;
    }
}
