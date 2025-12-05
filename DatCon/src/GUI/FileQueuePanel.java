package src.GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.DefaultListModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import src.Files.DatFile;
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
    private final JTable table;
    private final JobTableModel tableModel;
    private final TableRowSorter<TableModel> sorter;
    private final JButton addButton = new JButton("Add .DAT files");
    private final JButton removeButton = new JButton("Remove Selected");
    private final JButton clearButton = new JButton("Clear");

    public FileQueuePanel(DatCon datCon, DefaultListModel<DatJob> model) {
        this.datCon = datCon;
        this.model = model;
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 248, 250));
        setBorder(new LineBorder(new Color(216, 220, 226), 1, true));

        tableModel = new JobTableModel(model);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        table.setRowHeight(26);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Double.class,
                new NumericRenderer("#,##0.###"));
        table.setDefaultRenderer(Long.class, new NumericRenderer("#,##0"));
        table.getColumnModel().getColumn(0).setPreferredWidth(140); // File
        table.getColumnModel().getColumn(1).setPreferredWidth(110); // Status
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Lower marker
        table.getColumnModel().getColumn(3).setPreferredWidth(110); // Lower tick
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Upper marker
        table.getColumnModel().getColumn(5).setPreferredWidth(110); // Upper tick
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Offset zero
        table.getColumnModel().getColumn(7).setPreferredWidth(90); // Offset
        table.getColumnModel().getColumn(8).setPreferredWidth(90); // Sample
        table.getColumnModel().getColumn(9).setPreferredWidth(140); // CSV
        table.getColumnModel().getColumn(10).setPreferredWidth(80); // CSV evt
        table.getColumnModel().getColumn(11).setPreferredWidth(90); // Event log
        table.getColumnModel().getColumn(12).setPreferredWidth(90); // Config
        table.getColumnModel().getColumn(13).setPreferredWidth(90); // RecDefs
        table.getColumnModel().getColumn(14).setPreferredWidth(90); // KML
        table.getColumnModel().getColumn(15).setPreferredWidth(100); // HP elev
        table.getColumnModel().getColumn(2).setCellEditor(
                new DefaultCellEditor(new JComboBox<String>(
                        new String[] { "Recording Start", "Motor Start",
                                "GPS Lock" })));
        table.getColumnModel().getColumn(4).setCellEditor(
                new DefaultCellEditor(new JComboBox<String>(
                        new String[] { "Recording Stop", "Motor Stop" })));
        table.getColumnModel().getColumn(6).setCellEditor(
                new DefaultCellEditor(new JComboBox<String>(
                        new String[] { "Recording Start", "Motor Start",
                                "Flight Start" })));
        table.getColumnModel().getColumn(14).setCellEditor(
                new DefaultCellEditor(new JComboBox<String>(
                        new String[] { "None", "Ground", "Profile" })));
        sorter = null; // no sorter to avoid row index mismatch with live updates

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1400, 620));

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
        tableModel.fireTableDataChanged();
    }

    public void selectJob(DatJob job) {
        if (job == null)
            return;
        int idx = model.indexOf(job);
        if (idx >= 0) {
            table.getSelectionModel().setSelectionInterval(idx, idx);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == addButton) {
            datCon.promptForDatFiles();
        } else if (source == removeButton) {
            DatJob selected = getSelectedJob();
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
        int viewRow = table.getSelectedRow();
        DatJob selected = null;
        if (viewRow >= 0 && viewRow < model.size()) {
            selected = model.get(viewRow);
        }
        datCon.onJobSelected(selected);
    }

    private DatJob getSelectedJob() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0 || viewRow >= model.size())
            return null;
        return model.get(viewRow);
    }

    private static class JobTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private final String[] columns = { "File", "Status", "Lower Marker",
                "Lower Tick", "Upper Marker", "Upper Tick", "Offset Zero",
                "Offset", "Sample Rate", "CSV File", "Event CSV", "Event Log",
                "Config Log", "RecDefs", "KML", "HP Elev" };
        private final DefaultListModel<DatJob> backing;

        JobTableModel(DefaultListModel<DatJob> backing) {
            this.backing = backing;
        }

        @Override
        public int getRowCount() {
            return backing.getSize();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 3:
            case 5:
            case 7:
                return Long.class;
            case 8:
                return Integer.class;
            case 10:
            case 11:
            case 12:
            case 13:
                return Boolean.class;
            case 15:
                return Double.class;
            default:
                return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 1; // everything except file + status
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DatJob job = backing.get(rowIndex);
            switch (columnIndex) {
            case 0:
                return job.getDisplayName();
            case 1:
                if (job.hasError() && job.getErrorMessage().length() > 0) {
                    return job.getStatus().toString() + " - "
                            + job.getErrorMessage();
                }
                return job.getStatus().toString();
            case 2:
                return job.getLowerMarker();
            case 3:
                return job.getTickLower();
            case 4:
                return job.getUpperMarker();
            case 5:
                return job.getTickUpper();
            case 6:
                return job.getOffsetMarker();
            case 7:
                return job.getOffset();
            case 8:
                return job.getSampleRate();
            case 9:
                return job.getCsvFileName();
            case 10:
                return job.isCsvEventLog();
            case 11:
                return job.isLogEventEnabled();
            case 12:
                return job.isLogConfigEnabled();
            case 13:
                return job.isLogRecDefsEnabled();
            case 14:
                if (job.isKmlGroundTrack())
                    return "Ground";
                if (job.isKmlProfile())
                    return "Profile";
                return "None";
            case 15:
                return job.getHomePointElevation();
            default:
                return "";
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            DatJob job = backing.get(rowIndex);
            try {
                switch (columnIndex) {
                case 2:
                    job.setLowerMarker(toMarker(aValue, job.getLowerMarker()));
                    applyLowerMarker(job);
                    break;
                case 3:
                    job.setTickLowerUser(asLong(aValue, job.getTickLower()));
                    break;
                case 4:
                    job.setUpperMarker(toMarker(aValue, job.getUpperMarker()));
                    applyUpperMarker(job);
                    break;
                case 5:
                    job.setTickUpperUser(asLong(aValue, job.getTickUpper()));
                    break;
                case 6:
                    job.setOffsetMarker(toMarker(aValue, job.getOffsetMarker()));
                    applyOffsetMarker(job);
                    break;
                case 7:
                    job.setOffsetUser(asLong(aValue, job.getOffset()));
                    break;
                case 8:
                    job.setSampleRate(asInt(aValue, job.getSampleRate()));
                    break;
                case 9:
                    job.setCsvFileName(aValue == null ? "" : aValue.toString());
                    break;
                case 10:
                    job.setCsvEventLog(asBool(aValue, job.isCsvEventLog()));
                    break;
                case 11:
                    job.setLogEventEnabled(asBool(aValue, job.isLogEventEnabled()));
                    break;
                case 12:
                    job.setLogConfigEnabled(
                            asBool(aValue, job.isLogConfigEnabled()));
                    break;
                case 13:
                    job.setLogRecDefsEnabled(
                            asBool(aValue, job.isLogRecDefsEnabled()));
                    break;
                case 14:
                    String mode = aValue == null ? "" : aValue.toString();
                    job.setKmlGroundTrack("Ground".equalsIgnoreCase(mode));
                    job.setKmlProfile("Profile".equalsIgnoreCase(mode));
                    break;
                case 15:
                    job.setHomePointElevation(asDouble(aValue,
                            job.getHomePointElevation()));
                    break;
                default:
                    break;
                }
            } catch (Exception ex) {
            }
        }

        private void applyLowerMarker(DatJob job) {
            DatFile df = job.getDatFile();
            if (df == null)
                return;
            String marker = job.getLowerMarker();
            if ("Recording Start".equalsIgnoreCase(marker)) {
                job.setTickLower(df.lowestTickNo);
            } else if ("Motor Start".equalsIgnoreCase(marker)
                    && df.firstMotorStartTick != 0) {
                job.setTickLower(df.firstMotorStartTick);
            } else if ("GPS Lock".equalsIgnoreCase(marker)
                    && df.gpsLockTick != -1) {
                job.setTickLower(df.gpsLockTick);
            }
        }

        private void applyUpperMarker(DatJob job) {
            DatFile df = job.getDatFile();
            if (df == null)
                return;
            String marker = job.getUpperMarker();
            if ("Recording Stop".equalsIgnoreCase(marker)) {
                job.setTickUpper(df.highestTickNo);
            } else if ("Motor Stop".equalsIgnoreCase(marker)
                    && df.lastMotorStopTick != -1) {
                job.setTickUpper(df.lastMotorStopTick);
            }
        }

        private void applyOffsetMarker(DatJob job) {
            DatFile df = job.getDatFile();
            if (df == null)
                return;
            String marker = job.getOffsetMarker();
            if ("Recording Start".equalsIgnoreCase(marker)) {
                job.setOffset(df.lowestTickNo);
            } else if ("Motor Start".equalsIgnoreCase(marker)
                    && df.firstMotorStartTick != 0) {
                job.setOffset(df.firstMotorStartTick);
            } else if ("Flight Start".equalsIgnoreCase(marker)
                    && df.flightStartTick != -1) {
                job.setOffset(df.flightStartTick);
            }
        }

        private String toMarker(Object v, String fallback) {
            if (v == null)
                return fallback;
            return v.toString();
        }

        private long asLong(Object v, long fallback) {
            if (v instanceof Number)
                return ((Number) v).longValue();
            try {
                return Long.parseLong(v.toString());
            } catch (Exception e) {
                return fallback;
            }
        }

        private int asInt(Object v, int fallback) {
            if (v instanceof Number)
                return ((Number) v).intValue();
            try {
                return Integer.parseInt(v.toString());
            } catch (Exception e) {
                return fallback;
            }
        }

        private boolean asBool(Object v, boolean fallback) {
            if (v instanceof Boolean)
                return ((Boolean) v);
            try {
                return Boolean.parseBoolean(v.toString());
            } catch (Exception e) {
                return fallback;
            }
        }

        private double asDouble(Object v, double fallback) {
            if (v instanceof Number)
                return ((Number) v).doubleValue();
            try {
                return Double.parseDouble(v.toString());
            } catch (Exception e) {
                return fallback;
            }
        }
    }

    private static class NumericRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final NumberFormat fmt;

        NumericRenderer(String pattern) {
            this.fmt = new DecimalFormat(pattern);
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            if (value instanceof Number) {
                setText(fmt.format(value));
            } else {
                super.setValue(value);
            }
        }
    }
}
