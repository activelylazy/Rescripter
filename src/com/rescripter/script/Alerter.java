package com.rescripter.script;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class Alerter {

    private final IWorkbenchWindow window;

    public Alerter(IWorkbenchWindow window) {
        this.window = window;
    }
    
    public void info(String message) {
        MessageDialog.openInformation(
                window.getShell(),
                "EditorTest",
                message);
    }

}
