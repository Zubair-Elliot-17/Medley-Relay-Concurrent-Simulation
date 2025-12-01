package medleySimulation;

public class Helper {

    private static boolean thread_complete = false;  // Thread-safe variable should be private

    // Getter method
    public static synchronized boolean isThreadComplete() {
        return thread_complete;
    }

    // Setter method
    public static synchronized void setThreadComplete(boolean complete) {
        thread_complete = complete;
    }
}
