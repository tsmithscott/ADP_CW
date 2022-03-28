package adp.cw2122;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to simulate downloading a file across a slow network.
 * No changes are required to this file for the coursework assignment.
 */
public class Network {

  private static class SlowInputStream extends FilterInputStream {
    private final int delay = 1; // milliseconds per byte
    public SlowInputStream(InputStream in) {
      super(in);
    }
    @Override
    public int read() throws IOException {
      try {
        Thread.sleep(this.delay);
      } catch(InterruptedException x){
        Thread.currentThread().interrupt(); // preserve interruption
      }
      return super.read();
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int toReturn = super.read( b, off, len);
      if (toReturn >= 0) {
        try {
          Thread.sleep(this.delay * toReturn);
        } catch(InterruptedException x){
          Thread.currentThread().interrupt(); // preserve interruption
        }
      }
      return toReturn;
    }
  }

  public static InputStream getInputStreamForDownload( File uri) throws FileNotFoundException {
    FileInputStream fis = new FileInputStream(uri);
    return new SlowInputStream(fis);
  }

}
