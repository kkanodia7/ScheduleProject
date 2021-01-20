public class Task implements Comparable<Task> {

    private String name;
    private String description;
    private ScheduledTime schedTime;
    private int duration;
    private ScheduledTime schedTimeEnd;

    /**
     * Creates Task object based on params
     * @param name name of the task
     * @param description description of the task
     * @param schedTime date and time the task is scheduled for
     * @param duration how many minutes the task will take
     */
    public Task(String name, String description, ScheduledTime schedTime, int duration) {
        this.name = name;
        this.description = description;
        this.schedTime = schedTime;
        this.duration = duration;
        this.schedTimeEnd = new ScheduledTime(schedTime.getMillis() + duration * 60 * 1000);
    }

    /**
     * Creates Task object based on a String containing all Task data
     * @param data String containing all Task data
     */
    public Task(String data) {
        if (!data.contains("||name||") || !data.contains("||desc||") || !data.contains("||sched||") || !data.contains("||dur||"))
            throw new TaskFormatException();
        String name = data.substring(data.indexOf("||name||") + 8, data.indexOf("||desc||"));
        String desc = data.substring(data.indexOf("||desc||") + 8, data.indexOf("||sched||"));
        String sched = data.substring(data.indexOf("||sched||") + 9, data.indexOf("||dur||"));
        int dur = Integer.parseInt(data.substring(data.indexOf("||dur||") + 7));
        ScheduledTime st = new ScheduledTime(ScheduledTime.getInputMillis(sched));
        this.name = name;
        this.description = desc;
        this.schedTime = st;
        this.duration = dur;
        this.schedTimeEnd = new ScheduledTime(schedTime.getMillis() + duration * 60 * 1000);
    }

    /**
     * Gets Task name
     * @return Task name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets Task description
     * @return Task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets date and time Task is scheduled for
     * @return date and time Task is scheduled for
     */
    public ScheduledTime getSchedTime() {
        return schedTime;
    }

    /**
     * Gets date and time Task is scheduled to end at
     * @return date and time Task is scheduled to end at
     */
    public ScheduledTime getSchedTimeEnd() {
        return schedTimeEnd;
    }

    /**
     * Gets Task duration in minutes
     * @return Task duration in minutes
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets all Task data in single String
     * @return String containing Task data
     */
    public String getData() {
        return "||name||" + name + "||desc||" + description + "||sched||" + schedTime.dateTimeStr() +
                "||dur||" + duration;
    }

    /**
     * Sets Task name
     * @param name new Task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets Task description
     * @param description new Task description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets new scheduled date and time for Task, updating end time accordingly
     * @param schedTime new date and time to schedule Task at
     */
    public void setSchedTime(ScheduledTime schedTime) {
        this.schedTime = schedTime;
        this.schedTimeEnd = new ScheduledTime(this.schedTime.getMillis() + this.duration * 60 * 1000);
    }

    /**
     * Sets new duration for Task in minutes, updating end time accordingly
     * @param duration new duration for Task in minutes
     */
    public void setDuration(int duration) {
        this.duration = duration;
        this.schedTimeEnd = new ScheduledTime(this.schedTime.getMillis() + this.duration * 60 * 1000);
    }

    /**
     * Compares this Task to other to determine ascending order of scheduled times
     * @param task other Task to compare this Task to
     * @return negative number, positive number, or 0 depending on order of Task scheduled times
     */
    @Override
    public int compareTo(Task task) {
        return (int) (this.schedTime.getMillis() - task.schedTime.getMillis());
    }

}
