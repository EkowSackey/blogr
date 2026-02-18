package org.example.blogr.Utils;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRunner {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static <T> void run(Task<T> task) {
        executor.submit(task);
    }
}
