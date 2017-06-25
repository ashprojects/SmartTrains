package SmartTrainsDB.modals.fields;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by root on 11/6/17.
 */

public class DateTime extends Varchar {
    private static final String sqlTypeName = "DATETIME";
    public static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    public DateTime(boolean isPrimaryKey) {
        super(isPrimaryKey);
    }

    public DateTime() {
        super(false);
    }

    public DateTime(boolean unique, boolean notNull) {
        super(unique, notNull);
    }

    @Override
    public String getSqlTypeName() {
        return sqlTypeName;
    }

    @Override
    public boolean validate(Object object) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateTimeFormat, Locale.getDefault());
        try {
            dateFormat.parse(object.toString());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
