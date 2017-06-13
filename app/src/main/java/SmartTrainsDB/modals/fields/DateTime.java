package SmartTrainsDB.modals.fields;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by root on 11/6/17.
 */

public class DateTime extends Varchar {
    private static final String sqlTypeName = "DATETIME";
    private static final String dateFormat = "YYYY-MM-DD", timeFormat = " HH:MM:SS";

    public DateTime(boolean isPrimaryKey) {
        super(isPrimaryKey);
    }

    public DateTime() {
        super(false);
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(String value) {
        SimpleDateFormat dateFormater = new SimpleDateFormat(dateFormat);
        try {
            dateFormater.parse(value.split(" ")[0]);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid datetime format");
        }
        super.setValue(value);
    }

    @Override
    public String getSqlTypeName() {
        return sqlTypeName;
    }

    @Override
    public boolean validate(Object object) {
        // TODO parse and validate
        return super.validate(object);
    }
}
