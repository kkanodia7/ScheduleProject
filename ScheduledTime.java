import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScheduledTime {

    private static final DateFormat dispDateFormat = new SimpleDateFormat("EEEE, M/d/yy");
    private static final DateFormat dispTimeFormat = new SimpleDateFormat("h:mm a");
    private static final DateFormat inputDateTimeFormat = new SimpleDateFormat("M/d/yy h:mm a");

    private Calendar calendar;

    /**
     * Creates ScheduledTime object based on milliseconds given
     * @param millis milliseconds since the start of 1970, determining time
     */
    public ScheduledTime(long millis) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
    }

    /**
     * Gets time in milliseconds of this ScheduledTime object
     * @return time in milliseconds
     */
    public long getMillis() {
        return calendar.getTimeInMillis();
    }

    /**
     * Gets time in milliseconds of date and time in param based on SimpleDateFormat above,
     * throwing a TaskFormatException if format does not match
     * @param dateTimeTxt a date and time in the particular format of "inputDateTimeFormat"
     * @return millisecond representation of the date and time in param
     */
    public static long getInputMillis(String dateTimeTxt) {
        inputDateTimeFormat.setLenient(false);
        try {
            return inputDateTimeFormat.parse(dateTimeTxt).getTime();
        } catch (ParseException e) {
            throw new TaskFormatException();
        }
    }

    /**
     * Gets a nicely formatted String of this ScheduledTime's date based on the
     * "dispDateFormat" SimpleDateFormat above
     * @return nicely formatted date String
     */
    public String dateStr() {
        return dispDateFormat.format(calendar.getTime());
    }

    /**
     * Gets a nicely formatted String of this ScheduledTime's time based on the
     * "dispTimeFormat" SimpleDateFormat above
     * @return nicely formatted time String
     */
    public String timeStr() {
        return dispTimeFormat.format(calendar.getTime());
    }

    /**
     * Gets a nicely formatted String of this ScheduledTime's date and time based on the
     * "dispDateFormat" SimpleDateFormat above
     * @return nicely formatted date and time String
     */
    public String dateTimeStr() {
        return inputDateTimeFormat.format(calendar.getTime());
    }

}
