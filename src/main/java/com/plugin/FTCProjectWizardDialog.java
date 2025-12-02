package com.plugin;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Multi-step dialog for FTC Project Initiation Wizard.
 * Collects user input for cloning FTC SDK and configuring the project.
 */
public class FTCProjectWizardDialog extends DialogWrapper {
    private final Project project;
    
    // Step 1: Project Location
    public final TextFieldWithBrowseButton projectLocationField;
    public final JTextField teamNumberField;
    
    // Step 2: Clone Options
    public final JRadioButton cloneFullButton;
    public final JRadioButton cloneShallowButton;
    public final ButtonGroup cloneGroup;
    public final JTextField branchField;
    
    // Step 3: Integrations
    public final JBCheckBox ftcDashboardCheck;
    public final JBCheckBox roadRunnerCheck;
    public final JBCheckBox ftcLibCheck;
    public final JBCheckBox pedroPathingCheck;
    public final JBCheckBox slothCheck;
    
    // Step 4: Starter Code
    public final JBCheckBox generateSampleOpModeCheck;
    public final JBCheckBox generateHardwareClassCheck;
    public final JRadioButton javaLanguageButton;
    public final JRadioButton kotlinLanguageButton;
    public final ButtonGroup languageGroup;
    
    public FTCProjectWizardDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        
        // Initialize Step 1 fields
        projectLocationField = new TextFieldWithBrowseButton();
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select Project Location");
        projectLocationField.addBrowseFolderListener(
            "Select Project Location",
            "Choose where to create the FTC project",
            project,
            descriptor
        );
        
        // Set default location
        String userHome = System.getProperty("user.home");
        projectLocationField.setText(userHome + File.separator + "FtcRobotController");
        
        teamNumberField = new JTextField("5273");
        teamNumberField.setPreferredSize(new Dimension(100, 30));
        
        // Initialize Step 2 fields
        cloneFullButton = new JRadioButton("Full clone (includes history)", true);
        cloneShallowButton = new JRadioButton("Shallow clone (faster, no history)");
        cloneGroup = new ButtonGroup();
        cloneGroup.add(cloneFullButton);
        cloneGroup.add(cloneShallowButton);
        
        branchField = new JTextField("master");
        branchField.setPreferredSize(new Dimension(150, 30));
        
        // Initialize Step 3 fields
        ftcDashboardCheck = new JBCheckBox("FTC Dashboard (real-time telemetry)", false);
        roadRunnerCheck = new JBCheckBox("Road Runner Quick Start (advanced path following)", false);
        ftcLibCheck = new JBCheckBox("FTCLib (command-based framework)", false);
        pedroPathingCheck = new JBCheckBox("Pedro Pathing (autonomous paths)", false);
        slothCheck = new JBCheckBox("Sloth (hot reload for faster iteration)", false);
        
        // Initialize Step 4 fields
        generateSampleOpModeCheck = new JBCheckBox("Generate sample OpMode", true);
        generateHardwareClassCheck = new JBCheckBox("Generate hardware configuration class", true);
        javaLanguageButton = new JRadioButton("Java", true);
        kotlinLanguageButton = new JRadioButton("Kotlin");
        languageGroup = new ButtonGroup();
        languageGroup.add(javaLanguageButton);
        languageGroup.add(kotlinLanguageButton);
        
        setTitle("FTC Project Initiation Wizard");
        init();
    }
    
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create tabbed pane for multi-step wizard
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Step 1: Project Setup
        JPanel step1Panel = createStep1Panel();
        tabbedPane.addTab("1. Project Setup", step1Panel);
        
        // Step 2: Clone Options
        JPanel step2Panel = createStep2Panel();
        tabbedPane.addTab("2. Clone Options", step2Panel);
        
        // Step 3: Integrations
        JPanel step3Panel = createStep3Panel();
        tabbedPane.addTab("3. Integrations", step3Panel);
        
        // Step 4: Starter Code
        JPanel step4Panel = createStep4Panel();
        tabbedPane.addTab("4. Starter Code", step4Panel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.setPreferredSize(new Dimension(600, 500));
        
        return mainPanel;
    }
    
    private JPanel createStep1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Welcome text
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel welcomeLabel = new JLabel("<html><h2>Welcome to FTC Project Wizard</h2>" +
            "<p>This wizard will help you create a new FTC Robot Controller project.</p></html>");
        panel.add(welcomeLabel, gbc);
        
        // Project location
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Project Location:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(projectLocationField, gbc);
        
        // Team number
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Team Number (optional):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(teamNumberField, gbc);
        
        // Filler
        gbc.gridy = 3;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createStep2Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("<html><h3>Clone Options</h3>" +
            "<p>Configure how to clone the FTC Robot Controller SDK.</p></html>");
        panel.add(titleLabel, gbc);
        
        // Clone type
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Clone Type:"), gbc);
        
        JPanel clonePanel = new JPanel(new GridLayout(2, 1));
        clonePanel.add(cloneFullButton);
        clonePanel.add(cloneShallowButton);
        gbc.gridx = 1;
        panel.add(clonePanel, gbc);
        
        // Branch
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Branch:"), gbc);
        
        gbc.gridx = 1;
        panel.add(branchField, gbc);
        
        // Info
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><p><i>Note: The SDK will be cloned from:<br>" +
            "https://github.com/FIRST-Tech-Challenge/FtcRobotController</i></p></html>");
        panel.add(infoLabel, gbc);
        
        // Filler
        gbc.gridy = 4;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createStep3Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("<html><h3>Integrations</h3>" +
            "<p>Select optional libraries and tools to integrate:</p></html>");
        panel.add(titleLabel, gbc);
        
        // Checkboxes
        gbc.gridy = 1;
        panel.add(ftcDashboardCheck, gbc);
        
        gbc.gridy = 2;
        panel.add(roadRunnerCheck, gbc);
        
        gbc.gridy = 3;
        panel.add(ftcLibCheck, gbc);
        
        gbc.gridy = 4;
        panel.add(pedroPathingCheck, gbc);
        
        gbc.gridy = 5;
        panel.add(slothCheck, gbc);
        
        // Filler
        gbc.gridy = 6;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createStep4Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("<html><h3>Starter Code</h3>" +
            "<p>Generate sample code to get started quickly:</p></html>");
        panel.add(titleLabel, gbc);
        
        // Generate options
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(generateSampleOpModeCheck, gbc);
        
        gbc.gridy = 2;
        panel.add(generateHardwareClassCheck, gbc);
        
        // Language
        gbc.gridy = 3;
        panel.add(new JLabel("Language:"), gbc);
        
        JPanel languagePanel = new JPanel(new GridLayout(1, 2));
        languagePanel.add(javaLanguageButton);
        languagePanel.add(kotlinLanguageButton);
        gbc.gridx = 1;
        panel.add(languagePanel, gbc);
        
        // Filler
        gbc.gridy = 4;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
}
