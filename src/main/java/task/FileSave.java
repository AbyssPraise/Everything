package task;

import com.sun.istack.internal.NotNull;

import java.io.File;

public interface FileSave {
    void callback(@NotNull File dir);
}
