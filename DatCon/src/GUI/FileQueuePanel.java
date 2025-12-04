package src.GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;

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
        setBackground(new Color(248, 248, 250));
        setBorder(new LineBorder(new Color(216, 220, 226), 1, true));

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.setCellRenderer(new JobRenderer());
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
        addButton.setMargin(new java.awt.Insets(6, 10, 6, 10));

        gbc.gridx = 1;
        add(removeButton, gbc);
        removeButton.addActionListener(this);
        removeButton.setMargin(new java.awt.Insets(6, 10, 6, 10));

        gbc.gridx = 2;
        add(clearButton, gbc);
        clearButton.addActionListener(this);
        clearButton.setMargin(new java.awt.Insets(6, 10, 6, 10));
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

    private static class JobRenderer extends JLabel
            implements ListCellRenderer<DatJob> {
        private static final long serialVersionUID = 1L;
        private static final Color READY = new Color(35, 179, 119);
        private static final Color PROCESSING = new Color(32, 129, 226);
        private static final Color ERROR = new Color(200, 65, 65);
        private static final Color ANALYZING = new Color(242, 158, 36);
        private static final Color PENDING = new Color(120, 120, 120);

        JobRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(4, 6, 4, 6));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DatJob> list,
                DatJob value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null) {
                setText("");
                return this;
            }
            String status = value.getStatus().toString();
            if (value.hasError() && value.getErrorMessage().length() > 0) {
                status += " - " + value.getErrorMessage();
            }
            setText(value.getDisplayName() + "  •  " + status);
            Color fg = PENDING;
            switch (value.getStatus()) {
            case READY:
            case DONE:
                fg = READY;
                break;
            case PROCESSING:
                fg = PROCESSING;
                break;
            case ERROR:
                fg = ERROR;
                break;
            case ANALYZING:
                fg = ANALYZING;
                break;
            default:
                break;
            }
            setForeground(fg.darker());
            if (isSelected) {
                setBackground(new Color(230, 240, 255));
                setBorder(new CompoundBorder(
                        new LineBorder(new Color(180, 200, 240), 1, true),
                        new EmptyBorder(4, 6, 4, 6)));
            } else {
                setBackground(Color.WHITE);
                setBorder(new EmptyBorder(4, 6, 4, 6));
            }
            return this;
        }
    }
}
