package src.Files;

import java.io.File;

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

    public long getTickUpper() {
        return tickUpper;
    }

    public void setTickUpper(long tickUpper) {
        this.tickUpper = tickUpper;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isReady() {
        return status == Status.READY || status == Status.DONE;
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
