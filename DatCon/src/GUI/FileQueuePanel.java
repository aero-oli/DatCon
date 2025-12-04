package src.GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import src.Files.DatJob;
import src.apps.DatCon;

/**
 * Small panel that shows the batch of .DAT files to process and lets the user
 * add/remove items. Selecting a file makes it the active one for configuring
 * timing/offset settings.
 */
public class FileQueuePanel extends JPanel
        implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;

    private final DatCon datCon;
    private final DefaultListModel<DatJob> model;
    private final JList<DatJob> list;
    private final JButton addButton = new JButton("Add .DAT files");
    private final JButton removeButton = new JButton("Remove Selected");
    private final JButton clearButton = new JButton("Clear");

    public FileQueuePanel(DatCon datCon, DefaultListModel<DatJob> model) {
        this.datCon = datCon;
        this.model = model;
        setLayout(new GridBagLayout());

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(400, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        add(addButton, gbc);
        addButton.addActionListener(this);

        gbc.gridx = 1;
        add(removeButton, gbc);
        removeButton.addActionListener(this);

        gbc.gridx = 2;
        add(clearButton, gbc);
        clearButton.addActionListener(this);
    }

    public void refresh() {
        list.repaint();
    }

    public void selectJob(DatJob job) {
        if (job == null)
            return;
        list.setSelectedValue(job, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == addButton) {
            datCon.promptForDatFiles();
        } else if (source == removeButton) {
            DatJob selected = list.getSelectedValue();
            if (selected != null) {
                datCon.removeJob(selected);
            }
        } else if (source == clearButton) {
            datCon.clearJobs();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        DatJob selected = list.getSelectedValue();
        datCon.onJobSelected(selected);
    }
}
