package org.atac.ant.comm.ssh;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderThread extends Thread {
  private BufferedReader reader;
  private OutputManager outputManager;

  ReaderThread(InputStream in, OutputManager outputManager) throws IOException {
    this.outputManager = outputManager;
    reader = new BufferedReader(new InputStreamReader(in));
  }

  public void run() {
    try {
      String outputLine = reader.readLine();
      while (outputLine != null) {
        outputManager.readLine(outputLine);
        outputLine = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  interface OutputManager {
    void readLine(String line);
  }

}
