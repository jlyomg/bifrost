package com.dataour.bifrost.code.generator.core;

/**
 * ModuleGenerator
 *
 * @Author JASON
 * @Date 2024-12-01 00:14:18
 */
public class ModuleGenerator extends CodeGenerator {
    @Override
    public void genCode(Class targetClass, String menuPath) {
        genSearchReq(targetClass, menuPath);
        genAddReq(targetClass, menuPath);
        genUpdateReq(targetClass, menuPath);
    }

    private void genSearchReq(Class targetClass, String menuPath) {
        genCodeByTemplate(targetClass, menuPath, "ModuleNameSearchReq.tmp", "request", "search", "SearchReq");
    }

    private void genAddReq(Class targetClass, String menuPath) {
        genCodeByTemplate(targetClass, menuPath, "ModuleNameAddReq.tmp", "request", "AddReq");
    }

    private void genUpdateReq(Class targetClass, String menuPath) {
        genCodeByTemplate(targetClass, menuPath, "ModuleNameUpdateReq.tmp", "request", "UpdateReq");

    }
}
