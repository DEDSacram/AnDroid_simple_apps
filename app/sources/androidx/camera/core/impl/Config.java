package androidx.camera.core.impl;

import java.util.Set;

public interface Config {

    public interface OptionMatcher {
        boolean onOptionMatched(Option<?> option);
    }

    public enum OptionPriority {
        ALWAYS_OVERRIDE,
        REQUIRED,
        OPTIONAL
    }

    boolean containsOption(Option<?> option);

    void findOptions(String str, OptionMatcher optionMatcher);

    OptionPriority getOptionPriority(Option<?> option);

    Set<OptionPriority> getPriorities(Option<?> option);

    Set<Option<?>> listOptions();

    <ValueT> ValueT retrieveOption(Option<ValueT> option);

    <ValueT> ValueT retrieveOption(Option<ValueT> option, ValueT valuet);

    <ValueT> ValueT retrieveOptionWithPriority(Option<ValueT> option, OptionPriority optionPriority);

    public static abstract class Option<T> {
        public abstract String getId();

        public abstract Object getToken();

        public abstract Class<T> getValueClass();

        Option() {
        }

        public static <T> Option<T> create(String id, Class<?> valueClass) {
            return create(id, valueClass, (Object) null);
        }

        public static <T> Option<T> create(String id, Class<?> valueClass, Object token) {
            return new AutoValue_Config_Option(id, valueClass, token);
        }
    }

    static boolean hasConflict(OptionPriority priority1, OptionPriority priority2) {
        if (priority1 == OptionPriority.ALWAYS_OVERRIDE && priority2 == OptionPriority.ALWAYS_OVERRIDE) {
            return true;
        }
        if (priority1 == OptionPriority.REQUIRED && priority2 == OptionPriority.REQUIRED) {
            return true;
        }
        return false;
    }

    static Config mergeConfigs(Config extendedConfig, Config baseConfig) {
        MutableOptionsBundle mergedConfig;
        if (extendedConfig == null && baseConfig == null) {
            return OptionsBundle.emptyBundle();
        }
        if (baseConfig != null) {
            mergedConfig = MutableOptionsBundle.from(baseConfig);
        } else {
            mergedConfig = MutableOptionsBundle.create();
        }
        if (extendedConfig != null) {
            for (Option<?> opt : extendedConfig.listOptions()) {
                Option<?> option = opt;
                mergedConfig.insertOption(option, extendedConfig.getOptionPriority(opt), extendedConfig.retrieveOption(option));
            }
        }
        return OptionsBundle.from(mergedConfig);
    }
}
