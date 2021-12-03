package org.atac.ant.gui;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.atac.ant.gui.elements.AbstractGuiElement;
import org.atac.ant.gui.elements.ConsoleElement;
import org.atac.ant.gui.elements.FileEditorElement;
import org.atac.ant.gui.elements.LabelElement;
import org.atac.ant.gui.elements.SeparatorElement;
import org.atac.ant.gui.elements.SpareElement;
import org.atac.ant.gui.elements.TabElement;
import org.atac.ant.gui.elements.TextElement;
import org.atac.ant.gui.elements.input.AbstractGuiInputElement;
import org.atac.ant.gui.elements.input.FileChooserInputElement;
import org.atac.ant.gui.elements.input.ListInputElement;
import org.atac.ant.gui.elements.input.TextInputElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * GUI Aggregator<br>
 * Opens a JFrame and aggregates some other Swing elements.
 * <p/>
 * <p>The execution of:</p>
 * <pre>
 *  &lt;gui title='Test GUI Aggregator' vgap='10' fontSize='16'&gt;
 *     &lt;glabel text='Welcome to GUI Aggregator !'
 *                alignment='center'
 *                fontStyle='bold-italic' fontSize='24'/&gt;
 *     &lt;gseparator/&gt;
 *     &lt;gintext focus='true'
 *                 label='Choose a text value: '
 *                 text='AAAAAAA'
 *                 property='test.result.text'/&gt;
 *     &lt;gspare/&gt;
 *     &lt;glabel text='Right label' alignment='right'/&gt;
 *     &lt;gspare/&gt;
 *     &lt;glabel text='---- This is a label ---'/&gt;
 *     &lt;ginfile label='Choose a file: '
 *              file='test/data/fileForEditor.txt'
 *              property='test.result.file'
 *             type='both' filter='jar,xml'/&gt;
 *     &lt;gseparator/&gt;
 *     &lt;ginlist focus='true'
 *                 label='choose in the list: '
 *                 listing='AA,BB,CC,DD'
 *                 current='BB'
 *                 property='test.result.list'/&gt;
 *   &lt;/gui&gt;
 * </pre>
 * <p>Gives:</p>
 * <img src="guiaggregator.jpg"/>
 *
 * @ant.task name="gui" category="gui" ignore="false"
 */
public class GuiAggregatorTask extends Task {

  public static Map<String, Integer> fontStyles = new HashMap<String, Integer>();

  static {
    fontStyles.put("plain", Font.PLAIN);
    fontStyles.put("bold", Font.BOLD);
    fontStyles.put("italic", Font.ITALIC);
    fontStyles.put("bold-italic", Font.BOLD + Font.ITALIC);
  }

//  public static final String DEFAULT_TEXT_FONT = "courier";
//  public static final int DEFAULT_TEXT_FONT_SIZE = 12;

  public static final int DEFAULT_VGAP = 5;

  private ArrayList<AbstractGuiElement> elements = new ArrayList<AbstractGuiElement>();
  private ArrayList<TabElement> tabs = new ArrayList<TabElement>();

  private int width = -1;
  private int height = -1;
  private String title = "Gui Aggregator for ANT";
  private int vgap = DEFAULT_VGAP;
  private boolean failOnCancel = true;

    private File save;
    private Properties savedProperties = new Properties();

    private String fontName = UIManager.getFont("Label.font").getName();
  private int fontSize = UIManager.getFont("Label.font").getSize();
  private int fontStyle = UIManager.getFont("Label.font").getStyle();
  private ConsoleElement console;
  private ValidationChoice choice;
  private JFrame mainFrame;
  private JTabbedPane tabPane;

  public void execute() throws BuildException {
    try {
      precheck();
      createAndShowGui();
    } catch (Exception e) {
      throw new BuildException(e);
    }
  }


  private void precheck() {
    if (console != null && tabs.isEmpty()) {
      throw new BuildException("Must have tabs with console");
    }

    if (!tabs.isEmpty() && !elements.isEmpty()) {
      throw new BuildException("Elements should been add under tabs");
    }

    for (TabElement tab : tabs) {
      tab.precheck();
    }

    for (AbstractGuiElement element : elements) {
      element.precheck();
    }

    if (console != null) {
      console.precheck();
    }

      loadSavedProperties();
  }

    private void loadSavedProperties()
    {
        if(save != null && save.exists())
        {
            try
            {
                savedProperties.load(new FileReader(save));
            }
            catch (IOException e)
            {
                log("Error while loading saved properties file '" + save.getName() + "'\n" + e.getMessage(),
                        Project.MSG_ERR);
                return;
            }

            for (AbstractGuiElement element : elements)
            {
                if (element instanceof AbstractGuiInputElement)
                {
                    AbstractGuiInputElement inputElement = (AbstractGuiInputElement) element;
                    inputElement.load(savedProperties);
                }
            }
        }
    }

