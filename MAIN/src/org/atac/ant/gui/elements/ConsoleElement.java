package org.atac.ant.gui.elements;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Recorder;
import org.apache.tools.ant.taskdefs.Sequential;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Console element
 *
 * @ant.type name="gconsole" category="gui" ignore="false"
 */
public class ConsoleElement extends AbstractGuiElement {
  private JTextArea textArea;
  private File file;
  private int rows;
  private int columns;
  private Sequential sequential;
  private Recorder.VerbosityLevelChoices level;
  private boolean append = true;
  private Recorder recorder;
  private volatile boolean recording;

  public void precheck() {
    if (file == null) {
      throw new BuildException("'file' attribute is required");
    }

    if (level == null) {
      level = new Recorder.VerbosityLevelChoices();
      level.setValue("verbose");
    }
  }

  public JComponent getPane() {
    textArea = new JTextArea();
    textArea.setToolTipText("Console: " + file.getAbsolutePath());
    textArea.setFont(getFont("TextArea.font"));
    textArea.setForeground(Color.black);
    textArea.setRows(rows);
    textArea.setColumns(columns);
    textArea.setEditable(false);

    return new JScrollPane(textArea);
  }

  public void execute() {
    log("Execute Sequential task", Project.MSG_VERBOSE);

    clearConsole();

    startRecording();

    new Thread(new Runnable() {

      public void run() {
        try {
          sequential.execute();
        } catch (BuildException e) {
          e.printStackTrace();
        }
        stopRecording();
        log("Execute Sequential task Ended", Project.MSG_VERBOSE);

      }
    }).start();

    try {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      DataInputStream dis = new DataInputStream(bis);
      while (recording) {
        while (dis.available() != 0) {
          textArea.append(dis.readLine());
          textArea.append("\n");
          textArea.setCaretPosition(textArea.getText().length());
        }
      }
      fis.close();
      bis.close();
      dis.close();
    } catch (IOException e) {
      throw new BuildException("Error while reading file: " + file, e);
    }
    log("Console end reading", Project.MSG_VERBOSE);

  }

  private void clearConsole() {
    if(!append && file.exists()){
      file.delete();
    }

    textArea.setEditable(true);
    textArea.setText("");
    textArea.setEditable(false);
    textArea.setCaretPosition(0);
  }

  private void startRecording() {
    recording = true;
    recorder = (Recorder) getProject().createTask("record");
    recorder.setEmacsMode(true);
    recorder.setAppend(append);
    recorder.setName(file.getAbsolutePath());
    recorder.setLoglevel(level);
    recorder.setAction(getRecorderAction("start"));
    recorder.execute();
  }

   private void stopRecording() {
    recording = false;
    recorder.setAction(getRecorderAction("stop"));
    recorder.setName(file.getAbsolutePath());
    recorder.execute();
  }

  private Recorder.ActionChoices getRecorderAction(String action) {
     Recorder.ActionChoices recorderAction = new Recorder.ActionChoices();
     recorderAction.setValue(action);
     return recorderAction;
   }

  /**
   * Number of rows
   *
   * @ant.not-required
   */
  public void setRows(int rows) {
    this.rows = rows;
  }

  /**
   * Number of columns
   *
   * @ant.not-required
   */
  public void setColumns(int columns) {
    this.columns = columns;
  }

  /**
   * The file to edit.
   *
   * @ant.required
   */
  public void setFile(final File file) {
    this.file = file;
  }

  /**
   * Should the recorder append to a file, or create a new one
   *
   * @ant.not-required Default is: false
   */
  public void setAppend(boolean append) {
    this.append = append;
  }

  /**
   * Verbosity level: error|warn|info|verbose|debug
   *
   * @ant.not-required Default is: verbose
   */
  public void setLoglevel(Recorder.VerbosityLevelChoices level) {
    this.level = level;
  }

  /**
   * Sequential task to execute
   *
   * @ant.required
   */
  public void addSequential(Sequential sequential) {
    this.sequential = sequential;
  }

}
