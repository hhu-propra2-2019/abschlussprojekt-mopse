package mops;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "mops")
public class LayeredArchitectureTest {

    private static final String MOPS_PRESENTATION  = "mops.presentation..";
    private static final String MOPS_BUSINESSLOGIC = "mops.businesslogic..";
    private static final String MOPS_PERSISTENCE   = "mops.persistence..";
    private static final String MOPS_ROOT_LAYER    = "mops";
    /**
     * This checks, if the layer is correctly used and
     * no wrong accesses are made.
     */
    @ArchTest
    static final ArchRule checkLayeredArchitecture = layeredArchitecture()
            .layer("mopsPersistence").definedBy(MOPS_PERSISTENCE)
            .layer("mopsBusinesslogic").definedBy(MOPS_BUSINESSLOGIC)
            .layer("mopsPresentation").definedBy(MOPS_PRESENTATION)
            .layer("mopsRoot").definedBy(MOPS_ROOT_LAYER)

            .whereLayer("mopsPresentation").mayOnlyBeAccessedByLayers("mopsRoot")
            .whereLayer("mopsBusinesslogic").mayOnlyBeAccessedByLayers("mopsPresentation", "mopsRoot")
            .whereLayer("mopsPersistence").mayOnlyBeAccessedByLayers(
                    "mopsPresentation", "mopsBusinesslogic", "mopsRoot");

}
