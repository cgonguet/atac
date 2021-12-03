package org.atac.ant.misc;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyze a Findbugs XML file and display the result in a console
 * in order to link each bug with the concerned file/line.
 * <p/>
 * <p>Example of result in an IntelliJ console:</p>
 * <img src="findbugs_console.jpg"/>
 *
 * @ant.task name="findbugs" category="misc" ignore="false"
 */
public class FindbugsDisplayTask extends Task {
  private File file;
  private String property;
  private String out = "";
  private int nbBugs = 0;

  protected void precheck() {
    if (property != null && property.length() == 0) {
      throw new BuildException("'property' attribute bad format");
    }

    if (file == null) {
      throw new BuildException("'file' attribute is required");
    }
    if (!file.exists()) {
      throw new BuildException("File does not exist");
    }
  }


  public void execute() throws BuildException {
    precheck();
    try {

      Pattern patternFile = Pattern.compile("<file classname=\"(.+)\".*");
      Pattern patternBug = Pattern.compile(
              "<BugInstance type=\"(.+)\" priority=\"(.+)\" category=\"(.+)\" message=\"(.*)\" lineNumber=\"(.+)\".*");

      DataInputStream in = new DataInputStream(new FileInputStream(file));
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      String classname = "";
      String filename = "";
      String method = "";

      try {
        while ((strLine = br.readLine()) != null) {

          Matcher matcher = patternFile.matcher(strLine);
          if (matcher.matches()) {
            classname = matcher.group(1);
            filename = decodeFilename(classname);
          }

          matcher = patternBug.matcher(strLine);
          if (matcher.matches()) {
            nbBugs++;

            print("BUG PATTERN: " + matcher.group(1));

            String bugDesc = matcher.group(4);

            Matcher bugCodeMatcher = Pattern.compile("(.+):.*").matcher(bugDesc);
            if (bugCodeMatcher.matches()) {
              print("BUG CODE   : " + bugCodeMatcher.group(1));
            }

            print("PRIORITY   : " + matcher.group(2));

            print(bugDesc);

            String lineNumber = matcher.group(5).split("-")[0];
            if (lineNumber.equals("Not available")) {
              lineNumber = "0";
            }

            print(decodeMessage(classname, bugDesc, filename, lineNumber));

          }

        }
        log("Found " + nbBugs + " bugs");
        if (property != null) {
          log("Set property: " + property);
          getProject().setProperty(property, out);
        }

      } finally {
        in.close();

      }
    } catch (FileNotFoundException e) {
      throw new BuildException(e);
    } catch (IOException e) {
      throw new BuildException(e);
    }
  }

  private String decodeFilename(String classname) {
    String[] atomics = classname.split("\\.");
    return atomics[atomics.length-1].split("\\$")[0]+ ".java";

  }

  private String decodeMessage(String classname, String bugDesc, String filename, String lineNumber) {

    Matcher matcher;

    // interface
    matcher = Pattern.compile(".*" + classname + " .*").matcher(bugDesc);
    if (matcher.matches()) {
      return "\tat " + classname  + "(" + filename + ":" + lineNumber + ")\n";
    }

    // method
    matcher = Pattern.compile(".*" + classname + "\\.([^\\(]+)\\(.*").matcher(bugDesc);
    if (matcher.matches()) {
      return "\tat " + classname + "." + matcher.group(1) + "(" + filename + ":" + lineNumber + ")\n";
    }

    // field
    matcher = Pattern.compile(".*" + classname + "\\.(.+) ").matcher(bugDesc);
    if (matcher.matches()) {
      return "\tat " + classname + "(" + filename + ":" + lineNumber + ")\n";
    }
    // subclass
    matcher = Pattern.compile(".*" + classname + "\\$(.+)[ $].*").matcher(bugDesc);
    if (matcher.matches()) {
      return "\tat " + classname + "." + matcher.group(1) + "(" + filename + ":" +
             lineNumber + ")\n";
    }

    // filename/classname does not match description
    matcher = Pattern.compile(".+:[^\\.]* ([\\.\\S]*)[\\$\\.].*").matcher(bugDesc);
    if (matcher.matches()) {
      classname = matcher.group(1);
      filename = decodeFilename(classname);
      return "\tat " + classname + ".pipo" + "(" + filename + ":" + lineNumber + ")\n";
    }

    return "\n";

  }


  /**
   * Findbugs XML result file
   *
   * @ant.required
   */
  public void setFile(File file) {
    this.file = file;
  }

  /**
   * The name of a property in which the result should be stored.
   *
   * @ant.not-required If not set then just display the result to the console
   */
  public void setProperty(String property) {
    this.property = property;
  }


  private void print(String message) {
    if (property == null) {
      log(message);
      return;
    }
    out += message + "\n";
    log(message, Project.MSG_VERBOSE);
  }


}
