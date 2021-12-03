package org.atac.ant.gui.elements;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.awt.*;

/**
 * File Editor element represented by a JTextArea.
 *
 * @ant.type name="gfileeditor" category="gui" ignore="false"
 */
public class FileEditorElement extends TextElement {
  private File file;
  private boolean create = true;
  private boolean focus = false;

  public boolean requestFocus() {
    if (focus) {
      textArea.requestFocus();
      return true;
    }
    return false;
  }

  public void precheck() {
    if (file == null) {
      throw new BuildException("'file' attribute is required");
    }
    if (!file.exists() && !create) {
      throw new BuildException("File does not exist and creation is not authorized");
    }
  }

  @Override
  public void postAdd(Component parent)
  {
    textArea.setToolTipText("File editor: " + file.getAbsolutePath());
    textArea.setEditable(true);
    updateContent();
  }

  private void updateContent() {
    if (file.exists()) {
      log("Read content file '"+ file + "'", Project.MSG_VERBOSE);
      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuffer text = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
          text.append(line).append('\n');
        }
        textArea.setText(text.toString());
      } catch (IOException e) {
        throw new BuildException("Error while reading file: " + file, e);
      }
    }
  }

  public void validate() {
    try {
      final FileWriter writer = new FileWriter(file);
      writer.write(textArea.getDocument().getText(0, textArea.getDocument().getLength()));
      writer.flush();
      writer.close();
      log("Write content to file '" + file + "'", Project.MSG_VERBOSE);
    } catch (IOException e) {
      throw new BuildException("Error while writing file: " + file, e);
    } catch (BadLocationException e) {
      throw new BuildException("Error while writing file: " + file, e);
    }
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
   * If true: create the file if it is not yet created.
   *
   * @ant.not-required Default is: true
   */
  public void setCreate(final boolean create) {
    this.create = create;
  }

  /**
   * If true: get the focus on
   *
   * @ant.not-required Default is: false
   */
  public void setFocus(boolean focus) {
    this.focus = focus;
  }

}
