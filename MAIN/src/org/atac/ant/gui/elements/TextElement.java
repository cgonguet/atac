package org.atac.ant.gui.elements;

import javax.swing.*;
import java.awt.*;

/**
 * Text element represented by a JTextArea.
 *
 * @ant.type name="gtext" category="gui" ignore="false"
 */
public class TextElement extends AbstractGuiElement {
  String label;
  String text;
  int rows;
  int columns;
  protected JTextArea textArea;

  public JComponent getPane() {
    JPanel pane = new JPanel();
    pane.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(1, 1, 1, 1);
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pane.add(new JLabel(label), gbc);

    textArea = new JTextArea();
    textArea.setFont(getFont("TextArea.font"));
    textArea.setForeground(Color.black);
    textArea.setRows(rows);
    textArea.setColumns(columns);
    textArea.setText(text);
    textArea.setEditable(false);
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pane.add(new JScrollPane(textArea), gbc);

    return pane;
  }

  @Override
  public int getFillBehavior()
  {
    return GridBagConstraints.BOTH;
  }

  /**
   * Label to describe the text element
   *
   * @ant.not-required
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Text content
   *
   * @ant.not-required
   */
  public void setText(String text) {
    this.text = text;
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
}
