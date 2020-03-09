package mops;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.utils.AggregateRoot;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static mops.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;

public class ArchitectureRulesTest {

    private static final String MOPS_PRESENTATION = "mops.presentation";
    private static final String MOPS_BUSINESSLOGIC = "mops.businesslogic";
    private static final String MOPS_PERSISTENCE = "mops.persistence";
    private final JavaClasses javaClasses = new ClassFileImporter().importPackagesOf(Material1Application.class);

    /**
     * This test looks out for public classes that aren't annotated
     * with Aggregate Root but still are public, which stands against
     * having only one Aggregate Root per class/package.
     */
    @Test
    public void onlyAggregateRootsArePublic() {
        ArchRule aggregateRootPublicNothingElse = classes()
                .that()
                .areNotAnnotatedWith(AggregateRoot.class)
                .and()
                .resideInAPackage(".." + MOPS_PERSISTENCE + ".(*)..")
                .should()
                .notBePublic()
                .because("The implementation of an aggregate should be hidden!");

        aggregateRootPublicNothingElse.check(javaClasses);
    }

    /**
     * Tests if there is only one Aggregate Root per package
     * for outer communication.
     */
    @Test
    public void oneAggregateRootPerAggregate() {
        ArchRule oneAggregateRootPerPackage = slices()
                .matching(".." + MOPS_PERSISTENCE + ".(*)..")
                .should(HAVE_EXACTLY_ONE_AGGREGATE_ROOT);

        oneAggregateRootPerPackage.check(javaClasses);
    }

    /**
     * This checks, if the layer is correctly used and
     * no wrong accesses are made.
     */
    @ArchTest
    public void checkIfLayeredArchitectureIsNotViolated() {
        ArchRule checkLayeredArchitecture = layeredArchitecture()
                .layer("mopsPersistence").definedBy(MOPS_PERSISTENCE)
                .layer("mopsBusinesslogic").definedBy(MOPS_BUSINESSLOGIC)
                .layer("mopsPresentation").definedBy(MOPS_PRESENTATION)

                .whereLayer("mopsPresentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("mopsBusinesslogic").mayOnlyBeAccessedByLayers("mopsPresentation")
                .whereLayer("mopsPersistence").mayOnlyBeAccessedByLayers(
                        "mopsPresentation", "mopsBusinesslogic");

        checkLayeredArchitecture.check(javaClasses);
    }
}
