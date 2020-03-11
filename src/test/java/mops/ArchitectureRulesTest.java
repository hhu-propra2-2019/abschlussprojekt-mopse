package mops;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.lang.ArchRule;
import mops.utils.AggregateRoot;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static mops.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;

@ArchTag("checkArchitecture")
public class ArchitectureRulesTest {

    private static final String MOPS_PRESENTATION_BASE = ArchitectureRuleConfig.MOPS_PRESENTATION_BASE;
    private static final String MOPS_PERSISTENCE = ArchitectureRuleConfig.MOPS_PERSISTENCE;
    private static final JavaClasses javaClasses = ArchitectureRuleConfig.JAVA_CLASSES;
    /**
     * This test looks out for public classes that aren't annotated
     * with Aggregate Root but are still public, which stands against
     * having only one @AggregateRoot per class/package.
     */
    @Test
    // TODO: find solution
    @Disabled("We need to instantiate Aggregate members other than the root in tests and possibly other code")
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
                .matching(".." + MOPS_PERSISTENCE)
                .should(HAVE_EXACTLY_ONE_AGGREGATE_ROOT);

        oneAggregateRootPerPackage.check(javaClasses);
    }

    /**
     * This tests if there are any Controllers, that aren't in the Presentation layer,
     * which would be a violation of the Layer Architecture.
     */
    @Test
    public void allControllersShouldResideInMopsPresentation() {
        ArchRule allControllersShouldResideInMopsPresentation = classes()
                .that()
                .areAnnotatedWith(Controller.class)
                .and()
                .resideOutsideOfPackage(MOPS_PRESENTATION_BASE)
                .should()
                .notBeAnnotatedWith(Controller.class);

        allControllersShouldResideInMopsPresentation.check(javaClasses);
    }

    /**
     * This checks, if everything in "presentation" is annotated with @Controller,
     * because there shouldn't be anything else there.
     * the "areNotAnnotatedWith(SpringBootTest.class)" is there, so that test.mops.presentation,
     * which are the tests, will not be tested and cause a wrong outcome of the test.
     */
    @Test
    public void everythingInPresentationShouldBeAController() {
        ArchRule everythingInPresentationShouldBeAController = classes()
                .that()
                .resideInAPackage(MOPS_PRESENTATION_BASE)
                .and()
                .areNotAnnotatedWith(SpringBootTest.class)
                .should()
                .beAnnotatedWith(Controller.class);

        everythingInPresentationShouldBeAController.check(javaClasses);
    }

    /**
     * This tests, if there are no Cycles within the Packages.
     */
    @Test
    public void areThereAnyCyclesWithinPackages() {
        ArchRule areThereAnyCyclesWithinPackages = slices()
                .matching("mops.(*)..")
                .should()
                .beFreeOfCycles();

        areThereAnyCyclesWithinPackages.check(javaClasses);
    }
}
