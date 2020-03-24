package mops.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static mops.architecture.ArchitectureRuleConfig.*;

@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packages = "mops")
class LayeredArchitectureTest {

    /**
     * This checks, if the layer is correctly used and
     * no wrong accesses are made.
     */
    @ArchTest
    static final ArchRule CHECK_LAYERED_ARCHITECTURE = layeredArchitecture()
            .layer("mopsPersistence").definedBy(MOPS_PERSISTENCE)
            .layer("mopsBusinesslogic").definedBy(MOPS_BUSINESSLOGIC)
            .layer("mopsPresentation").definedBy(MOPS_PRESENTATION)

            .whereLayer("mopsPresentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("mopsBusinesslogic").mayOnlyBeAccessedByLayers("mopsPresentation")
            .whereLayer("mopsPersistence").mayOnlyBeAccessedByLayers("mopsPresentation", "mopsBusinesslogic");

}
