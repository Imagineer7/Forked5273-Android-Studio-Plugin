package com.plugin;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Executor for FTC Project Wizard workflow.
 * Handles cloning, configuration, and code generation.
 */
public class FTCProjectWizardExecutor {
    private final Project project;
    private final FTCProjectWizardDialog dialog;
    
    public FTCProjectWizardExecutor(Project project, FTCProjectWizardDialog dialog) {
        this.project = project;
        this.dialog = dialog;
    }
    
    public void execute() {
        // Validate inputs
        String projectLocation = dialog.projectLocationField.getText();
        if (projectLocation.isEmpty()) {
            Messages.showErrorDialog(project, "Project location is required.", "Invalid Input");
            return;
        }
        
        File projectDir = new File(projectLocation);
        if (projectDir.exists()) {
            int result = Messages.showYesNoDialog(
                project,
                "Directory already exists. Do you want to continue?",
                "Directory Exists",
                Messages.getQuestionIcon()
            );
            if (result != Messages.YES) {
                return;
            }
        }
        
        // Run the setup in a background task
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Setting up FTC Project", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setText("Cloning FTC Robot Controller SDK...");
                    indicator.setIndeterminate(true);
                    
                    // Step 1: Clone the repository
                    cloneRepository(projectDir, indicator);
                    
                    if (indicator.isCanceled()) return;
                    
                    // Step 2: Configure integrations
                    indicator.setText("Configuring integrations...");
                    configureIntegrations(projectDir);
                    
                    if (indicator.isCanceled()) return;
                    
                    // Step 3: Generate starter code
                    indicator.setText("Generating starter code...");
                    generateStarterCode(projectDir);
                    
                    // Step 4: Show success message
                    indicator.setText("Project setup complete!");
                    Messages.showInfoMessage(
                        project,
                        "FTC project has been created successfully at: " + projectLocation,
                        "Success"
                    );
                    
                } catch (Exception e) {
                    Messages.showErrorDialog(
                        project,
                        "Failed to set up project: " + e.getMessage(),
                        "Setup Failed"
                    );
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void cloneRepository(File projectDir, ProgressIndicator indicator) throws Exception {
        String branch = dialog.branchField.getText();
        boolean isShallow = dialog.cloneShallowButton.isSelected();
        
        FTCGitCloner cloner = new FTCGitCloner();
        cloner.cloneRepository(projectDir, branch, isShallow, indicator);
        
        // Validate the clone
        File settingsGradle = new File(projectDir, "settings.gradle");
        if (!settingsGradle.exists()) {
            throw new Exception("Clone validation failed: settings.gradle not found");
        }
    }
    
    private void configureIntegrations(File projectDir) throws Exception {
        FTCGradleEditor gradleEditor = new FTCGradleEditor(projectDir);
        
        if (dialog.ftcDashboardCheck.isSelected()) {
            gradleEditor.addDashboardIntegration();
        }
        
        if (dialog.roadRunnerCheck.isSelected()) {
            gradleEditor.addRoadRunnerIntegration();
        }
        
        if (dialog.ftcLibCheck.isSelected()) {
            gradleEditor.addFTCLibIntegration();
        }
        
        if (dialog.pedroPathingCheck.isSelected()) {
            gradleEditor.addPedroPathingIntegration();
        }
        
        if (dialog.slothCheck.isSelected()) {
            gradleEditor.addSlothIntegration();
        }
    }
    
    private void generateStarterCode(File projectDir) throws Exception {
        if (!dialog.generateSampleOpModeCheck.isSelected() && 
            !dialog.generateHardwareClassCheck.isSelected()) {
            return;
        }
        
        boolean isJava = dialog.javaLanguageButton.isSelected();
        String teamNumber = dialog.teamNumberField.getText();
        
        FTCStarterCodeGenerator generator = new FTCStarterCodeGenerator(projectDir, isJava, teamNumber);
        
        if (dialog.generateSampleOpModeCheck.isSelected()) {
            generator.generateSampleOpMode();
        }
        
        if (dialog.generateHardwareClassCheck.isSelected()) {
            generator.generateHardwareClass();
        }
    }
}
