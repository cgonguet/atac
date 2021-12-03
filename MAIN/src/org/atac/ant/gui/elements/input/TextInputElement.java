package org.atac.ant.gui.elements.input;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * Input text element represented by a JTextField.
 *
 * @ant.type name="gintext" category="gui" ignore="false"
 */
public class TextInputElement extends AbstractGuiInputElement {
  JTextField textField;
  String text = "";

  public Component getInputComponent() {
    textField = new JTextField(text);
    if(columns>0)
    {
      textField.setColumns(columns);
    }
    else if (text.isEmpty())
    {
      textField.setColumns(10);
    }

    textField.setFont(getFont("Label.font"));
    textField.setCaretPosition(text.length());
    return textField;
  }

  public String getInputValue() {
    return textField.getText().trim();
  }

    @Override
    String getDefaultValue()
    {
        return text;
    }

    @Override
    void setDefaultValue(String value)
    {
        text = value;
    }

    /**
   * Text content
   *
   * @ant.not-required
   */
  public void setText(String text) {
    setDefaultValue(text);
  }

}
