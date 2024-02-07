package ua.com.globallogic.cryptograph.utils;

import java.io.File;

public final class FileSelection {
    private File fileSelection;

    public FileSelection() {

    }

    public File getFileSelection() {
        return fileSelection;
    }

    public void setFileSelection(File fileSelection) {
        this.fileSelection = fileSelection;
    }

    @Override
    public String toString() {
        return fileSelection.getAbsolutePath();
    }
}
