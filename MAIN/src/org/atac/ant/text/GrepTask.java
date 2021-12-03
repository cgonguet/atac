package org.atac.ant.text;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Grep in file.
 * <p/>
 * With 'example.txt':
 * <pre>
 *     blabla
 *     CRi 00012345.89 pipotron
 *     blablabla
 *     CRI AZERTY
 *     CRI 0123456.9087 yahoo
 *     CRI 667788.0
 *     CRi: 1234.123 shouldbefound
 *     the end
 * </pre>
 * <h4>Example 1</h4>
 * <pre>
 *  &lt;grep file='example.txt' property='grep.result'
 *         regexp='.*CR[iI] ([0-9]+\.[0-9]+) (.*)'/&gt;
 *   &lt;echo&gt;Result all line   = ${grep.result}&lt;/echo&gt;
 *   &lt;echo&gt;Result first argument  =  ${grep.result.0}&lt;/echo&gt;
 *   &lt;echo&gt;Result second arg =  ${grep.result.1}&lt;/echo&gt;
 * </pre>
 * Gives:
 * <pre>
 * Result all line   =       CRi 00012345.89 pipotron
 * Result first arg  =  00012345.89
 * Result second arg =  pipotron
 * </pre>
 * <h4>Example 2</h4>
 * <pre>
 *  &lt;grep file='example.txt' property='grep.result'
 *           regexp='.*CR[iI] ([0-9]+\.[0-9]+).*'
 *           tolist=','/&gt;
 *   &lt;echo&gt;List of CRI found = ${grep.result.0}&lt;/echo&gt;
 * </pre>
 * Gives:
 * <pre>
 * List of CRI found = 00012345.89,0123456.9087,667788.0
 * </pre>
 * <h4>Example 3</h4>
 * <pre>
 * &lt;grep file='${tmp.file}' property='grep.result'
 *      regexp='.*CR[iI]:* ([0-9]+\.[0-9]+) (.*)'
 *    tolist=','/&gt;
 * &lt;echo&gt;List of CRI found = ${grep.result.0}&lt;/echo&gt;
 * &lt;echo&gt;List of CRI comment found = ${grep.result.1}&lt;/echo&gt;
 * </pre>
 * Gives:
 * <pre>
 * List of CRI found = 00012345.89,0123456.9087,1234.123
 * List of CRI comment found = pipotron,yahoo,shouldbefound
 * </pre>
 *
 * @ant.task name="grep" category="text" ignore="false"
 */
public class GrepTask extends Task {
  private File file;
  private String regexp;
  private String property;
  String[] availables;
  private String tolist;

  protected void precheck() {
    if (property == null || property.length() == 0) {
      throw new BuildException("'property' attribute is required.");
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
      String out = "";

      List<List<String>> listVal = new ArrayList<List<String>>();

      Pattern p = Pattern.compile(regexp);

      DataInputStream in = new DataInputStream(new FileInputStream(file));
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;

      String content = FileUtils.readFully(new FileReader(file));
      try {
//        while ((strLine = br.readLine()) != null) {
          Matcher matcher = p.matcher(content);
          if (matcher.matches()) {
            out += matcher.group();
            if (matcher.groupCount() != 0) {
              for (int i = 1; i <= matcher.groupCount(); i++) {
                String groupVal = matcher.group(i);
                int index = i - 1;
                if (tolist != null) {
                  if (listVal.size() <= index) {
                    listVal.add(new ArrayList<String>());
                  }
                  listVal.get(index).add(groupVal);
                } else {
                  String groupProp = property + "." + index;
                  getProject().setProperty(groupProp, groupVal);
                  log("Set '" + groupProp + "' to '" + groupVal + "'", Project.MSG_VERBOSE);
                }
              }
              if (tolist == null) {
                return;
              }
            }
            out += '\n';
          }
//        }
      } finally {
        in.close();

        getProject().setProperty(property, out);
        log("Set '" + property + "' to '" + out + "'", Project.MSG_VERBOSE);

        if (tolist != null) {
          for (int i = 0; i < listVal.size(); i++) {
            List<String> values = listVal.get(i);
            String propVal = "";
            for (int j = 0; j < values.size(); j++) {
              propVal += values.get(j) + ((j == values.size() - 1) ? "" : tolist);
            }
            String propName = property + "." + i;
            getProject().setProperty(propName, propVal);
            log("Set '" + propName + "' to '" + propVal + "'", Project.MSG_VERBOSE);
          }
        }

      }
    } catch (FileNotFoundException e) {
      throw new BuildException(e);
    } catch (IOException e) {
      throw new BuildException(e);
    }
  }

  /**
   * The name of a property in which the result should be stored.
   *
   * @ant.required
   */
  public void setProperty(String property) {
    this.property = property;
  }

  /**
   * Regular expression to grep
   *
   * @ant.required
   */
  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

  /**
   * Separator for the list result
   *
   * @ant.not-required if not set then return only the first occurence
   */
  public void setTolist(String separator) {
    this.tolist = separator;
  }

  /**
   * The file to grep into.
   *
   * @ant.required
   */
  public void setFile(File file) {
    this.file = file;
  }

}
