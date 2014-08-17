package com.arcbees.gwtp.plugin.core.project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.arcbees.gwtp.plugin.core.project.features.Feature;
import com.arcbees.gwtp.plugin.core.project.features.FeatureConfigOption;
import com.arcbees.gwtp.plugin.core.project.features.Node;

public class FeatureConfigPage extends WizardPage {

    static FeatureConfigPage get() {
        return INSTANCE;
    }

    private Map<Feature, Group> featureGroups = new HashMap<>();

    private Set<FeatureConfigOption> configOptions = new HashSet<>();

    private static FeatureConfigPage INSTANCE = new FeatureConfigPage();

    private FeatureConfigPage() {
        super("Configure Your Features", "Configure Your Features", null);
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NULL);
        setControl(container);
        container.setLayout(new GridLayout(1, false));

        createFeatureConfigWidgets(container, Feature.getFeatures().getChildren());
    }

    private void createFeatureConfigWidgets(final Composite container, final List<Node<Feature>> children) {
        for (final Node<Feature> node : children) {
            createGroup(container, node.getData());
            createFeatureConfigWidgets(container, node.getChildren());
        }

    }

    private void createGroup(final Composite container, final Feature feature) {
        if (feature.hasConfigOptions()) {
            final Group group = new Group(container, SWT.NONE);
            group.setLayout(new GridLayout(2, false));
            group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
            group.setText(feature.getTitle());
            featureGroups.put(feature, group);

            for (final FeatureConfigOption option : feature.getConfigOptions()) {
                createTextInput(group, option);
            }
        }
    }

    private void createTextInput(final Composite composite, final FeatureConfigOption option) {
        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setText(option.getName() + ":");

        final Text input = new Text(composite, SWT.BORDER);
        input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        input.setText(option.getValue());

        input.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                option.setValue(input.getText());

            }
        });

        configOptions.add(option);

    }

    public void fillContext(final Map<String, Object> context) {
        for (final FeatureConfigOption option : configOptions) {
            context.put(option.getName(), option.getValue());
        }

    }

}
