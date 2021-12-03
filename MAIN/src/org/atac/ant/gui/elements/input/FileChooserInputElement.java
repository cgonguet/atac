package org.atac.ant.gui.elements.input;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * File chooser element represented by a JFileChooser.
 *
 * @ant.type name="ginfile" category="gui" ignore="false"
 */
public class FileChooserInputElement extends AbstractGuiInputElement
        implements ActionListener {
  private JTextField textField;
  private JPanel userPane;
  File file;
  String[] filter;
  int type = JFileChooser.FILES_ONLY;
  boolean editable = false;

  public Component getInputComponent() {
    userPane = new JPanel(new BorderLayout());
    textField = new JTextField();
    textField.setFont(getFont("Label.font"));
    setTextField();
    if(columns>0)
    {
      textField.setColumns(columns);
    }
    else if (textField.getText().isEmpty())
    {
      textField.setColumns(10);
    }

    userPane.add(textField, BorderLayout.CENTER);
    JButton chooseButton = new JButton("Choose");
    chooseButton.addActionListener(this);
    userPane.add(chooseButton, BorderLayout.EAST);
    return userPane;
  }

  private void setTextField() {
      setTextField(file == null ? "" : file.getAbsolutePath());
  }

    private void setTextField(String value)
    {
        textField.setEditable(true);
        textField.setText(value);
        textField.setCaretPosition(textField.getText().length());
        textField.setEditable(editable);
    }

    String getInputValue() {
    return textField.getText().trim();
  }

    @Override
    String getDefaultValue()
    {
        return file == null ? "" : file.getAbsolutePath();
    }

    @Override
    void setDefaultValue(String value)
    {
        file = new File(value);
    }

  @Override
  public void postAdd(Component parent)
  {
    setTextField(); // in order to see end of text
  }

  /**
   * Comma separated values to define filters
   *
   * @ant.not-required
   */
  public void setFilter(String filter) {
    this.filter = filter.split(",");
  }

  static Map<String, Integer> types = new HashMap<String, Integer>();

  static {
    types.put("dir", JFileChooser.DIRECTORIES_ONLY);
    types.put("file", JFileChooser.FILES_ONLY);
    types.put("both", JFileChooser.FILES_AND_DIRECTORIES);
  }

  /**
   * Type (dir|file|both)
   *
   * @ant.not-required Default is: file
   */
  public void setType(String typeStr) {
    Integer typeInteger = types.get(typeStr);
    if (typeInteger == null) {
      throw new BuildException("'type' attribute should be in: " + types.keySet());
    } else {
      type = typeInteger;
    }
  }

  public void actionPerformed(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser(getInputValue());
    fileChooser.setFileSelectionMode(type);

    if (filter != null) {
      for (final String suffix : filter) {
        fileChooser.setFileFilter(new FileFilter() {

          public boolean accept(File f) {
            if (f.isDirectory() || f.getName().matches(".+\\." + suffix)) {
              return true;
            }
            return false;
          }

          public String getDescription() {
            return suffix;
          }
        });
      }

    }

    int result = fileChooser.showOpenDialog(userPane);
    if (result == JFileChooser.ERROR_OPTION) {
      throw new BuildException("Error while selecting file");
    }
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    file = fileChooser.getSelectedFile();
    setTextField();
  }

  /**
   * Default file.
   *
   * @ant.required
   */
  public void setFile(File file) {
    this.file = file;
    log("File is set to '" + this.file + "'", Project.MSG_VERBOSE);

  }

  /**
   * If true the text field is editable
   *
   * @ant.not-required Default is: false
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

}
