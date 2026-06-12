package gui;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static LocalizationManager instance;
    private final ObjectProperty<Locale> locale = new SimpleObjectProperty<>();
    private ResourceBundle bundle;

    private LocalizationManager() {
        // По умолчанию русский язык
        setLocale(new Locale("ru"));
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale newLocale) {
        locale.set(newLocale);
        bundle = ResourceBundle.getBundle("messages", newLocale);
    }

    public Locale getLocale() {
        return locale.get();
    }

    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    /**
     * Создает привязку строки, которая автоматически обновляется при смене локали.
     */
    public StringBinding createBinding(String key) {
        return new StringBinding() {
            {
                bind(locale);
            }

            @Override
            protected String computeValue() {
                return getString(key);
            }
        };
    }
}
