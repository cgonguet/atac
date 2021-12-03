package org.atac.ant.gui.elements.input;

import org.atac.ant.gui.util.BoundsPopupMenuListener;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Input list of elements represented by a JComboBox.
 *
 * @ant.type name="ginlist" category="gui" ignore="false"
 */
public class ListInputElement extends AbstractGuiInputElement {
  JComboBox jComboBox;
  private String listing;
  private String current;

  public Component getInputComponent() {
    jComboBox = new JComboBox();
    jComboBox.setFont(getFont("Label.font"));

    if(columns > 0)
    {
      StringBuffer prototype = new StringBuffer();
      for (int i = 0; i < columns; i++)
      {
        prototype.append('m');
      }
      jComboBox.setPrototypeDisplayValue(prototype.toString());
      jComboBox.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
    }

    List<String> items = Arrays.asList(listing.split(","));
    for (String item : items)
    {
      jComboBox.addItem(item);
    }

    if(current==null || current.isEmpty() || !items.contains(current))
    {
      current = items.get(0);
    }
    jComboBox.setSelectedItem(current);

    return jComboBox;
  }


  public String getInputValue() {
    return (String) jComboBox.getSelectedItem();
  }

    @Override
    String getDefaultValue()
    {
        return current;
    }

    @Override
    void setDefaultValue(String value)
    {
        this.current = value;
    }

    /**
   * Current value
   *
   * @ant.not-required
   */
  public void setCurrent(String current) {
     setDefaultValue(current);
  }

  /**
   * Comma separated values
   *
   * @ant.required
   */
  public void setListing(String listing) {
    this.listing = listing;
  }

}
