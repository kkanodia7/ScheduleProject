import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

public class Schedule {

    private static ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Prints all Tasks out in a nicely formatted list, with the date heading and then the time
     * range each Task is scheduled for, followed by the Task name and description
     * @param toDelete indication of whether to number Tasks for deletion or not
     */
    public static void printAllTasks(boolean toDelete) {
        if (tasks.isEmpty()) {
            System.out.println("You have no tasks!");
            return;
        }
        String lastDate = tasks.get(0).getSchedTime().dateStr();
        System.out.println(lastDate);
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String currDate = t.getSchedTime().dateStr();
            if (!lastDate.equals(currDate)) {
                System.out.println("\n" + currDate);
                lastDate = currDate;
            }
            String taskEntry = t.getSchedTime().timeStr() + " - " + t.getSchedTimeEnd().timeStr() +
                    ": " + t.getName();
            if (toDelete)
                taskEntry = "(" + (i + 1) + ") " + taskEntry;
            else
                taskEntry += " - " + t.getDescription();
            System.out.println(taskEntry);
        }
    }

    /**
     * Prompts user to enter a new Task's information - its name, description, scheduled date and time,
     * and duration. This method uses extensive error checking to ensure all information is properly formatted
     * and correctly entered. It will also ensure that Tasks are scheduled for future times and that no
     * Task schedules conflict with each other. Finally it adds the Task to the list.
     */
    public static void newTask() {
        System.out.println("Adding new task.");
        Scanner sc = new Scanner(System.in);
        DateFormat inputDateFormat = new SimpleDateFormat("M/d/yy");
        DateFormat inputDateTimeFormat = new SimpleDateFormat("M/d/yy h:mm a");
        inputDateFormat.setLenient(false);
        inputDateTimeFormat.setLenient(false);

        String taskName;
        String taskDescription;
        String taskDateTime;
        String hrs;
        String mins;

        do {
            System.out.print("Task Name: ");
            taskName = sc.nextLine();
            if (taskName.indexOf('|') != -1) {
                System.out.println("Please refrain from using the | character.");
                taskName = "";
            }
        } while (taskName.equals(""));

        do {
            System.out.print("Task Description: ");
            taskDescription = sc.nextLine();
            if (taskDescription.indexOf('|') != -1) {
                System.out.println("Please refrain from using the | character.");
                taskDescription = "|";
            }
        } while (taskDescription.equals("|"));

        do {
            System.out.print("Schedule for what date and time (ex. 2/15/21 3:30 PM)? ");
            taskDateTime = sc.nextLine();
            try {
                Date d = inputDateTimeFormat.parse(taskDateTime);
                if (d.getTime() < (new Date()).getTime()) {
                    System.out.println("Please enter a future date and time.");
                    taskDateTime = "";
                }
            } catch (ParseException e) {
                System.out.println("Please enter a date and time in the format shown.");
                taskDateTime = "";
            }
        } while (taskDateTime.equals(""));

        do {
            System.out.print("How many hours will this task take? ");
            hrs = sc.nextLine();
            try {
                int hourVal = Integer.parseInt(hrs);
                if (hourVal < 0) {
                    System.out.println("Please enter an integer greater than or equal to 0.");
                    hrs = "";
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer for the number of hours.");
                hrs = "";
            }
        } while (hrs.equals(""));

        do {
            System.out.print("How many minutes will this task take? ");
            mins = sc.nextLine();
            try {
                int minuteVal = Integer.parseInt(mins);
                if (minuteVal >= 60 || minuteVal < 0) {
                    System.out.println("Please enter an integer between 0 and 59 for the number of minutes.");
                    mins = "";
                } else if (mins.equals("0") && hrs.equals("0")) {
                    System.out.println("Please enter a number higher than 0 for the duration.");
                    mins = "";
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer for the number of minutes.");
                mins = "";
            }
        } while (mins.equals(""));

        ScheduledTime taskSchedTime = new ScheduledTime(ScheduledTime.getInputMillis(taskDateTime));
        int taskDuration = Integer.parseInt(hrs) * 60 + Integer.parseInt(mins);
        Task t = new Task(taskName, taskDescription, taskSchedTime, taskDuration);
        tasks.add(t);
        Collections.sort(tasks);
        int index = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).equals(t)) {
                index = i;
                break;
            }
        }
        if ((index > 0 && tasks.get(index-1).getSchedTimeEnd().getMillis() > t.getSchedTime().getMillis()) ||
                (index < tasks.size()-1 && tasks.get(index+1).getSchedTime().getMillis() < t.getSchedTimeEnd().getMillis())) {
            System.out.println("The new task's schedule conflicts with a previous task! Task failed to add.");
            tasks.remove(t);
        } else {
            save();
            System.out.println("Task added: " + t.getName());
        }
    }

    /**
     * Deletes a Task from the Task list by printing all Tasks with their corresponding numbers, and prompting
     * the user to pick a number Task to delete. Also allows user to cancel deletion by entering 0.
     */
    public static void deleteTask() {
        printAllTasks(true);
        if (tasks.isEmpty())
            return;
        Scanner sc = new Scanner(System.in);
        int index;
        do {
            System.out.print("\nWhich number task would you like to delete? (0 to cancel): ");
            try {
                index = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                index = -1;
            }
        } while (index < 0 || index > tasks.size());
        if (index != 0) {
            Task t = tasks.remove(index - 1);
            save();
            System.out.println("Task deleted: " + t.getName());
        }
    }

    /**
     * Creates Schedule.txt file if it doesn't already exist, and prints list of all Tasks to the file. This
     * file is formatted the same way as the printAllTasks() method's list; see above for format.
     */
    public static void exportToFile() {
        if (tasks.isEmpty()) {
            System.out.println("You have no tasks!");
            return;
        }
        File scheduleFile = new File("Schedule.txt");
        try {
            scheduleFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to export to file!");
        }
        try {
            FileWriter fileWriter = new FileWriter("Schedule.txt");
            String lastDate = tasks.get(0).getSchedTime().dateStr();
            fileWriter.write(lastDate + "\n");
            for (Task t : tasks) {
                String currDate = t.getSchedTime().dateStr();
                if (!lastDate.equals(currDate)) {
                    fileWriter.write("\n" + currDate + "\n");
                    lastDate = currDate;
                }
                String taskEntry = t.getSchedTime().timeStr() + " - " + t.getSchedTimeEnd().timeStr() +
                        ": " + t.getName() + " - " + t.getDescription();
                fileWriter.write(taskEntry + "\n");
            }
            fileWriter.close();
            System.out.println("Exported to Schedule.txt!");
        } catch (IOException e) {
            System.out.println("Unable to export to file!");
        }
    }

    /**
     * Gives a thorough explanation of the format for the text file, then asks the user to input the file name.
     * Overrides all current Tasks in list with Tasks found in the file using load() method, explained below.
     */
    public static void importFromFile() {
        Scanner sc = new Scanner(System.in);
        System.out.println("This will override current tasks with those in file. Please prepare a txt file with the following format:");
        System.out.println("||name||<task-name>||desc||<task-description>||sched||<date-time>||dur||<total-minutes>");
        System.out.println("Replace the text within <> with your task's properties. For example:");
        System.out.println("||name||Task 1||desc||My Description||sched||2/15/21 3:00 PM||dur||220");
        System.out.println("Make a new line for each task and ensure everything is formatted properly.");
        System.out.println("Note that for duration you must enter the total minutes. Also, do not use the | character.");
        System.out.println("Place the txt file in the same directory as this program, then enter its name (leave blank to cancel): ");
        String fileName = sc.nextLine();
        if (fileName.equals(""))
            return;
        load(fileName);
        save();
    }

    /**
     * Creates data.txt file if it doesn't already exist, and writes all Tasks to it in a particular format.
     */
    public static void save() {
        File taskData = new File("data.txt");
        try {
            taskData.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to save data!");
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter("data.txt");
            for (Task t : tasks) {
                fileWriter.write(t.getData() + '\n');
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Unable to save data!");
        }
    }

    /**
     * Converts all lines of given file to Tasks and loads them to Task list. If an error in the formatting
     * of the file is found, the Tasks will not be loaded into the list.
     * @param fileName name of file to load Task data from
     */
    public static void load(String fileName) {
        long currMillis = (new Date()).getTime();
        try {
            File taskData = new File(fileName);
            Scanner fileSc = new Scanner(taskData);
            ArrayList<Task> tempTasks = new ArrayList<>();
            while (fileSc.hasNextLine()) {
                Task t = new Task(fileSc.nextLine());
                if (currMillis < t.getSchedTimeEnd().getMillis())
                    tempTasks.add(t);
            }
            Collections.sort(tempTasks);
            tasks.clear();
            tasks.addAll(tempTasks);
        } catch (FileNotFoundException e) {
            if (!fileName.equals("data.txt"))
                System.out.println("File " + fileName + " not found!");
        } catch (Exception e) {
            System.out.println("Formatting error in " + fileName + "!");
        }
    }

    /**
     * Loads Task data from data.txt, then asks the user to pick between 6 options: See all tasks, enter a new task,
     * delete an old task, export the current schedule to a file, import a schedule from a file, and exit.
     * Calls methods accordingly, continues asking until user chooses Exit.
     */
    public static void main(String[] args) {
        load("data.txt");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--------------------------------------------------------------");
            System.out.println("What would you like to do? Enter the corresponding number.");
            System.out.print("See tasks(1), New task(2), Delete task(3), Export schedule to file(4), Import schedule from file(5), Exit(6): ");
            String choice = "";

            while (choice.equals("")) {
                choice = sc.nextLine();
                System.out.println();
                switch (choice) {
                    case "1":
                        printAllTasks(false);
                        break;
                    case "2":
                        newTask();
                        break;
                    case "3":
                        deleteTask();
                        break;
                    case "4":
                        exportToFile();
                        break;
                    case "5":
                        importFromFile();
                        break;
                    case "6":
                        System.exit(0);
                        break;
                    default:
                        System.out.print("Enter the number corresponding to the action you wish to take: ");
                        choice = "";
                        break;
                }
            }
        }
    }

}
