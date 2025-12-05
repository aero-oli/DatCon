package src.Files;

import java.io.File;
import src.Files.Persist;

/**
 * Represents one .DAT file to be processed in a batch. Stores the pre-analysed
 * DatFile instance plus the per-file time/offset settings the user chooses.
 */
public class DatJob {

    public enum Status {
        PENDING, ANALYZING, READY, PROCESSING, DONE, ERROR
    }

    private final File file;
    private DatFile datFile;
    private Status status = Status.PENDING;
    private long tickLower = 0;
    private long tickUpper = 0;
    private long offset = 0;
    private String errorMessage = "";

    // Track whether the user manually edited tick/offset values in the table.
    private boolean lowerUserSet = false;
    private boolean upperUserSet = false;
    private boolean offsetUserSet = false;

    // Per-file output/settings
    private int sampleRate = Persist.csvSampleRate;
    private boolean csvEnabled = true;
    private boolean csvEventLog = false;
    private String csvFileName = "";

    private boolean logEventEnabled = Persist.logPanelEFB;
    private boolean logConfigEnabled = Persist.logPanelCFB;
    private boolean logRecDefsEnabled = Persist.logPanelRDFB;
    private String logEventFileName = "";
    private String logConfigFileName = "";
    private String logRecDefsFileName = "";

    private boolean kmlGroundTrack = false;
    private boolean kmlProfile = false;
    private double homePointElevation = Double.NaN;
    private String kmlFileName = "";

    // Marker labels for table editing (cosmetic, but help keep intent)
    private String lowerMarker = "Recording Start";
    private String upperMarker = "Recording Stop";
    private String offsetMarker = "Recording Start";

    public DatJob(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getDisplayName() {
        return file.getName();
    }

    public DatFile getDatFile() {
        return datFile;
    }

    public void setDatFile(DatFile datFile) {
        this.datFile = datFile;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTickLower() {
        return tickLower;
    }

    public void setTickLower(long tickLower) {
        this.tickLower = tickLower;
    }

    public void setTickLowerUser(long tickLower) {
        this.tickLower = tickLower;
        this.lowerUserSet = true;
    }

    public long getTickUpper() {
        return tickUpper;
    }

    public void setTickUpper(long tickUpper) {
        this.tickUpper = tickUpper;
    }

    public void setTickUpperUser(long tickUpper) {
        this.tickUpper = tickUpper;
        this.upperUserSet = true;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setOffsetUser(long offset) {
        this.offset = offset;
        this.offsetUserSet = true;
    }

    public boolean isLowerUserSet() {
        return lowerUserSet;
    }

    public boolean isUpperUserSet() {
        return upperUserSet;
    }

    public boolean isOffsetUserSet() {
        return offsetUserSet;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public boolean isCsvEnabled() {
        return csvEnabled;
    }

    public void setCsvEnabled(boolean csvEnabled) {
        this.csvEnabled = csvEnabled;
    }

    public boolean isCsvEventLog() {
        return csvEventLog;
    }

    public void setCsvEventLog(boolean csvEventLog) {
        this.csvEventLog = csvEventLog;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public boolean isLogEventEnabled() {
        return logEventEnabled;
    }

    public void setLogEventEnabled(boolean logEventEnabled) {
        this.logEventEnabled = logEventEnabled;
    }

    public boolean isLogConfigEnabled() {
        return logConfigEnabled;
    }

    public void setLogConfigEnabled(boolean logConfigEnabled) {
        this.logConfigEnabled = logConfigEnabled;
    }

    public boolean isLogRecDefsEnabled() {
        return logRecDefsEnabled;
    }

    public void setLogRecDefsEnabled(boolean logRecDefsEnabled) {
        this.logRecDefsEnabled = logRecDefsEnabled;
    }

    public String getLogEventFileName() {
        return logEventFileName;
    }

    public void setLogEventFileName(String logEventFileName) {
        this.logEventFileName = logEventFileName;
    }

    public String getLogConfigFileName() {
        return logConfigFileName;
    }

    public void setLogConfigFileName(String logConfigFileName) {
        this.logConfigFileName = logConfigFileName;
    }

    public String getLogRecDefsFileName() {
        return logRecDefsFileName;
    }

    public void setLogRecDefsFileName(String logRecDefsFileName) {
        this.logRecDefsFileName = logRecDefsFileName;
    }

    public boolean isKmlGroundTrack() {
        return kmlGroundTrack;
    }

    public void setKmlGroundTrack(boolean kmlGroundTrack) {
        this.kmlGroundTrack = kmlGroundTrack;
        if (kmlGroundTrack) {
            this.kmlProfile = false;
        }
    }

    public boolean isKmlProfile() {
        return kmlProfile;
    }

    public void setKmlProfile(boolean kmlProfile) {
        this.kmlProfile = kmlProfile;
        if (kmlProfile) {
            this.kmlGroundTrack = false;
        }
    }

    public double getHomePointElevation() {
        return homePointElevation;
    }

    public void setHomePointElevation(double homePointElevation) {
        this.homePointElevation = homePointElevation;
    }

    public String getKmlFileName() {
        return kmlFileName;
    }

    public void setKmlFileName(String kmlFileName) {
        this.kmlFileName = kmlFileName;
    }

    public String getLowerMarker() {
        return lowerMarker;
    }

    public void setLowerMarker(String lowerMarker) {
        this.lowerMarker = lowerMarker;
    }

    public String getUpperMarker() {
        return upperMarker;
    }

    public void setUpperMarker(String upperMarker) {
        this.upperMarker = upperMarker;
    }

    public String getOffsetMarker() {
        return offsetMarker;
    }

    public void setOffsetMarker(String offsetMarker) {
        this.offsetMarker = offsetMarker;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isReady() {
        return datFile != null
                && (status == Status.READY || status == Status.DONE);
    }

    public boolean hasError() {
        return status == Status.ERROR;
    }

    @Override
    public String toString() {
        String statusLabel = status.toString();
        if (status == Status.ERROR && errorMessage != null
                && errorMessage.length() > 0) {
            statusLabel += " - " + errorMessage;
        }
        return getDisplayName() + " [" + statusLabel + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DatJob other = (DatJob) obj;
        return file.equals(other.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
