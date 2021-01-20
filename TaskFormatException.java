public class TaskFormatException extends RuntimeException {
    /**
     * Runtime Exception for when a Task's format is incorrect when loading from a file
     */
    public TaskFormatException() {
        super("Task Formatting Error!");
    }
}
