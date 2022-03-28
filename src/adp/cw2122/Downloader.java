package adp.cw2122;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class Downloader extends JFrame {

  private final File[] downloads;
  private final List<JProgressBar> bars;

  public Downloader(final File[] downloads) {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    final JPanel progPanel = new JPanel(new GridLayout(0,1));
    this.downloads = downloads;
    this.bars = new ArrayList<>(downloads.length);
    for (int i = 0; i < downloads.length; i++) {
      final JProgressBar bar = new JProgressBar();
      bar.setMaximum((int) downloads[i].length());
      this.bars.add(bar);
      final JPanel border = new JPanel(new BorderLayout());
      border.setBorder(BorderFactory.createTitledBorder(downloads[i].getPath()));
      border.add(bar);
      final JButton cancelButton = new JButton("X");
      final int j = i;
      cancelButton.addActionListener((ev)->doCancel(j));
      border.add(cancelButton, BorderLayout.EAST);
      progPanel.add(border);
    }

    final JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(progPanel);

    final JButton goButton = new JButton("Start");
    goButton.addActionListener((ev)->doDownloads());
    mainPanel.add(goButton, BorderLayout.SOUTH);

    add(mainPanel);

    pack();
    setVisible(true);
  }

  private void doCancel(final int i) {
    System.out.println( "Cancelling " + i);
  }

  private void doDownloads() {
    for (int i = 0; i < this.downloads.length; i++) {
      doDownload(i);
    }
  }

  private byte[] doDownload(final int i) {
    System.out.println( "Doing: " + this.downloads[i]);
    final JProgressBar bar = this.bars.get(i);
    byte[] bytes = null;
    try {
      final InputStream is = Network.getInputStreamForDownload(this.downloads[i]);
      final ArrayList<Byte> byteList = new ArrayList<Byte>(1024);
      while(true) {
        final int read = is.read();
        if (read < 0) {
          break;
        } else {
          byteList.add((byte) read);
          bar.setValue(byteList.size());
          if ((byteList.size() % 1000) == 0) {
            System.out.println( byteList.size() + " bytes read");
          }
        } 
      }
      bytes = new byte[byteList.size()];
      for (int j = 0; j < bytes.length; j++) {
        bytes[j] = byteList.get(j);
      }
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
      showImage(image,this.downloads[i].getPath());
    } catch (final IOException e) {
      e.printStackTrace();
    }
    System.out.println( this.downloads[i] + " DONE");
    return bytes;
  }

  private void showImage(final BufferedImage image, final String name) {
    final JFrame iw = new JFrame(name);
    iw.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    final ImagePanel ip = new ImagePanel();
    ip.setImage(image);
    iw.add(ip);
    iw.pack();
    iw.setVisible(true);
  }

  private static class ImagePanel extends JComponent {
    private BufferedImage image;
    public void setImage(final BufferedImage image) {
      this.image = image;
      setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
      repaint();
    }
    @Override
    public void paintComponent(final Graphics g) {
      g.drawImage(this.image, 0, 0, this);
    }
  }

  public static void launch() {
    final String[] names = new String[] { "images/pyramids.jpg", "images/pagodas.jpg", "images/bug.jpg" };
    final File[] downloads = new File[names.length];
    for (int i = 0; i < downloads.length; i++) {
      downloads[i] = new File(names[i]);
    }
    new Downloader(downloads);
  }

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(()->launch());
  }
}
