package mine.block.quicksearch.math;

import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionIfc;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    @ApiStatus.Internal
    public static HashMap<String, FunctionIfc> CUSTOM_FUNCTIONS = new HashMap<>();

    /**
     * Registers a custom evaluator function to QuickSearch.
     * @param name The name of the function
     * @param func An instance of the function.
     * @return The instance of the function passed to the method.
     * @param <T> The class or lambda which extends AbstractFunction.
     */
    public static <T extends FunctionIfc> T registerFunction(String name, T func) {
        return (T) CUSTOM_FUNCTIONS.put(name, func);
    }

    /**
     * Removes a custom evaluator function from QuickSearch
     * @param name The name of the function.
     * @return Null if the function name doesn't exist, and the function itself if it does exist.
     * @param <T> The class or lambda which extends AbstractFunction.
     */
    @Nullable
    public static <T extends FunctionIfc> T removeFunction(String name) {
        return (T) CUSTOM_FUNCTIONS.remove(name);
    }
}
