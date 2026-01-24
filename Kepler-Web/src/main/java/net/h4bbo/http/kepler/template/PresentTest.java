package net.h4bbo.http.kepler.template;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

public class PresentTest extends AbstractExtension implements Test {

    @Override
    public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
        if (!(input instanceof String)) throw new IllegalArgumentException("present test only accepts a string");
        return ((EvaluationContextImpl) context).getScopeChain().containsKey((String) input);
    }

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Map<String, Test> getTests() {
        return Map.of("present", this);
    }
}