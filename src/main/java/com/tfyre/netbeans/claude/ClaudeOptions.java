package com.tfyre.netbeans.claude;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbPreferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
@OptionsPanelController.SubRegistration(
        location = "Advanced",
        displayName = "#AdvancedOption_DisplayName_Claude",
        keywords = "#AdvancedOption_Keywords_Claude",
        keywordsCategory = "Advanced/Claude"
)
@org.openide.util.NbBundle.Messages({
    "AdvancedOption_DisplayName_Claude=Claude",
    "AdvancedOption_Keywords_Claude=claude ai",
    "LBL_ApiKey=Claude API Key:",
    "LBL_Model=Claude Model:"
})
public final class ClaudeOptions extends OptionsPanelController {

    private ClaudePanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // Do nothing
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private ClaudePanel getPanel() {
        if (panel == null) {
            panel = new ClaudePanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private static class ClaudePanel extends JPanel {

        private final ClaudeOptions controller;
        private JTextField apiKeyField;
        private JComboBox<ClaudeModel> modelComboBox;

        ClaudePanel(ClaudeOptions controller) {
            this.controller = controller;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            add(new JLabel(Bundle.LBL_ApiKey()), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            apiKeyField = new JTextField(20);
            add(apiKeyField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            add(new JLabel(Bundle.LBL_Model()), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            modelComboBox = new JComboBox<>(ClaudeModel.values());
            add(modelComboBox, gbc);

            apiKeyField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    controller.changed();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    controller.changed();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    controller.changed();
                }
            });

            modelComboBox.addActionListener(e -> controller.changed());
        }

        void load() {
            apiKeyField.setText(NbPreferences.forModule(ClaudeOptions.class).get("ClaudeApiKey", ""));
            String modelApiValue = NbPreferences.forModule(ClaudeOptions.class).get("ClaudeModel", ClaudeModel.CLAUDE_3_OPUS_20240229.getApiValue());
            modelComboBox.setSelectedItem(ClaudeModel.fromApiValue(modelApiValue));
        }

        void store() {
            NbPreferences.forModule(ClaudeOptions.class).put("ClaudeApiKey", apiKeyField.getText().trim());
            NbPreferences.forModule(ClaudeOptions.class).put("ClaudeModel", ((ClaudeModel) modelComboBox.getSelectedItem()).getApiValue());
        }

        boolean valid() {
            return !apiKeyField.getText().trim().isEmpty();
        }
    }
}
