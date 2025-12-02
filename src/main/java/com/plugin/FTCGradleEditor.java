package com.plugin;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for editing Gradle build files to add FTC integrations.
 * Uses simple text manipulation to preserve formatting and comments.
 */
public class FTCGradleEditor {
    private final File projectDir;
    private final File teamCodeBuildGradle;
    
    public FTCGradleEditor(File projectDir) {
        this.projectDir = projectDir;
        this.teamCodeBuildGradle = new File(projectDir, "TeamCode/build.gradle");
    }
    
    /**
     * Add FTC Dashboard integration.
     * Repository: https://github.com/acmerobotics/ftc-dashboard
     */
    public void addDashboardIntegration() throws IOException {
        addMavenRepository("https://maven.brott.dev/");
        addDependency("implementation 'com.acmerobotics.dashboard:dashboard:0.4.16'");
    }
    
    /**
     * Add Road Runner Quick Start integration.
     * Repository: https://github.com/acmerobotics/road-runner-quickstart
     */
    public void addRoadRunnerIntegration() throws IOException {
        addMavenRepository("https://maven.brott.dev/");
        addDependency("implementation 'com.acmerobotics.roadrunner:core:0.5.6'");
        addDependency("implementation 'com.acmerobotics.dashboard:dashboard:0.4.16'");
    }
    
    /**
     * Add FTCLib integration.
     * Repository: https://github.com/FTCLib/FTCLib
     */
    public void addFTCLibIntegration() throws IOException {
        addDependency("implementation 'org.ftclib.ftclib:core:2.1.1'");
    }
    
    /**
     * Add Pedro Pathing integration.
     * Repository: https://github.com/pedropathing/pedroPathing
     */
    public void addPedroPathingIntegration() throws IOException {
        // Pedro Pathing is typically added as source files
        addComment("// Pedro Pathing: Add pedro pathing source files to your TeamCode folder");
    }
    
    /**
     * Add Sloth hot reload integration.
     * Repository: https://github.com/Dairy-Foundation/Sloth
     */
    public void addSlothIntegration() throws IOException {
        addMavenRepository("https://jitpack.io");
        addDependency("implementation 'com.github.Dairy-Foundation:Sloth:1.0.0'");
    }
    
    /**
     * Add a Maven repository to the build.gradle file.
     */
    private void addMavenRepository(String repoUrl) throws IOException {
        if (!teamCodeBuildGradle.exists()) {
            throw new FileNotFoundException("TeamCode/build.gradle not found at: " + teamCodeBuildGradle.getAbsolutePath());
        }
        
        List<String> lines = Files.readAllLines(teamCodeBuildGradle.toPath());
        List<String> newLines = new ArrayList<>();
        
        boolean repositoriesFound = false;
        boolean alreadyExists = false;
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            newLines.add(line);
            
            // Check if repository already exists
            if (line.contains(repoUrl)) {
                alreadyExists = true;
            }
            
            // Find repositories block and add after opening brace
            if (line.trim().equals("repositories {")) {
                repositoriesFound = true;
                if (!alreadyExists) {
                    // Add the repository URL
                    newLines.add("    maven { url '" + repoUrl + "' }");
                }
            }
        }
        
        // If repositories block not found, add it before dependencies
        if (!repositoriesFound && !alreadyExists) {
            newLines = new ArrayList<>();
            boolean added = false;
            for (String line : lines) {
                if (!added && line.trim().equals("dependencies {")) {
                    newLines.add("repositories {");
                    newLines.add("    maven { url '" + repoUrl + "' }");
                    newLines.add("}");
                    newLines.add("");
                    added = true;
                }
                newLines.add(line);
            }
            
            // If still not added, it means dependencies block wasn't found either
            if (!added) {
                throw new IOException("Could not find a suitable place to add repository in build.gradle");
            }
        }
        
        if (!alreadyExists) {
            Files.write(teamCodeBuildGradle.toPath(), newLines);
        }
    }
    
    /**
     * Add a dependency to the build.gradle file.
     */
    private void addDependency(String dependency) throws IOException {
        if (!teamCodeBuildGradle.exists()) {
            throw new FileNotFoundException("TeamCode/build.gradle not found");
        }
        
        List<String> lines = Files.readAllLines(teamCodeBuildGradle.toPath());
        
        // Check if dependency already exists
        for (String line : lines) {
            if (line.trim().equals(dependency)) {
                return; // Already exists
            }
        }
        
        List<String> newLines = new ArrayList<>();
        boolean added = false;
        
        for (String line : lines) {
            newLines.add(line);
            
            // Add dependency inside dependencies block
            if (!added && line.trim().equals("dependencies {")) {
                newLines.add("    " + dependency);
                added = true;
            }
        }
        
        if (added) {
            Files.write(teamCodeBuildGradle.toPath(), newLines);
        }
    }
    
    /**
     * Add a comment to the build.gradle file.
     */
    private void addComment(String comment) throws IOException {
        if (!teamCodeBuildGradle.exists()) {
            throw new FileNotFoundException("TeamCode/build.gradle not found");
        }
        
        List<String> lines = Files.readAllLines(teamCodeBuildGradle.toPath());
        
        // Check if comment already exists
        for (String line : lines) {
            if (line.trim().equals(comment.trim())) {
                return; // Already exists
            }
        }
        
        // Add comment at the end
        lines.add("");
        lines.add(comment);
        
        Files.write(teamCodeBuildGradle.toPath(), lines);
    }
}
