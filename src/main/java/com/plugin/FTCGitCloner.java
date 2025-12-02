package com.plugin;

import com.intellij.openapi.progress.ProgressIndicator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;

import java.io.File;

/**
 * Utility class for cloning the FTC Robot Controller repository using JGit.
 */
public class FTCGitCloner {
    private static final String FTC_SDK_URL = "https://github.com/FIRST-Tech-Challenge/FtcRobotController.git";
    
    /**
     * Clone the FTC Robot Controller repository.
     * 
     * @param targetDir Directory where to clone the repository
     * @param branch Branch to clone (e.g., "master")
     * @param shallow Whether to perform a shallow clone
     * @param indicator Progress indicator for UI updates
     * @throws GitAPIException If cloning fails
     */
    public void cloneRepository(File targetDir, String branch, boolean shallow, ProgressIndicator indicator) 
            throws GitAPIException {
        
        // Validate input
        if (branch == null || branch.trim().isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be empty");
        }
        
        // Create parent directory if it doesn't exist
        File parentDir = targetDir.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new IllegalStateException("Failed to create parent directory: " + parentDir);
            }
        }
        
        // Check if target directory already exists and is not empty
        if (targetDir.exists() && targetDir.list() != null && targetDir.list().length > 0) {
            throw new IllegalStateException("Target directory is not empty: " + targetDir);
        }
        
        // Set up JGit progress monitor that updates IntelliJ progress indicator
        ProgressMonitor progressMonitor = new ProgressMonitor() {
            private String taskName = "";
            private int totalWork = 0;
            private int completed = 0;
            
            @Override
            public void start(int totalTasks) {
                indicator.setIndeterminate(false);
            }
            
            @Override
            public void beginTask(String title, int totalWork) {
                this.taskName = title;
                this.totalWork = totalWork;
                this.completed = 0;
                indicator.setText("Cloning: " + title);
                indicator.setFraction(0.0);
            }
            
            @Override
            public void update(int completed) {
                this.completed += completed;
                if (totalWork > 0) {
                    double fraction = (double) this.completed / totalWork;
                    indicator.setFraction(fraction);
                }
                indicator.setText("Cloning: " + taskName + " (" + this.completed + "/" + totalWork + ")");
            }
            
            @Override
            public void endTask() {
                indicator.setFraction(1.0);
            }
            
            @Override
            public boolean isCancelled() {
                return indicator.isCanceled();
            }
            
            @Override
            public void showDuration(boolean enabled) {
                // Not used
            }
        };
        
        // Perform the clone operation
        Git.cloneRepository()
            .setURI(FTC_SDK_URL)
            .setDirectory(targetDir)
            .setBranch(branch)
            .setCloneAllBranches(false)
            .setDepth(shallow ? 1 : Integer.MAX_VALUE)
            .setProgressMonitor(progressMonitor)
            .call();
    }
}
