package mops;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class ArchitectureRuleConfig {

    public static final String MOPS_PRESENTATION_BASE = "mops.presentation";
    public static final String MOPS_PRESENTATION = "mops.presentation..";
    public static final String MOPS_BUSINESSLOGIC = "mops.businesslogic..";
    public static final String MOPS_PERSISTENCE = "mops.persistence..";

    public static final JavaClasses JAVA_CLASSES = new ClassFileImporter().importPackagesOf(Material1Application.class);
}
