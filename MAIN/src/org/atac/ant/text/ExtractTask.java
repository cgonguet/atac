package org.atac.ant.text;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.*;

/**
 * Extract from file.
 *
 * <p/>
 * With 'example.txt':
 * <pre>
 *    startsection 1 name=section_1 description=desc_for_section_1
 *      action 11
 *        line 111
 *        line 112
 *      endaction 11
 *      action 12
 *        line 121
 *        line 122
 *      endaction 12
 *    endsection 1
 *    startsection 2
 *      action 21
 *        line 211
 *        line 212
 *      endaction 21
 *      action 22
 *        line 221
 *        line 222
 *      endaction 22
 *    endsection 2
 *  </pre>
 * <p/>
 * <h4>Example 1:</h4>
 * <pre>
 *  &lt;extract file='example.txt'
 *           property='extract.result'
 *           from='[ \t]*startsection.+'
 *           to='[ \t]*endsection.*'
 *           keep='1'
 *           includelimits='true'/&gt;
 *  &lt;echo&gt;${extract.result}&lt;/echo&gt;
 * </pre>
 * Gives:
 * <pre>
 *    startsection 1 name=section_1 description=desc_for_section_1
 *      action 11
 *        line 111
 *        line 112
 *      endaction 11
 *      action 12
 *        line 121
 *        line 122
 *      endaction 12
 *    endsection 1
 * </pre>
 * <h4>Example 2:</h4>
 * <pre>
 *  &lt;extract file='example.txt'
 *           property='extract.result'
 *           from='[ \t]*action.+'
 *           to='[ \t]*endaction.*'
 *           keep='3'
 *           includelimits='false'
 *           separator='__NEWLINE__  This is a new action: __NEWLINE__'/&gt;
 *  &lt;echo&gt;${extract.result}&lt;/echo&gt;
 * </pre>
 * Gives:
 * <pre>
 * This is a new action:
 *        line 111
 *        line 112
 *
 * This is a new action:
 *        line 121
 *        line 122
 *
 * This is a new action:
 *        line 211
 *        line 212
 * </pre>
 *
 * @ant.task name="extract" category="text" ignore="false"
 */
public class ExtractTask extends Task {
  private File file;
  private String from;
  private String to;
  private boolean includeLimits = false;
  private int keep = 1;
  private String separator;
  private String property;

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

      DataInputStream in = new DataInputStream(new FileInputStream(file));
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      try {

        int found = 0;
        boolean bufferize = false;
        while ((strLine = br.readLine()) != null) {

          if (strLine.matches(from)) {
            if(separator !=null){
              out+= separator.replaceAll("__NEWLINE__","\n");
            }
            if (includeLimits) {
              out += strLine;
              out += '\n';
            }
            bufferize = true;
            continue;
          }
          if (strLine.matches(to)) {

            if (includeLimits) {
              out += strLine;
              out += '\n';
            }
            bufferize = false;
            found++;
            if(found==keep){
              break;
            }
          }
          if (bufferize) {
            out += strLine;
            out += '\n';
          }
        }
      } finally {
        in.close();

        getProject().setProperty(property, out);
        log("Set '" + property + "' to '" + out + "'", Project.MSG_VERBOSE);
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
   * File from which the data should be extracted.
  *
  * @ant.required
  */
  public void setFile(File file) {
    this.file = file;
  }


  /**
   * Regular expression. Extraction starts from this.
  *
  * @ant.not-required
  */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * Regular expression. Extraction stops to this.
  *
  * @ant.not-required
  */
  public void setTo(String to) {
    this.to = to;
  }

  /**
   * If true the limits (from,to) are included in the result.
  *
  * @ant.not-required
  */
  public void setIncludeLimits(boolean includeLimits) {
    this.includeLimits = includeLimits;
  }

  /**
   * Number of occurence to keep .
  *
  * @ant.not-required
  */
  public void setKeep(int keep) {
    this.keep = keep;
  }

  /**
   * List separator in case of multiple occurence to keep.
   * The keyword '__NEWLINE__' adds a new line in the result.
  *
  * @ant.not-required
  */
  public void setSeparator(String separator) {
    this.separator = separator;
  }
}
