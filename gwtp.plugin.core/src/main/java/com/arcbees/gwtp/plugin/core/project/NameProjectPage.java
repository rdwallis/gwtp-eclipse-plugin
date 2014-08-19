/**
 * Copyright 2014 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.gwtp.plugin.core.project;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class NameProjectPage extends WizardPage {
    private static final  NameProjectPage INSTANCE = new NameProjectPage();

    private class Info {
        String projectName;
        String packageName;
        String mavenArtifactId;
        String mavenGroup;
        String projectLocation;
    };

    private final Info info = new Info();
    private Text projectName;
    private Text packageInput;
    private Text mavenArtifactId;
    private Text mavenGroup;
    private boolean advancedInputEnabled;
    private Text projectLocation;

    private String lastAutoGeneratedLocation = "";

    private DirectoryDialog directoryDialog;

    private NameProjectPage() {
        super("Create a GWTP Project", "Create a GWTP Project", null);
    }

    static NameProjectPage get() {
        return INSTANCE;
    }

    private void autoPopulateAdvancedInputs() {
        if (!advancedInputEnabled) {
            mavenArtifactId.setText(projectName.getText());
            mavenGroup.setText(packageInput.getText());
        }
        if (projectLocation.getText().equals(lastAutoGeneratedLocation)) {
            projectLocation.setText(ResourcesPlugin.getWorkspace()
                    .getRoot().getLocation().toString() + "/" + projectName.getText());
            lastAutoGeneratedLocation = projectLocation.getText();
        }
        info.mavenArtifactId = mavenArtifactId.getText();
        info.mavenGroup = mavenGroup.getText();
        info.projectName = projectName.getText();
        info.packageName = packageInput.getText();
        info.projectLocation = projectLocation.getText();
    }

    private Button createCheckButton(final Composite container, final String labelText) {
        final Button checkButton = new Button(container, SWT.CHECK);
        checkButton.setText(labelText);
        checkButton.setSelection(true);

        final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        checkButton.setLayoutData(data);
        return checkButton;
    }

    @Override
    public void createControl(final Composite parent) {

        final Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        final TabFolder tabFolder = new TabFolder(container, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        final Composite simpleTabBody = createTab(tabFolder, "Simple Setup");
        final Composite advancedTabBody = createTab(tabFolder, "Advanced");

        final ModifyListener modifyListener = new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                autoPopulateAdvancedInputs();
            }

        };

        projectName = createTextInput(simpleTabBody, "Project Name", "MyProject", modifyListener);
        packageInput = createTextInput(simpleTabBody, "Project Package", "com.example.myproject", modifyListener);

        final Button autoPopulateButton = createCheckButton(advancedTabBody, "Auto Populate");
        autoPopulateButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                setAdvancedInputsEnabled(!autoPopulateButton.getSelection());
            }

        });

        mavenArtifactId = createTextInput(advancedTabBody, "Maven Artifact Id", "MyProject", null);
        mavenGroup = createTextInput(advancedTabBody, "Maven Group", "com.example.myproject", null);

        final Group grpLocation = new Group(container, SWT.NONE);
        grpLocation.setLayout(new GridLayout(2, false));
        grpLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        grpLocation.setText("Project Location");

        projectLocation = new Text(grpLocation, SWT.BORDER);
        projectLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        projectLocation.setText(lastAutoGeneratedLocation);

        final Button openFolderButton = new Button(grpLocation, SWT.NONE);
        openFolderButton.setText("Open");

        openFolderButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                showFolderDialog();
            }
        });

        setAdvancedInputsEnabled(false);
    }

    private Composite createTab(final TabFolder tabFolder, final String name) {
        return createTabBody(tabFolder, createTabItem(tabFolder, name));
    }

    private Composite createTabBody(final TabFolder tabFolder, final TabItem tabItem) {
        final Composite tabBody = new Composite(tabFolder, SWT.NONE);
        tabBody.setLayout(getTabBodyLayout());
        tabItem.setControl(tabBody);
        return tabBody;
    }

    private TabItem createTabItem(final TabFolder tabFolder, final String name) {
        final TabItem tab = new TabItem(tabFolder, SWT.NONE);
        tab.setText(name);
        return tab;
    }

    private Text createTextInput(final Composite tab, final String labelText, final String defaultValue,
            final ModifyListener modifyListener) {
        final Label label = new Label(tab, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setText(labelText + ":");

        final Text input = new Text(tab, SWT.BORDER);
        input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        input.setText(defaultValue);
        if (modifyListener != null) {
            input.addModifyListener(modifyListener);
        }

        return input;
    }

    IPath getLocation() {
        return new Path(info.projectLocation);
    }

    String getMavenArtifactId() {
        return info.mavenArtifactId;
    }

    String getMavenGroup() {
        return info.mavenGroup;
    }

    String getPackageName() {
        return info.packageName;
    }

    String getProjectName() {
        return info.projectName;
    }

    private Layout getTabBodyLayout() {
        final GridLayout tabBodyLayout = new GridLayout(2, false);
        tabBodyLayout.marginTop = 10;
        tabBodyLayout.marginBottom = 10;
        tabBodyLayout.verticalSpacing = 20;
        return tabBodyLayout;
    }

    private void setAdvancedInputsEnabled(final boolean enabled) {
        this.advancedInputEnabled = enabled;
        mavenGroup.setEnabled(enabled);
        mavenArtifactId.setEnabled(enabled);
        autoPopulateAdvancedInputs();
    }

    private void showFolderDialog() {
        if (directoryDialog == null) {
            directoryDialog = new DirectoryDialog(getShell());
        }

        final String selectedLocation = directoryDialog.open();
        if (selectedLocation != null) {
            projectLocation.setText(selectedLocation);
        }
    }
}
