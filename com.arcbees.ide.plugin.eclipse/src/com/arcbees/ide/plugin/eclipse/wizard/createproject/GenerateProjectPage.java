/**
 * Copyright 2013 ArcBees Inc.
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

package com.arcbees.ide.plugin.eclipse.wizard.createproject;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.arcbees.ide.plugin.eclipse.domain.ProjectConfigModel;

public class GenerateProjectPage extends WizardPage {
    private ProjectConfigModel projectConfigModel;

    public GenerateProjectPage(ProjectConfigModel projectConfigModel) {
        super("wizardPageGenerateProject");
        this.projectConfigModel = projectConfigModel;
        
        setTitle("Generate Project");
        setDescription("Confirm project configuration and generate it.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        
    }
    
    private void generate() {
        IProjectConfigurationManager projectConfig = MavenPlugin.getProjectConfigurationManager();
        //projectConfig.createArchetypeProjects(location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor);
        
        System.out.println("test");
    }
}
