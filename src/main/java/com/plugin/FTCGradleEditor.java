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
        addMavenRepository("https://mymaven.bylazar.com/releases");

        // Pedro Pathing dependencies with FTC Dashboard excluded for Sloth compatibility
        addMultiLineDependency("implementation('com.pedropathing:ftc:2.0.4') {",
            "exclude group: \"com.acmerobotics.dashboard\"", "}");
        addMultiLineDependency("implementation('com.pedropathing:telemetry:1.0.0') {",
            "exclude group: \"com.acmerobotics.dashboard\"", "}");
    }

    /**
     * Add Full Panels integration.
     * Repository: https://github.com/BotsBurgh/fullpanels
     */
    public void addFullPanelsIntegration() throws IOException {
        addMavenRepository("https://mymaven.bylazar.com/releases");

        // Full Panels integration (with FTC Dashboard excluded for Sloth compatibility)
        addMultiLineDependency("implementation('com.bylazar:fullpanels:1.0.10') {",
            "exclude group: \"com.acmerobotics.dashboard\"", "}");
        addMultiLineDependency("implementation(\"com.bylazar:camerastream:1.0.0\") {",
            "exclude group: \"com.acmerobotics.dashboard\"", "}");
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

            // Check if repository already exists
            if (line.contains(repoUrl)) {
                alreadyExists = true;
            }
            
            newLines.add(line);

            // Find repositories block and add after opening brace
            if (line.trim().equals("repositories {")) {
                repositoriesFound = true;
                // Look ahead to see what repositories already exist
                boolean needsStandardRepos = true;
                for (int j = i + 1; j < lines.size() && !lines.get(j).trim().equals("}"); j++) {
                    String repoLine = lines.get(j).trim();
                    if (repoLine.contains("mavenCentral()") || repoLine.contains("google()")) {
                        needsStandardRepos = false;
                        break;
                    }
                }

                // Add standard repositories if they don't exist
                if (needsStandardRepos) {
                    newLines.add("    mavenCentral()");
                    newLines.add("    google() // Needed for androidx");
                }

                // Add the custom repository
                if (!alreadyExists) {
                    String repoComment = getRepositoryComment(repoUrl);
                    if (!repoComment.isEmpty()) {
                        newLines.add("    " + repoComment);
                    }
                    newLines.add("    maven { url = \"" + repoUrl + "\" }");
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
                    newLines.add("    mavenCentral()");
                    newLines.add("    google() // Needed for androidx");
                    String repoComment = getRepositoryComment(repoUrl);
                    if (!repoComment.isEmpty()) {
                        newLines.add("    " + repoComment);
                    }
                    newLines.add("    maven { url = \"" + repoUrl + "\" }");
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
        
        if (!alreadyExists || !repositoriesFound) {
            Files.write(teamCodeBuildGradle.toPath(), newLines);
        }
    }
    
    /**
     * Get a comment for a repository URL.
     */
    private String getRepositoryComment(String repoUrl) {
        if (repoUrl.contains("mymaven.bylazar.com")) {
            return "// Pedro Pathing";
        } else if (repoUrl.contains("maven.brott.dev")) {
            return "// FTC Dashboard / Road Runner";
        } else if (repoUrl.contains("jitpack.io")) {
            return "// Sloth";
        }
        return "";
    }

    /**
     * Add a dependency to the build.gradle file.
     */
    private void addDependency(String dependency) throws IOException {
        if (!teamCodeBuildGradle.exists()) {
            throw new FileNotFoundException("TeamCode/build.gradle not found");
        }
        
        List<String> lines = Files.readAllLines(teamCodeBuildGradle.toPath());
        
        // Extract the main dependency name for duplicate checking
        String dependencyName = extractDependencyName(dependency);

        // Check if dependency already exists
        for (String line : lines) {
            if (line.contains(dependencyName)) {
                return; // Already exists
            }
        }
        
        List<String> newLines = new ArrayList<>();
        boolean added = false;
        
        for (String line : lines) {
            newLines.add(line);
            
            // Add dependency inside dependencies block
            if (!added && line.trim().equals("dependencies {")) {
                // Handle multi-line dependencies
                if (dependency.contains("\n")) {
                    String[] dependencyLines = dependency.split("\n");
                    for (int i = 0; i < dependencyLines.length; i++) {
                        newLines.add("    " + dependencyLines[i]);
                    }
                } else {
                    newLines.add("    " + dependency);
                }
                added = true;
            }
        }

        if (added) {
            Files.write(teamCodeBuildGradle.toPath(), newLines);
        }
    }

    /**
     * Add a multi-line dependency with proper formatting.
     */
    private void addMultiLineDependency(String firstLine, String... additionalLines) throws IOException {
        if (!teamCodeBuildGradle.exists()) {
            throw new FileNotFoundException("TeamCode/build.gradle not found");
        }

        List<String> lines = Files.readAllLines(teamCodeBuildGradle.toPath());

        // Extract the main dependency name for duplicate checking
        String dependencyName = extractDependencyName(firstLine);

        // Check if dependency already exists
        for (String line : lines) {
            if (line.contains(dependencyName)) {
                return; // Already exists
            }
        }

        List<String> newLines = new ArrayList<>();
        boolean added = false;

        for (String line : lines) {
            newLines.add(line);

            // Add dependency inside dependencies block
            if (!added && line.trim().equals("dependencies {")) {
                // Add the first line
                newLines.add("    " + firstLine);

                // Add additional lines with proper indentation
                for (String additionalLine : additionalLines) {
                    newLines.add("        " + additionalLine);
                }

                added = true;
            }
        }

        if (added) {
            Files.write(teamCodeBuildGradle.toPath(), newLines);
        }
    }

    /**
     * Extract the main dependency name from a dependency string for duplicate checking.
     */
    private String extractDependencyName(String dependency) {
        // Extract the group:artifact part from implementation('group:artifact:version')
        if (dependency.contains("'")) {
            int start = dependency.indexOf("'") + 1;
            int end = dependency.indexOf("'", start);
            if (end > start) {
                String fullName = dependency.substring(start, end);
                // Return just group:artifact (without version)
                int versionIndex = fullName.lastIndexOf(":");
                if (versionIndex > 0) {
                    return fullName.substring(0, versionIndex);
                }
                return fullName;
            }
        } else if (dependency.contains("\"")) {
            int start = dependency.indexOf("\"") + 1;
            int end = dependency.indexOf("\"", start);
            if (end > start) {
                String fullName = dependency.substring(start, end);
                // Return just group:artifact (without version)
                int versionIndex = fullName.lastIndexOf(":");
                if (versionIndex > 0) {
                    return fullName.substring(0, versionIndex);
                }
                return fullName;
            }
        }
        return dependency;
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
