package mops;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.utils.AggregateBuilder;
import mops.utils.AggregateRoot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static mops.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;

@ArchTag("checkArchitecture")
@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packages = "mops")
public class ArchitectureRulesTest {

    /**
     * This test looks out for public classes that aren't annotated
     * with Aggregate Root but are still public, which stands against
     * having only one @AggregateRoot per class/package.
     */
    @ArchTest
    static final ArchRule aggregateRootPublicNothingElse = classes()
            .that()
            .areNotAnnotatedWith(AggregateRoot.class)
            .and()
            .areNotAnnotatedWith(AggregateBuilder.class)
            .and()
            .resideInAPackage(".." + ArchitectureRuleConfig.MOPS_PERSISTENCE)
            .should()
            .notBePublic()
            .because("The implementation of an aggregate should be hidden!");

    /**
     * Tests if there is only one Aggregate Root per package
     * for outer communication.
     */
    @Test
    public void oneAggregateRootPerAggregate() {
        ArchRule oneAggregateRootPerPackage = slices()
                .matching(".." + ArchitectureRuleConfig.MOPS_PERSISTENCE)
                .should(HAVE_EXACTLY_ONE_AGGREGATE_ROOT);

        oneAggregateRootPerPackage.check(ArchitectureRuleConfig.JAVA_CLASSES);
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
                .resideOutsideOfPackage(ArchitectureRuleConfig.MOPS_PRESENTATION_BASE)
                .should()
                .notBeAnnotatedWith(Controller.class);

        allControllersShouldResideInMopsPresentation.check(ArchitectureRuleConfig.JAVA_CLASSES);
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
                .resideInAPackage(ArchitectureRuleConfig.MOPS_PRESENTATION_BASE)
                .and()
                .areNotAnnotatedWith(WebMvcTest.class)
                .should()
                .beAnnotatedWith(Controller.class);

        everythingInPresentationShouldBeAController.check(ArchitectureRuleConfig.JAVA_CLASSES);
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

        areThereAnyCyclesWithinPackages.check(ArchitectureRuleConfig.JAVA_CLASSES);
    }
}
