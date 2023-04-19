package androidx.camera.core.impl;

import androidx.camera.core.impl.Config;
import java.util.Set;

public interface ReadableConfig extends Config {
    Config getConfig();

    boolean containsOption(Config.Option<?> id) {
        return getConfig().containsOption(id);
    }

    <ValueT> ValueT retrieveOption(Config.Option<ValueT> id) {
        return getConfig().retrieveOption(id);
    }

    <ValueT> ValueT retrieveOption(Config.Option<ValueT> id, ValueT valueIfMissing) {
        return getConfig().retrieveOption(id, valueIfMissing);
    }

    void findOptions(String idSearchString, Config.OptionMatcher matcher) {
        getConfig().findOptions(idSearchString, matcher);
    }

    Set<Config.Option<?>> listOptions() {
        return getConfig().listOptions();
    }

    <ValueT> ValueT retrieveOptionWithPriority(Config.Option<ValueT> id, Config.OptionPriority priority) {
        return getConfig().retrieveOptionWithPriority(id, priority);
    }

    Config.OptionPriority getOptionPriority(Config.Option<?> opt) {
        return getConfig().getOptionPriority(opt);
    }

    Set<Config.OptionPriority> getPriorities(Config.Option<?> option) {
        return getConfig().getPriorities(option);
    }
}
