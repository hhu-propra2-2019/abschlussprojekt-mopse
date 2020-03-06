package mops;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.utils.AggregateRoot;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static mops.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;

@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class ArchitectureRulesTest {

    private static final String mopsPresentation = "mops.presentation";
    private static final String mopsBusinesslogic = "mops.businesslogic";
    private static final String mopsPersistence = "mops.persistence";
    private final JavaClasses javaClasses = new ClassFileImporter().importPackagesOf(Material1Application.class);

    /**
     * * This test looks out for public classes that aren't annotated
     * with Aggregate Rootbut still are public, which stands against
     * having only one Aggregate Root per class/package.
     *
     * @throws Exception if something goes wrong an exception gets thrown.
     */
    @Test
    public void onlyAggregateRootsArePublic() throws Exception {
        ArchRule aggregateRootPublicNothingElse = classes()
                .that()
                .areNotAnnotatedWith(AggregateRoot.class)
                .and()
                .resideInAPackage("..mops.persistence.(*)..")
                .should()
                .notBePublic()
                .because("The implemention of an aggregate should be hidden!");

        aggregateRootPublicNothingElse.check(javaClasses);
    }

    /**
     * * Tests if there is only one Aggregate Root per package
     * for outer communication.
     *
     * @throws Exception If something goes wrong this throws an exception.
     */
    @Test
    public void oneAggregateRootPerAggregate() throws Exception {
        ArchRule oneAggregateRootPerPackage = slices()
                .matching("..mops.persistence.(*)..")
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
                .layer("mopsPersistence").definedBy(mopsPersistence)
                .layer("mopsBusinesslogic").definedBy(mopsBusinesslogic)
                .layer("mopsPresentation").definedBy(mopsPresentation)
                .whereLayer("mopsPresentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("mopsBusinesslogic").mayOnlyBeAccessedByLayers("mopsPresentation")
                .whereLayer("mopsPersistence").mayOnlyBeAccessedByLayers("mopsBusinesslogic");

        checkLayeredArchitecture.check(javaClasses);
    }
}