    private void writeSavedProperties()
    {
        if(save != null)
        {
            for (AbstractGuiElement element : elements)
            {
                if (element instanceof AbstractGuiInputElement)
                {
                    AbstractGuiInputElement inputElement = (AbstractGuiInputElement) element;
                    inputElement.save(savedProperties);
                }
            }

            try
            {
                FileWriter writer = new FileWriter(save);
                savedProperties.store(writer, "");
                writer.close();
            }
            catch (IOException e)
            {
                log("Error while writing saved properties to file '" + save.getName() + "'\n" + e.getMessage(),
                        Project.MSG_ERR);
            }
        }
    }

    private void createAndShowGui() throws Exception {

    mainFrame = new JFrame();
    mainFrame.setTitle(title);

    mainFrame.getContentPane().add(getUserPane(), BorderLayout.CENTER);

    if (console == null) {
      choice = new ValidationChoice();
    } else {
      choice = new ValidationChoice("Execute", "Close");
    }

    mainFrame.getContentPane().add(choice, BorderLayout.SOUTH);

    mainFrame.pack();
    updateFrameSize();
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          for (TabElement tab : tabs)
          {
            tab.postAdd(mainFrame);
          }
          for (AbstractGuiElement element : elements)
          {
            element.postAdd(mainFrame);
          }

    mainFrame.setVisible(true);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for (AbstractGuiElement element : tabs) {
          if (element.requestFocus()) {
            return;
          }
        }

        for (AbstractGuiElement element : elements) {
          if (element.requestFocus()) {
            return;
          }
        }
      }
    });
    proceed();
  }

  private JComponent getUserPane() {

     if (tabs.isEmpty()) {
       return getVerticalLayoutPane(elements, vgap);
     }

     return getTabsPane();

   }

  private JComponent getTabsPane() {
     tabPane = new JTabbedPane();
     tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
     for (TabElement tab : tabs) {
       addTab(tab);
     }
     if (console != null) {
       tabPane.addTab("Console", console.getPane());
     }
     return tabPane;
   }


  private int tabMaxWidth = 0;
  private int tabMaxHeight = 0;

  private void addTab(TabElement tab)
  {
    tab.applyVgap(vgap);
    JComponent pane = tab.getPane();
    tabMaxWidth = (int) Math.max(tabMaxWidth, pane.getPreferredSize().getWidth());
    tabMaxHeight = (int) Math.max(tabMaxHeight, pane.getPreferredSize().getHeight());
    tabPane.addTab(tab.getTitle(), pane);
  }

  public static JComponent getVerticalLayoutPane(ArrayList<AbstractGuiElement> aInElements, int aInVgap)
  {

    boolean hasInput = false;
    for (AbstractGuiElement element : aInElements)
    {
      if (element instanceof AbstractGuiInputElement)
      {
        hasInput = true;
        break;
      }
    }

    JPanel userPane = new JPanel();
    userPane.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(1, 1, aInVgap, 1);
    gbc.gridx = 0;
    gbc.weightx = 1.0;
    int row = 0;
    for (AbstractGuiElement element : aInElements)
    {
      gbc.gridy = row++;
      gbc.fill = element.getFillBehavior();
      if(gbc.fill == GridBagConstraints.BOTH || gbc.fill == GridBagConstraints.VERTICAL)
      {
        gbc.weighty = 1.0;
      }
      else
      {
        gbc.weighty = 0.0;
      }

      if (hasInput)
      {
        if (element instanceof AbstractGuiInputElement)
        {
          AbstractGuiInputElement inputElement = (AbstractGuiInputElement) element;
          gbc.gridx = 0;
          gbc.insets = new Insets(1, 1, aInVgap, 1);
          gbc.gridwidth = 1;
          gbc.anchor = GridBagConstraints.WEST;
          gbc.weightx = 0.0;
          userPane.add(inputElement.getLabel(), gbc);
          gbc.gridx = 1;
          gbc.insets = new Insets(1, 10, aInVgap, 0);
          gbc.gridwidth = 2;
          gbc.anchor = GridBagConstraints.CENTER;
          gbc.weightx = 1.0;
          userPane.add(inputElement.getInput(), gbc);
        }
        else
        {
          gbc.insets = new Insets(1, 1, aInVgap, 1);
          gbc.gridx = 0;
          gbc.gridwidth = 3;
          gbc.anchor = GridBagConstraints.CENTER;
          gbc.weightx = 1.0;
          userPane.add(element.getPane(), gbc);
        }
      }
      else
      {
        userPane.add(element.getPane(), gbc);
      }
    }
     return new JScrollPane(userPane);
   }

  private static class ValidationChoice extends JPanel {
    public static final int OK = 0;
    public static final int CANCEL = 1;

    JButton[] buttons = new JButton[2];
    Object sync = new Object();
    int result;

    public ValidationChoice() {
      this("OK", "Cancel");
    }

    public ValidationChoice(String ok, String cancel) {
      addButton(new Action(OK, ok), "West");
      addButton(new Action(CANCEL, cancel), "East");
    }

    public int waitForChoice() throws InterruptedException {
      synchronized (sync) {
        sync.wait();
      }
      return result;
    }

    private void addButton(final Action action, final String pos) {
      JButton button = new JButton(new AbstractAction(action.name) {
        public void actionPerformed(ActionEvent e) {
          result = action.val;
          synchronized (sync) {
            sync.notifyAll();
          }
        }
      });
      buttons[action.val] = button;
      add(button, pos);
    }

    public void enabled(boolean enabled){
      buttons[OK].setEnabled(enabled);
      buttons[CANCEL].setEnabled(enabled);
    }

    public static class Action {
      int val;
      String name;

      public Action(int val, String name) {
        this.val = val;
        this.name = name;
      }
    }
  }

  private void validate() {
    for (TabElement tab : tabs) {
      tab.validate();
    }

    for (AbstractGuiElement element : elements) {
      element.validate();
    }

      writeSavedProperties();
  }

  private void proceed() throws InterruptedException {
    int result = choice.waitForChoice();
    switch (result) {
      case ValidationChoice.OK:
        log("Validate", Project.MSG_VERBOSE);
        validate();
        if (console == null) {
          exit(mainFrame, "OK");
          return;
        }

        choice.enabled(false);
        focusConsole();
        console.execute();
        choice.enabled(true);

        proceed();
        break;
      case ValidationChoice.CANCEL:
        if (failOnCancel) {
          throw new BuildException("Cancel");
        }
        exit(mainFrame, "Cancel");
        break;
      default:
        throw new BuildException("Internal Error");
    }
  }

  private boolean exit(JFrame mainFrame, String choice) {
    log("Exit on '" + choice + "'", Project.MSG_VERBOSE);
    mainFrame.setVisible(false);
    mainFrame.dispose();
    return true;
  }

  private void focusConsole() {
    if (console != null) {
      tabPane.setSelectedIndex(tabs.size());
    }
  }

  /**
   * GUI Title
   *
   * @ant.not-required Default is: 'Gui Aggregator for ANT'
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Vertical gap
   *
   * @ant.not-required Default is: 5
   */
  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  private void updateFrameSize() {
    if (width != -1 && height != -1) {
      mainFrame.setSize(width, height);
      return;
    }

    if(tabPane !=null)
    {
      Dimension maxDim = new Dimension(tabMaxWidth + 2, tabMaxHeight + 2);
      for (int i = 0; i < tabPane.getTabCount(); i++)
      {
         tabPane.getComponentAt(i).setPreferredSize(maxDim);
      }
      mainFrame.pack();
    }

  }

  /**
   * GUI size: &lt;width&gt;x&lt;height&gt;
   *
   * @ant.not-required
   */
  public void setSize(final String dim) {
    if (!dim.matches("[0-9]+x[0-9]+")) {
      throw new BuildException("'size' attribute should match <width>x<height> with <width> and <height> two integer");
    }
    String[] dims = dim.split("x");
    width = Integer.valueOf(dims[0]);
    height = Integer.valueOf(dims[1]);
  }

  /**
   * If true the execution fails on cancel
   *
   * @ant.not-required Default is: true
   */
  public void setFailOnCancel(boolean failOnCancel) {
    this.failOnCancel = failOnCancel;
  }


  /**
   * GUI Font name
   *
   * @ant.not-required Default is: UI default
   */
  public void setFontName(final String fontName) {
    this.fontName = fontName;
    setDefaultFont();
  }

  /**
   * GUI Font style
   *
   * @ant.not-required Default is: UI default
   */
  public void setFontStyle(final String style) {
    Integer decodeFontStyle = GuiAggregatorTask.fontStyles.get(style);
    if (decodeFontStyle == null) {
      throw new BuildException("'fontStyle' should be in " + GuiAggregatorTask.fontStyles.keySet());
    }
    this.fontStyle = decodeFontStyle;
    setDefaultFont();
  }

  /**
   * GUI Font size
   *
   * @ant.not-required Default is: UI default
   */
  public void setFontSize(final int fontSize) {
    this.fontSize = fontSize;
    setDefaultFont();
  }

  private void setDefaultFont() {
    Font font = new Font(fontName, fontStyle, fontSize);
    UIManager.put("Label.font", font);
    UIManager.put("Button.font", font);
    UIManager.put("TextArea.font", font);
  }


    /**
     * Saved properties file
     *
     * @ant.not-required
     */
    public void setSave(final File savedPropertiesFile) {
        this.save = savedPropertiesFile;
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

  public void addGTab(TabElement tab) {
    tabs.add(tab);
  }

  public void addGConsole(ConsoleElement console) {
    this.console = console;
  }
}
