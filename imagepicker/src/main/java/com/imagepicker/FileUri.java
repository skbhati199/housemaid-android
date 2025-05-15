package com.imagepicker;

import android.net.Uri;
import java.io.File;

public class FileUri {
  private Uri imageUrl;
  private File file;

  Uri getImageUrl() {
    return imageUrl;
  }

  void setImageUrl(Uri imageUrl) {
    this.imageUrl = imageUrl;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }
}
