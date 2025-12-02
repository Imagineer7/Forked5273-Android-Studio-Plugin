package com.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * Action to initiate the FTC Project Wizard.
 * Creates a new FTC Robot Controller project by cloning the SDK and setting up TeamCode.
 */
public class FTCProjectWizardAction extends AnAction {
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        
        FTCProjectWizardDialog dialog = new FTCProjectWizardDialog(project);
        boolean accepted = dialog.showAndGet();
        
        if (!accepted) {
            return;
        }
        
        // Execute the wizard workflow
        FTCProjectWizardExecutor executor = new FTCProjectWizardExecutor(project, dialog);
        executor.execute();
    }
}
