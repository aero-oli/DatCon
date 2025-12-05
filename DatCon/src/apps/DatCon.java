/* DatCon class

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that redistribution of source code include
the following disclaimer in the documentation and/or other materials provided
with the distribution.

THIS SOFTWARE IS PROVIDED BY ITS CREATOR "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE CREATOR OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package src.apps;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Font;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import src.Files.AnalyzeDatResults;
import src.Files.ConvertDat;
import src.Files.DJIAssistantFile;
import src.Files.DatConLog;
import src.Files.DatConPopups;
import src.Files.DatFile;
import src.Files.FileBeingUsed;
import src.Files.Persist;
import src.Files.DatJob;
import src.Files.WorkingDir;
import src.GUI.CheckUpdates;
import src.GUI.CsvPanel;
import src.GUI.FileQueuePanel;
import src.GUI.DatConMenuBar;
import src.GUI.DataModelDialog;
import src.GUI.KMLPanel;
import src.GUI.LogFilesPanel;
import src.GUI.LoggingPanel;
import src.GUI.TimeAxisPanel;

@SuppressWarnings("serial")
public class DatCon extends JPanel
        implements ActionListener, ComponentListener, MouseListener {

    static public final String version = "3.5.0";

    public static JFrame frame = null;

    public DatFile datFile = null;

    private DefaultListModel<DatJob> jobModel = new DefaultListModel<>();

    private FileQueuePanel fileQueuePanel = null;

    private DatJob currentJob = null;

    JPanel contentPanel = null;

    static JFileChooser fc;

    static JFileChooser dc;

    private static FileDialog datFileDialog;

    private static FileDialog dirFileDialog;

    private static final boolean PREFER_NATIVE_DIALOGS = true;

    public static DatCon instance = null;

    Color contentPaneBGColor = null;

    JButton dirViewIt = new JButton("View It");

    public JButton goButton = new JButton("GO!");

    JTextField datFileTextField = new JTextField(
            "Use Add .DAT files below");

    JTextField outputDirTextField = new JTextField(
            "Click here to specify output directory");

    public File inputFile = null;

    public File outputDir = null;

    public static Persist persist;

    public CheckUpdates checkUpdates = null;

    public String outputDirName = "";

    public static int frameHeight = 900;

    public static int frameWidth = 950;

    //public static Dimension datConSize = new Dimension(800, 300);

    String datFileName = "";

    public DatConMenuBar menuBar = null;

    public TimeAxisPanel timeAxisPanel = null;

    public KMLPanel kmlPanel = null;

    private CsvPanel csvPanel;

    public LoggingPanel log = null;

    private LogFilesPanel logFilesPanel = null;

    private Timer resizeTimer = null;

    // UI styling
    private static final Color PANEL_BG = new Color(248, 248, 250);
    private static final Color FRAME_BG = new Color(242, 242, 245);
    private static final Color PRIMARY = new Color(32, 129, 226);
    private static final Color ACCENT = new Color(35, 179, 119);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    public DatCon() {
        DatCon.instance = this;
        new Persist();
        checkUpdates = new CheckUpdates(this);
    }

    public Container createContentPane() {
        new WorkingDir(this);
        resizeTimer = new Timer(250, this);
        resizeTimer.setRepeats(false);
        contentPanel = new JPanel();
        contentPanel.addComponentListener(this);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        contentPanel.setOpaque(true);
        contentPaneBGColor = FRAME_BG;
        contentPanel.setBackground(FRAME_BG);
        log = new LoggingPanel();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;

        JLabel outDirLabel = new JLabel("Output Dir  ");
        outDirLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(outDirLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(outputDirTextField, gbc);
        outputDirTextField.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
        outputDirTextField.setBackground(Color.WHITE);
        outputDirTextField.setFont(LABEL_FONT);
        outputDirTextField.addMouseListener(this);

        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(dirViewIt, gbc);
        styleButton(dirViewIt, PRIMARY, Color.WHITE);
        dirViewIt.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 1.0;
        fileQueuePanel = new FileQueuePanel(this, jobModel);
        stylePanel(fileQueuePanel);
        contentPanel.add(fileQueuePanel, gbc);
        gbc.weighty = 0.0;

        // Panels are still instantiated (for logic) but not shown; the table is the primary editor now.
        timeAxisPanel = new TimeAxisPanel(this);
        csvPanel = new CsvPanel(this);
        logFilesPanel = new LogFilesPanel(this);
        kmlPanel = new KMLPanel(this);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(goButton, gbc);
        goButton.setEnabled(false);
        goButton.setFont(HEADER_FONT);
        styleButton(goButton, ACCENT, Color.WHITE);
        goButton.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 6;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(log, gbc);

        outputDirName = Persist.outputDirName;
        File outDirFile = new File(outputDirName);
        if (outDirFile.exists())
            setOutputDir(outDirFile);
        File inputFile = new File(Persist.inputFileName);
        if (inputFile.exists() && Persist.loadLastOnStartup) {
            //setInputFile(inputFile);
            setDatFile(inputFile);
        } else {
            File inputDir = inputFile.getParentFile();
            if (inputDir != null) {
                if (datFileDialog != null) {
                    datFileDialog.setDirectory(inputDir.getAbsolutePath());
                }
                fc.setCurrentDirectory(inputDir);
            }
        }
        checkState();
        //contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        return contentPanel;
    }

    private void createEmptyBox(int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.insets.set(0, 0, 0, 0);
        contentPanel.add(Box.createRigidArea(new Dimension(50, 0)), gbc);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Persist.datConSize = frame.getSize();
        if (resizeTimer.isRunning()) {
            resizeTimer.restart();
        } else {
            resizeTimer.start();
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    private void getNewDatFile() {
        // Deprecated: selection now handled via Add .DAT files button
    }

    private void setDatFile(File iFile) {
        addDatFiles(new File[] { iFile });
    }

    public void createFileNames() {
        String flyFileName = "";
        String flyFileNameRoot = "";
        File inputFile = new File(datFileName);
        flyFileName = inputFile.getName();
        flyFileNameRoot = flyFileName.substring(0,
                flyFileName.lastIndexOf('.'));
        csvPanel.createFileNames(flyFileNameRoot);
        logFilesPanel.createFileNames(flyFileNameRoot);
        kmlPanel.createFileNames(flyFileNameRoot);
    }

    public void createFileNamesForJob(DatJob job) {
        if (job == null) {
            return;
        }
        datFileName = job.getFile().getAbsolutePath();
        String flyFileName = job.getFile().getName();
        String flyFileNameRoot = flyFileName.substring(0,
                flyFileName.lastIndexOf('.'));
        // Only generate defaults when the job doesn't already have user-specified names.
        if (job.getCsvFileName() == null || job.getCsvFileName().isEmpty()) {
            csvPanel.createFileNames(flyFileNameRoot);
            job.setCsvFileName(csvPanel.csvFileName);
        } else {
            csvPanel.csvFileName = job.getCsvFileName();
            csvPanel.csvFile.setText(job.getCsvFileName());
        }

        // Log files: only populate defaults for names that are still blank, without
        // overwriting any user-edited values.
        String defaultElo = flyFileNameRoot + ".log.txt";
        String defaultClo = flyFileNameRoot + ".config.txt";
        String defaultRecDefs = flyFileNameRoot + ".recDefs.txt";

        if (job.getLogEventFileName() == null
                || job.getLogEventFileName().isEmpty()) {
            job.setLogEventFileName(defaultElo);
        }
        logFilesPanel.eloFileName = job.getLogEventFileName();
        logFilesPanel.eventFile.setText(job.getLogEventFileName());

        if (job.getLogConfigFileName() == null
                || job.getLogConfigFileName().isEmpty()) {
            job.setLogConfigFileName(defaultClo);
        }
        logFilesPanel.cloFileName = job.getLogConfigFileName();
        logFilesPanel.configFile.setText(job.getLogConfigFileName());

        if (job.getLogRecDefsFileName() == null
                || job.getLogRecDefsFileName().isEmpty()) {
            job.setLogRecDefsFileName(defaultRecDefs);
        }
        logFilesPanel.recDefsFileName = job.getLogRecDefsFileName();
        logFilesPanel.recDefsFile.setText(job.getLogRecDefsFileName());

        if (job.getKmlFileName() == null || job.getKmlFileName().isEmpty()) {
            kmlPanel.createFileNames(flyFileNameRoot);
            job.setKmlFileName(kmlPanel.kmlFileName);
        } else {
            kmlPanel.kmlFileName = job.getKmlFileName();
            kmlPanel.kmlFileTextField.setText(job.getKmlFileName());
        }
    }

    public void promptForDatFiles() {
        if (inputFile != null) {
            if (datFileDialog != null && inputFile.getParentFile() != null) {
                datFileDialog.setDirectory(inputFile.getParent());
            }
            fc.setSelectedFile(inputFile);
        }
        if (datFileDialog != null) {
            File[] files = showNativeDatFileDialog();
            addDatFiles(files);
        } else {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                if (files == null || files.length == 0) {
                    File single = fc.getSelectedFile();
                    if (single != null) {
                        files = new File[] { single };
                    }
                }
                addDatFiles(files);
            }
        }
    }

    private void addDatFiles(File[] files) {
        if (files == null || files.length == 0)
            return;
        for (File iFile : files) {
            try {
                if (DatFile.isDatFile(iFile.getAbsolutePath())
                        || DJIAssistantFile.isDJIDat(iFile)
                        || Persist.invalidStructOK) {
                    DatJob job = new DatJob(iFile);
                    if (!jobModel.contains(job)) {
                        // initialize per-file defaults
                        job.setSampleRate(Persist.csvSampleRate);
                        job.setCsvEnabled(true);
                        job.setCsvEventLog(false);
                        job.setLogEventEnabled(Persist.logPanelEFB);
                        job.setLogConfigEnabled(Persist.logPanelCFB);
                        job.setLogRecDefsEnabled(Persist.logPanelRDFB);
                        job.setLowerMarker("Recording Start");
                        job.setUpperMarker("Recording Stop");
                        job.setOffsetMarker("Recording Start");
                        createFileNamesForJob(job);
                        jobModel.addElement(job);
                        inputFile = iFile;
                        Persist.inputFileName = iFile.getAbsolutePath();
                        if (outputDir == null) {
                            setOutputDir(iFile.getParentFile());
                        }
                        // keep the most recently added job selected
                        fileQueuePanel.selectJob(job);
                    }
                } else {
                    log.Error(iFile.getAbsolutePath()
                            + " is not a valid .DAT file");
                }
            } catch (IOException e) {
                log.Error(iFile.getAbsolutePath()
                        + " is not a valid .DAT file");
            }
        }
        checkState();
    }

    private File[] showNativeDatFileDialog() {
        datFileDialog.setVisible(true);
        File[] files = datFileDialog.getFiles();
        if (files != null && files.length > 0) {
            return files;
        }
        String single = datFileDialog.getFile();
        if (single != null) {
            return new File[] { new File(datFileDialog.getDirectory(), single) };
        }
        return null;
    }

    private File showNativeDirectoryDialog() {
        String original = System.getProperty("apple.awt.fileDialogForDirectories");
        // Make sure the dialog stays in directory-select mode on macOS
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        dirFileDialog.setVisible(true);
        // restore property (avoid leaking global change)
        if (original == null) {
            System.clearProperty("apple.awt.fileDialogForDirectories");
        } else {
            System.setProperty("apple.awt.fileDialogForDirectories", original);
        }
        String directory = dirFileDialog.getDirectory();
        String file = dirFileDialog.getFile();
        if (directory == null || file == null) {
            return null;
        }
        return new File(directory, file);
    }

    private void initChoosers(JFrame owner) {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "DAT file", "DAT");
        fc = new JFileChooser(/* directory */);
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(filter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);

        dc = new JFileChooser();
        dc.setAcceptAllFileFilterUsed(false);
        dc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (PREFER_NATIVE_DIALOGS) {
            datFileDialog = new FileDialog(owner, "Select .DAT files",
                    FileDialog.LOAD);
            datFileDialog.setMultipleMode(true);
            datFileDialog.setFilenameFilter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".dat");
                }
            });
            if (isMac()) {
                String original = System
                        .getProperty("apple.awt.fileDialogForDirectories");
                try {
                    System.setProperty("apple.awt.fileDialogForDirectories",
                            "true");
                    dirFileDialog = new FileDialog(owner,
                            "Select Output Directory", FileDialog.LOAD);
                    dirFileDialog.setMultipleMode(false);
                } finally {
                    if (original == null) {
                        System.clearProperty("apple.awt.fileDialogForDirectories");
                    } else {
                        System.setProperty("apple.awt.fileDialogForDirectories",
                                original);
                    }
                }
            }
        }
    }

    private boolean isMac() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        return osName.contains("mac");
    }

    private class PreAnalyze extends SwingWorker<DatFile, Object> {
        DatJob job = null;

        private DatCon datCon;

        PreAnalyze(DatJob job, DatCon datCon) {
            this.job = job;
            this.datCon = datCon;
        }

        @Override
        protected DatFile doInBackground() throws Exception {
            startWaitCursor();
            job.setStatus(DatJob.Status.ANALYZING);
            refreshJobList();
            DatFile analyzed = null;
            try {
                analyzed = DatFile.createDatFile(
                        job.getFile().getAbsolutePath(), datCon);
                if (analyzed != null) {
                    analyzed.reset();
                    analyzed.preAnalyze();
                }
            } finally {
                stopWaitCursor();
            }
            return analyzed;
        }

        @Override
        protected void done() {
            try {
                DatFile analyzed = get();
                if (analyzed != null) {
                    job.setDatFile(analyzed);
                    long lowestTick = analyzed.lowestTickNo;
                    long upperTick = (analyzed.lastMotorStopTick != -1)
                            ? analyzed.lastMotorStopTick
                            : analyzed.highestTickNo;
                    job.setTickLower(lowestTick);
                    job.setTickUpper(upperTick);
                    job.setOffset(0);
                    if (job == currentJob) {
                        datFile = analyzed;
                        datFileName = analyzed.getFile().getAbsolutePath();
                        datFileTextField.setText(datFileName);
                        Persist.save();
                        goButton.setBackground(Color.YELLOW);
                        goButton.setForeground(Color.BLACK);
                        goButton.setEnabled(false);
                        goButton.setText("Pre Analyzing .DAT");
                        setFromMarkers();
                        reset();
                        timeAxisPanel.initFromDatFile(analyzed);
                        LogFilesPanel.instance.updateAfterPreAnalyze(analyzed);
                        DatConLog.separator();
                        createFileNamesForJob(job);
                        timeAxisPanel.saveToJob(job);
                    }
                    job.setStatus(DatJob.Status.READY);
                } else {
                    job.setStatus(DatJob.Status.ERROR);
                    job.setErrorMessage("Pre-analyze failed");
                }
                refreshJobList();
                checkState();
                Persist.save();
            } catch (Exception e) {
                job.setStatus(DatJob.Status.ERROR);
                String msg = (e.getCause() != null && e.getCause().getMessage() != null)
                        ? e.getCause().getMessage()
                        : (e.getMessage() != null ? e.getMessage()
                                : "Pre-analyze error");
                job.setErrorMessage(msg);
                refreshJobList();
                checkState();
                DatConLog.Exception(e);
            }
        }
    }

    public void setFromMarkers() throws Exception {
        if (datFile != null) {
            timeAxisPanel.setFromMarkers(datFile);
        }
    }

    public void onJobSelected(DatJob job) {
        if (job == currentJob) {
            return;
        }
        // Persist edits from the previously selected job before switching.
        syncCurrentJobFromUI();

        currentJob = job;
        if (job == null) {
            datFile = null;
            datFileName = "";
            datFileTextField.setText("Use Add .DAT files below");
            checkState();
            return;
        }
        inputFile = job.getFile();
        datFileName = job.getFile().getAbsolutePath();
        datFileTextField.setText(datFileName);
        csvPanel.applyJob(job);
        logFilesPanel.applyJob(job);
        kmlPanel.applyJob(job);
        if (job.getDatFile() == null) {
            PreAnalyze fmTask = new PreAnalyze(job, this);
            fmTask.execute();
        } else {
            try {
                datFile = job.getDatFile();
                timeAxisPanel.reset();
                timeAxisPanel.initFromDatFile(datFile);
                timeAxisPanel.applyJob(job);
                LogFilesPanel.instance.updateAfterPreAnalyze(datFile);
                createFileNamesForJob(job);
            } catch (Exception e) {
                DatConLog.Exception(e);
            }
        }
        checkState();
    }

    public void removeJob(DatJob job) {
        if (job == null) {
            return;
        }
        int idx = jobModel.indexOf(job);
            jobModel.removeElement(job);
            if (job == currentJob) {
                currentJob = null;
                datFile = null;
                datFileName = "";
                if (jobModel.size() > 0) {
                    int newIndex = Math.max(0, idx - 1);
                    fileQueuePanel.selectJob(jobModel.getElementAt(newIndex));
                } else {
                    datFileTextField.setText("Use Add .DAT files below");
                }
            }
            refreshJobList();
            checkState();
        }

    public void clearJobs() {
        jobModel.clear();
        currentJob = null;
        datFile = null;
        datFileName = "";
        datFileTextField.setText("Use Add .DAT files below");
        refreshJobList();
        checkState();
    }

    private void syncCurrentJobFromUI() {
        if (currentJob == null) {
            return;
        }
        // Persist panel state into the current job so GO uses the latest edits.
        timeAxisPanel.saveToJob(currentJob);
        csvPanel.saveToJob(currentJob);
        logFilesPanel.saveToJob(currentJob);
        kmlPanel.saveToJob(currentJob);
    }

    public void timeAxisUpdated() {
        // Capture time-axis edits immediately.
        syncCurrentJobFromUI();
    }

    private void refreshJobList() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fileQueuePanel.refresh();
            }
        });
    }

    private void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(216, 220, 226), 1, true),
                new EmptyBorder(8, 8, 8, 8)));
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        button.setFont(LABEL_FONT);
    }

    private void go() {
        // Persist any panel edits on the selected job before validation/run.
        syncCurrentJobFromUI();
        if (outputDir == null || outputDirName == null
                || outputDirName.length() == 0) {
            log.Error("Output directory not set");
            return;
        }
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                log.Error("Cannot create output directory: " + outputDirName);
                return;
            }
        }
        List<DatJob> readyJobs = new ArrayList<>();
        for (int i = 0; i < jobModel.size(); i++) {
            DatJob job = jobModel.getElementAt(i);
            if (job.getStatus() == DatJob.Status.ERROR) {
                continue;
            }
            try {
                ensureAnalyzed(job);
            } catch (Exception e) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage(e.getMessage());
                DatConLog.Exception(e);
                continue;
            }
            if (job.isReady()) {
                readyJobs.add(job);
            }
        }
        if (readyJobs.isEmpty()) {
            log.Error("No analyzed .DAT files ready to convert");
            return;
        }
        BatchGo batchGo = new BatchGo(readyJobs);
        batchGo.execute();
    }

    private DatFile ensureAnalyzed(DatJob job) throws Exception {
        DatFile analyzed = job.getDatFile();
        if (analyzed != null) {
            return analyzed;
        }
        analyzed = DatFile.createDatFile(job.getFile().getAbsolutePath(), this);
        if (analyzed == null) {
            throw new IllegalStateException(
                    "Unable to analyze (null) " + job.getDisplayName());
        }
        analyzed.reset();
        analyzed.preAnalyze();
        job.setDatFile(analyzed);
        long lowestTick = analyzed.lowestTickNo;
        long upperTick = (analyzed.lastMotorStopTick != -1)
                ? analyzed.lastMotorStopTick
                : analyzed.highestTickNo;
        // Only apply defaults if the user hasn't already typed their own bounds.
        if (!job.isLowerUserSet()) {
            job.setTickLower(lowestTick);
        }
        if (!job.isUpperUserSet()) {
            job.setTickUpper(upperTick);
        }
        // apply marker prefs to ticks/offset now that we have metadata, but don't
        // override user-entered numeric values
        applyMarkersToJob(job, analyzed);
        job.setStatus(DatJob.Status.READY);
        job.setErrorMessage("");
        createFileNamesForJob(job);
        return analyzed;
    }

    private void applyMarkersToJob(DatJob job, DatFile df) {
        if (df == null)
            return;
        String lower = job.getLowerMarker();
        if (!job.isLowerUserSet()) {
            if ("Recording Start".equalsIgnoreCase(lower)) {
                job.setTickLower(df.lowestTickNo);
            } else if ("Motor Start".equalsIgnoreCase(lower)
                    && df.firstMotorStartTick != 0) {
                job.setTickLower(df.firstMotorStartTick);
            } else if ("GPS Lock".equalsIgnoreCase(lower)
                    && df.gpsLockTick != -1) {
                job.setTickLower(df.gpsLockTick);
            }
        }

        String upper = job.getUpperMarker();
        if (!job.isUpperUserSet()) {
            if ("Recording Stop".equalsIgnoreCase(upper)) {
                job.setTickUpper(df.highestTickNo);
            } else if ("Motor Stop".equalsIgnoreCase(upper)
                    && df.lastMotorStopTick != -1) {
                job.setTickUpper(df.lastMotorStopTick);
            }
        }

        String offsetM = job.getOffsetMarker();
        if (!job.isOffsetUserSet()) {
            if ("Recording Start".equalsIgnoreCase(offsetM)) {
                job.setOffset(df.lowestTickNo);
            } else if ("Motor Start".equalsIgnoreCase(offsetM)
                    && df.firstMotorStartTick != 0) {
                job.setOffset(df.firstMotorStartTick);
            } else if ("Flight Start".equalsIgnoreCase(offsetM)
                    && df.flightStartTick != -1) {
                job.setOffset(df.flightStartTick);
            }
        }
    }

    private class BatchGo extends SwingWorker<Void, DatJob> {
        private final List<DatJob> jobs;

        BatchGo(List<DatJob> jobs) {
            this.jobs = jobs;
        }

        @Override
        protected Void doInBackground() throws Exception {
            startWaitCursor();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    goButton.setBackground(Color.BLUE);
                    goButton.setForeground(Color.WHITE);
                    goButton.setEnabled(false);
                }
            });
            int total = jobs.size();
            int index = 1;
            for (DatJob job : jobs) {
                if (!job.isReady()) {
                    continue;
                }
                job.setStatus(DatJob.Status.PROCESSING);
                refreshJobList();
                try {
                    processJob(job, index, total);
                    // processJob sets status to ERROR/DONE as needed. If it left the
                    // job in READY or PROCESSING, treat that as success.
                    if (job.getStatus() != DatJob.Status.ERROR) {
                        job.setStatus(DatJob.Status.DONE);
                        job.setErrorMessage("");
                    }
                } catch (FileBeingUsed fbu) {
                    job.setStatus(DatJob.Status.ERROR);
                    job.setErrorMessage("In use: " + fbu.getFileName());
                    log.Error("Can't convert " + job.getFile().getName()
                            + " because " + fbu.getFileName()
                            + " is being used");
            } catch (Exception e) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage(e.getMessage() == null ? "Error"
                        : e.getMessage());
                DatConLog.Exception(e);
            } catch (Throwable t) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage(
                        t.getMessage() == null ? "Unexpected error" : t.getMessage());
                DatConLog.Exception(new Exception(t));
            } finally {
                refreshJobList();
            }
            index++;
        }
            return null;
        }

        private void processJob(DatJob job, int index, int total)
                throws Exception {
            final DatFile analyzed;
            try {
                analyzed = ensureAnalyzed(job);
                // Keep the field for any legacy code that still reads it,
                // but use the local reference for the rest of this method to
                // avoid races if the selection listener clears datFile while
                // the batch worker is running.
                datFile = analyzed;
            } catch (Exception ex) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage(ex.getMessage() == null ? "Analyze failed"
                        : ex.getMessage());
                DatConLog.Exception(ex);
                return;
            }
            if (analyzed == null) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage("Dat file not analyzed");
                return;
            }
            if (job.getTickUpper() <= job.getTickLower()) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage("Upper tick must be greater than lower tick");
                return;
            }
            try {
                analyzed.reset();
            } catch (Exception ex) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage(ex.getMessage() == null ? "Reset failed"
                        : ex.getMessage());
                DatConLog.Exception(ex);
                return;
            }
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    datFileName = job.getFile().getAbsolutePath();
                    datFileTextField.setText(datFileName);
                    createFileNamesForJob(job);
                    try {
                        timeAxisPanel.reset();
                        timeAxisPanel.initFromDatFile(analyzed);
                        timeAxisPanel.applyJob(job);
                        csvPanel.applyJob(job);
                        logFilesPanel.applyJob(job);
                        kmlPanel.applyJob(job);
                        LogFilesPanel.instance.updateAfterPreAnalyze(analyzed);
                    } catch (Exception e) {
                        DatConLog.Exception(e);
                    }
                    goButton.setText(
                            "Converting (" + index + "/" + total + ")");
                }
            });

            ConvertDat convertDat;
            try {
                convertDat = analyzed.createConVertDat();
            } catch (Exception ex) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage(ex.getMessage() == null ? "Convert init failed"
                        : ex.getMessage());
                DatConLog.Exception(ex);
                return;
            }
            if (convertDat == null) {
                job.setStatus(DatJob.Status.ERROR);
                job.setErrorMessage("Convert init returned null");
                return;
            }
            try {
                log.Info("Starting convert for " + job.getDisplayName());
                // Open writers before wiring them into convertDat
                createPrintStreams();
                // Push the per-file timing/offset and panel settings into convertDat
                timeAxisPanel.setArgs(convertDat);
                csvPanel.setArgs(convertDat);
                logFilesPanel.setArgs(convertDat);
                kmlPanel.setArgs(convertDat);
                if (csvPanel.csvButton.isSelected()
                        && (csvPanel.csvWriter == null
                                || job.getCsvFileName() == null
                                || job.getCsvFileName().isEmpty())) {
                    job.setStatus(DatJob.Status.ERROR);
                    job.setErrorMessage("CSV writer not opened");
                    return;
                }
                convertDat.createRecordParsers();
                if (convertDat.records == null
                        || convertDat.records.isEmpty()) {
                    job.setStatus(DatJob.Status.ERROR);
                    job.setErrorMessage("No record parsers built");
                    return;
                }
                AnalyzeDatResults results = convertDat.analyze(true);
                log.Info(results.toString());
                csvPanel.updateAfterGo();
                logFilesPanel.updateAfterGo();
                kmlPanel.updateAfterGo(convertDat);
                // Validate expected outputs
                if (csvPanel.csvButton.isSelected()) {
                    File expectedCsv = new File(outputDirName,
                            job.getCsvFileName());
                    DatConLog.Log("Post-analyze CSV length=" + expectedCsv.length()
                            + " file=" + expectedCsv.getAbsolutePath());
                    if (!expectedCsv.exists() || expectedCsv.length() == 0) {
                        job.setStatus(DatJob.Status.ERROR);
                        job.setErrorMessage("CSV not generated");
                        return;
                    }
                }
            } finally {
                closePrintStreams();
            }
            job.setStatus(DatJob.Status.DONE);
            job.setErrorMessage("");
        }

        @Override
        protected void done() {
            try {
                stopWaitCursor();
                goButton.setBackground(Color.GREEN);
                goButton.setForeground(Color.BLACK);
                goButton.setEnabled(true);
                goButton.setText("GO!");
                refreshJobList();
                checkState();
            } catch (Exception e) {
                DatConLog.Exception(e);
            }
        }
    }

    private void createPrintStreams() throws FileBeingUsed {
        try {
            csvPanel.createPrintStreams(outputDirName);
            logFilesPanel.createPrintStreams(outputDirName);
            kmlPanel.createPrintStreams(outputDirName);
        } catch (FileNotFoundException e) {
            String msg = e.getMessage();
            if (msg.indexOf(
                    "because it is being used by another process)") > 0) {
                String fileName = msg.substring(0, msg.indexOf(" ("));
                throw (new FileBeingUsed(fileName));
            }
        }
    }

    public void closePrintStreams() {
        csvPanel.closePrintStreams();
        logFilesPanel.closePrintStreams();
        kmlPanel.closePrintStreams();
    }

    void setOutputDir(File file) {
        outputDir = file;
        outputDirName = outputDir.getAbsolutePath();
        outputDirTextField.setText(outputDirName);
    }

    private void reset() {
        timeAxisPanel.reset();
        csvPanel.reset();
        //HPElevationPanel.reset();
        logFilesPanel.reset();
        kmlPanel.reset();
        //HPElevationPanel.reset();
    }

    public void dontViewIt() {
        csvPanel.dontViewIt();
        logFilesPanel.dontViewIt();
        kmlPanel.dontViewIt();
    }

    private void setArgs(ConvertDat convertDat) {
        timeAxisPanel.setArgs(convertDat);
        csvPanel.setArgs(convertDat);
        logFilesPanel.setArgs(convertDat);
        kmlPanel.setArgs(convertDat);
        //HPElevationPanel.setArgs(convertDat);
    }

    public void setInputFile(File inFile) {
        inputFile = inFile;
        String fName = inputFile.getAbsolutePath();
        Persist.inputFileName = fName;
        datFileTextField.setText(fName);
        setOutputDir(inputFile.getParentFile());
    }

    public void checkState() {
        StringBuilder cantGo = new StringBuilder();
        if (outputDir != null && outputDirTextField.getText().length() > 0) {
            outputDirTextField.setBackground(Color.WHITE);
        } else {
            outputDirTextField.setBackground(Color.RED);
            cantGo.append("OutputDir not specified,");
        }
        if (jobModel.size() > 0) {
            datFileTextField.setBackground(Color.WHITE);
            List<String> jobIssues = new ArrayList<>();
            for (int i = 0; i < jobModel.size(); i++) {
                DatJob job = jobModel.getElementAt(i);
                if (!job.isReady()) {
                    jobIssues.add(job.getDisplayName() + " not analyzed");
                } else if (job.getTickLower() >= job.getTickUpper()) {
                    jobIssues.add(job.getDisplayName() + " lower >= upper");
                }
            }
            if (!jobIssues.isEmpty()) {
                cantGo.append(String.join("; ", jobIssues));
            }
        } else {
            datFileTextField.setBackground(Color.RED);
            cantGo.append("No .DAT files selected,");
        }
        if (cantGo.length() > 0) {
            goButton.setBackground(Color.RED);
            goButton.setForeground(Color.BLACK);
            goButton.setEnabled(false);
            goButton.setText("Can't Go: " + cantGo.toString());
        } else {
            goButton.setBackground(Color.GREEN);
            goButton.setForeground(Color.BLACK);
            goButton.setEnabled(true);
            goButton.setText("GO!");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            JComponent source = (JComponent) (e.getSource());
            if (source == outputDirTextField) {
                if (outputDir != null) {
                    if (dirFileDialog != null) {
                        dirFileDialog.setDirectory(outputDir.getAbsolutePath());
                    }
                    dc.setSelectedFile(outputDir);
                }
                if (dirFileDialog != null) {
                    File chosenDir = showNativeDirectoryDialog();
                    if (chosenDir != null) {
                        setOutputDir(chosenDir);
                        Persist.outputDirName = outputDirName;
                        Persist.save();
                        checkState();
                    }
                } else {
                    int returnVal = dc.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        setOutputDir(dc.getSelectedFile());
                        Persist.outputDirName = outputDirName;
                        Persist.save();
                        checkState();
                    }
                }
            }
        } catch (Exception exception) {
            DatConLog.Exception(exception);
            ;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Object source = (e.getSource());
            if (source == goButton) {
                go();
            } else if (source == dirViewIt) {
                Desktop.getDesktop().open(new File(outputDirName));
            } else if (source == resizeTimer) {
                Persist.save();
            }
        } catch (Exception exception) {
            DatConLog.Exception(exception);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void startWaitCursor() {
        RootPaneContainer root = (RootPaneContainer) frame.getRootPane()
                .getTopLevelAncestor();
        root.getGlassPane()
                .setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        root.getGlassPane().addMouseListener(mouseAdapter);
        root.getGlassPane().setVisible(true);
    }

    public void stopWaitCursor() {
        RootPaneContainer root = (RootPaneContainer) frame.getRootPane()
                .getTopLevelAncestor();
        root.getGlassPane()
                .setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        root.getGlassPane().removeMouseListener(mouseAdapter);
        root.getGlassPane().setVisible(false);
    }

    private final static MouseAdapter mouseAdapter = new MouseAdapter() {
    };

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            UIManager.put("ToolTip.background", Color.WHITE);
            UIManager.put("ToolTip.foreground", Color.BLACK);
            // Create and set up the content pane.
            DatCon datCon = new DatCon();
            frame = new JFrame("DatCon");
            datCon.initChoosers(frame);
            //        frame.addComponentListener(new ComponentAdapter() {
            //            public void componentResized(ComponentEvent evt) {
            //                Component c = (Component) evt.getSource();
            //                int x = 1;
            //            }
            //        });
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // frame.setJMenuBar(datCon.createMenuBar());
            frame.setJMenuBar(new DatConMenuBar(datCon));
            frame.setContentPane(datCon.createContentPane());

            // Display the window.
            frame.setSize(Persist.datConSize);
            frame.setVisible(true);
            ImageIcon img = new ImageIcon("drone.jpg");
            frame.setIconImage(img.getImage());
            if (Persist.checkUpdts) {
                datCon.checkUpdates.checkForUpdates();
            }
        } catch (Exception e) {
            DatConLog.Exception(e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        DatConLog log = new DatConLog();
        if (!log.ok()) {
            DatConPopups.noLogFile();
            System.exit(1);
        }
        String dataModel = System.getProperty("sun.arch.data.model");
        if (dataModel.equals("64")) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createAndShowGUI();
                }
            });
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addPropertyChangeListener("permanentFocusOwner", new PropertyChangeListener()
        {
            public void propertyChange(final PropertyChangeEvent e)
            {
                if (e.getNewValue() instanceof JTextField)
                {
                                        SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            JTextField textField = (JTextField)e.getNewValue();
                            textField.selectAll();
                        }
                    });

                }
            }
        });
        } else {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DataModelDialog.createAndShowDataModelDialog();
                }
            });
        }
    }

    public DatFile getDatFile() {
        return datFile;
    }
}
