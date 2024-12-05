package com.dataour.bifrost.code.generator;

import com.dataour.bifrost.code.generator.core.CodeGenerator;
import com.dataour.bifrost.domain.TenantDO;

public class CodeGenApplication {
    public static void main(String[] args) {
        CodeGenerator.run(TenantDO.class, "system");
    }
}
