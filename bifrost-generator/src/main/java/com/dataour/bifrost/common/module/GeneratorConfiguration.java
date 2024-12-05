package com.dataour.bifrost.common.module;

import lombok.Setter;

import javax.xml.bind.annotation.*;

@Setter
@XmlRootElement(name = "generatorConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneratorConfiguration {

    @XmlElement(name = "Module")
    private Module module;

    public Module getModule() {
        return module;
    }

    @XmlAccessorType(XmlAccessType.FIELD) // 仅映射字段
    public static class Module {
        @XmlAttribute
        private String domainReferenceName;

        @XmlAttribute
        private String menuPath;

        // Getters and Setters
        public String getDomainReferenceName() {
            return domainReferenceName;
        }

        public void setDomainReferenceName(String domainReferenceName) {
            this.domainReferenceName = domainReferenceName;
        }

        public String getMenuPath() {
            return menuPath;
        }

        public void setMenuPath(String menuPath) {
            this.menuPath = menuPath;
        }
    }
}
