/**
 * Copyright 2011 Les Syst�mes M�dicaux Imagem Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imagem.gwtpplugin.wizard;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.projectfile.src.shared.Model;

public class NewModelWizard extends Wizard implements INewWizard {

	private NewModelWizardPage page;
	private IStructuredSelection selection;

	public NewModelWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Model");
	}
	
	@Override
	public void addPages() {
		page = new NewModelWizardPage(selection);
		addPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		Model model = null;
		try {
			IPackageFragmentRoot root = page.getPackageFragmentRoot();
			
			model = new Model(root, page.getPackageText(), page.getTypeName());
			model.createConstructor();
			model.createSerializationField();
			
			Field[] modelFields = page.getFields();
			IField[] fields = new IField[modelFields.length];
			for(int i = 0; i < modelFields.length; i++) {
				fields[i] = model.createField(modelFields[i].getType(), modelFields[i].getName());
			}
			for(IField field : fields) {
				model.createSetterMethod(field);
			}
			for(IField field : fields) {
				model.createGetterMethod(field);
			}
			
			if(page.generateEquals()) {
				model.createEqualsMethod(fields);
				model.createHashCodeMethod(fields);
			}
			
			if(model != null) model.commit();
		}
		catch (JavaModelException e) {
			e.printStackTrace();

			try {
				if(model != null) model.discard();
			}
			catch (JavaModelException e1) {	}
			
			return false;
		}
		
		return true;
	}

}