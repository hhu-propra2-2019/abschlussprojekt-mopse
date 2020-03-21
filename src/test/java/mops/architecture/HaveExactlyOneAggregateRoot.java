package mops.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.library.dependencies.Slice;
import mops.util.AggregateRoot;

import java.util.List;

import static com.tngtech.archunit.lang.SimpleConditionEvent.satisfied;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static java.util.stream.Collectors.toList;


/**
 * This is a class that helps the tests to find out
 * whether every package has only one Aggregate Root or not.
 */
class HaveExactlyOneAggregateRoot extends ArchCondition<Slice> {

    static final HaveExactlyOneAggregateRoot HAVE_EXACTLY_ONE_AGGREGATE_ROOT = new HaveExactlyOneAggregateRoot();

    private HaveExactlyOneAggregateRoot() {
        super("have exactly one aggregate root");
    }

    /**
     * @param slice           this helps to iterate over all packages and to get all Aggregate Roots.
     * @param conditionEvents here it says whether we only have one AggregateRoot or not
     *                        and gives it back up.
     */
    @Override
    public void check(Slice slice, ConditionEvents conditionEvents) {
        List<String> aggregateRootNames = getAggregateRootNames(slice);

        if (aggregateRootNames.size() == 1) {
            conditionEvents.add(satisfied(slice, "Exactly one Aggregate Root!"));
        } else {
            conditionEvents.add(violated(slice, "Violation of only one Aggregate Root!"));
        }
    }

    /**
     * @param slice this helps to iterate over a class and get all Aggregate Roots.
     * @return this returns all Aggregate Roots as a list.
     */
    private List<String> getAggregateRootNames(Slice slice) {
        return slice.stream()
                .filter(c -> c.isAnnotatedWith(AggregateRoot.class))
                .map(JavaClass::getName)
                .collect(toList());
    }
}
