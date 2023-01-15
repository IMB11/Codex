package mine.block.quicksearch.math;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.math.BigDecimal;

@FunctionParameter(name = "seconds", nonNegative = true, nonZero = true)
public class SecToTickFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... parameterValues) throws EvaluationException {
        return new EvaluationValue(parameterValues[0].getNumberValue().multiply(BigDecimal.valueOf(20)));
    }
}
