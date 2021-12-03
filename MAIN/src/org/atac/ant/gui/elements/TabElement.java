package org.atac.ant.gui.elements;

import org.apache.tools.ant.BuildException;
import org.atac.ant.gui.GuiAggregatorTask;
import org.atac.ant.gui.elements.input.FileChooserInputElement;
import org.atac.ant.gui.elements.input.ListInputElement;
import org.atac.ant.gui.elements.input.TextInputElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Tab element represented by a JTabbedPane.
 *
 * @ant.type name="gtab" category="gui" ignore="false"
 */
public class TabElement extends AbstractGuiElement {
  String title;
  private int vgap = GuiAggregatorTask.DEFAULT_VGAP;
  private ArrayList<AbstractGuiElement> elements = new ArrayList<AbstractGuiElement>();
  private JComponent tabPane;

  public JComponent getPane() {
    tabPane = GuiAggregatorTask.getVerticalLayoutPane(elements, vgap);
    return tabPane;
  }

  @Override
  public void precheck() {
    if(title==null){
      throw new BuildException("Tab title is required");
    }
    for (AbstractGuiElement element : elements) {
      element.precheck();
    }
  }

  @Override
  public void validate() {
    for (AbstractGuiElement element : elements) {
      element.validate();
    }
  }

  @Override
  public void postAdd(Component parent)
  {
    for (AbstractGuiElement element : elements) {
      element.postAdd(tabPane);
    }
  }

  /**
   * Title content
   *
   * @ant.required
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public void applyVgap(int vgap) {
    this.vgap = vgap;
  }

  public void addGInText(TextInputElement element) {
    elements.add(element);
  }

  public void addGInfile(FileChooserInputElement element) {
    elements.add(element);
  }

  public void addGInlist(ListInputElement element) {
    elements.add(element);
  }

  public void addGFileEditor(FileEditorElement element) {
    elements.add(element);
  }

  public void addGText(TextElement element) {
    elements.add(element);
  }

  public void addGLabel(LabelElement element) {
    elements.add(element);
  }

  public void addGSeparator(SeparatorElement element) {
    elements.add(element);
  }

  public void addGSpare(SpareElement element) {
    elements.add(element);
  }

  public String getTitle() {
    return title;
  }
}
