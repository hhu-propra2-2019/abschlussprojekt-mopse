package mops;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("checkLayeredArchitecture")
@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packages = "mops")
public class LayeredArchitectureTest {

    /**
     * This checks, if the layer is correctly used and
     * no wrong accesses are made.
     */
    @ArchTest
    static final ArchRule checkLayeredArchitecture = layeredArchitecture()
            .layer("mopsPersistence").definedBy(ArchitectureRuleConfig.MOPS_PERSISTENCE)
            .layer("mopsBusinesslogic").definedBy(ArchitectureRuleConfig.MOPS_BUSINESSLOGIC)
            .layer("mopsPresentation").definedBy(ArchitectureRuleConfig.MOPS_PRESENTATION)

            .whereLayer("mopsPresentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("mopsBusinesslogic").mayOnlyBeAccessedByLayers("mopsPresentation")
            .whereLayer("mopsPersistence").mayOnlyBeAccessedByLayers("mopsPresentation", "mopsBusinesslogic");
}
