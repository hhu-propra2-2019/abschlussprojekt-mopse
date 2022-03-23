package mops.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.Material1Application;
import mops.util.AggregateBuilder;
import mops.util.AggregateRoot;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static mops.architecture.ArchitectureRuleConfig.*;
import static mops.architecture.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;

@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packagesOf = Material1Application.class)
class ArchitectureRulesTest {

    /**
     * This test looks out for public classes that aren't annotated
     * with Aggregate Root but are still public, which stands against
     * having only one @AggregateRoot per class/package.
     */
    @ArchTest
    static final ArchRule AGGREGATE_ROOT_PUBLIC_NOTHING_ELSE = classes()
            .that()
            .areNotAnnotatedWith(AggregateRoot.class)
            .and()
            .areNotAnnotatedWith(AggregateBuilder.class)
            .and()
            .areNotAnnotatedWith(Configuration.class)
            .and()
            .areNotAssignableTo(Exception.class)
            .and()
            .resideInAPackage(".." + MOPS_PERSISTENCE)
            .should()
            .notBePublic()
            .because("the implementation of an aggregate should be hidden!");

    /**
     * Tests if there is only one Aggregate Root per package
     * for outer communication.
     */
    @ArchTest
    static final ArchRule ONE_AGGREGATE_ROOT_PER_PACKAGE = slices()
            .matching("..persistence.(*)" )
            .should(HAVE_EXACTLY_ONE_AGGREGATE_ROOT);

    /**
     * This tests if there are any Controllers, that aren't in the Presentation layer,
     * which would be a violation of the Layer Architecture.
     */
    @ArchTest
    static final ArchRule ALL_CONTROLLERS_SHOULD_RESIDE_IN_MOPS_PRESENTATION = classes()
            .that()
            .areAnnotatedWith(Controller.class)
            .and()
            .resideOutsideOfPackage(MOPS_PRESENTATION_BASE)
            .should()
            .notBeAnnotatedWith(Controller.class)
            .allowEmptyShould(true);

    /**
     * This checks, if everything in "presentation" is annotated with @Controller,
     * because there shouldn't be anything else there.
     * the "areNotAnnotatedWith(SpringBootTest.class)" is there, so that test.mops.presentation,
     * which are the tests, will not be tested and cause a wrong outcome of the test.
     */
    @ArchTest
    static final ArchRule EVERYTHING_IN_PRESENTATION_SHOULD_BE_A_CONTROLLER = classes()
            .that()
            .resideInAPackage(MOPS_PRESENTATION_BASE)
            .and()
            .areNotAnnotatedWith(WebMvcTest.class)
            .should()
            .beAnnotatedWith(Controller.class);

    /**
     * This tests, if there are no Cycles within the Packages.
     */
    // @ArchTest
    // TODO: re-enable once cycles are fixed
    static final ArchRule ARE_THERE_ANY_CYCLES_WITHIN_PACKAGES = slices()
            .matching(MOPS + ".(**)")
            .should()
            .beFreeOfCycles();

    /**
     * This tests if all Classes that have Service in their name
     * are annotated with @Service.
     */
    @ArchTest
    static final ArchRule SERVICES_ARE_ANNOTATED_WITH_SERVICE = classes()
            .that()
            .haveSimpleNameContaining("Service")
            .and()
            .resideInAPackage(MOPS_BUSINESSLOGIC)
            .should()
            .beAnnotatedWith(Service.class);

    /**
     * This tests if all classes that are annotated with @Service
     * also have Service in their name, because otherwise it would be confusing.
     */
    @ArchTest
    static final ArchRule EVERYTHING_ANNOTATED_WITH_SERVICE_SHOULD_HAVE_SERVICE_IN_NAME = classes()
            .that()
            .areAnnotatedWith(Service.class)
            .and()
            .resideInAPackage(MOPS_BUSINESSLOGIC)
            .should()
            .haveSimpleNameContaining("Service");
}
