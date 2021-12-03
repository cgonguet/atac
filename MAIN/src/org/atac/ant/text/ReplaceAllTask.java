package org.atac.ant.text;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Replace all occurences of regexp in a value<br>
 * Set the result in a property.
 *
 * <pre>
 *  &lt;replace value='a.b.c.d'
 *              property='replace.result'
 *              regexp='\.'
 *              replacement='/'/&gt;
 *   &lt;echo&gt;${replace.result}&lt;/echo&gt;
 * </pre>
 * Gives:  <pre>a/b/c/d</pre>
 *
 *
 * @ant.task name="replaceall" category="text" ignore="false"
 */
public class ReplaceAllTask extends Task {
  private String value;
  private String regexp;
  private String replacement;
  private String property;

  protected void precheck() {
    if (property == null || property.length() == 0) {
      throw new BuildException("'property' attribute is required.");
    }

    if (value == null) {
      throw new BuildException("'value' attribute is required");
    }
    if (regexp == null) {
      throw new BuildException("'regexp' attribute is required");
    }
    if (replacement == null) {
      throw new BuildException("'replacement' attribute is required");
    }
  }

  public void execute() throws BuildException {
    precheck();
    String output = value.replaceAll(regexp, replacement);
    getProject().setProperty(property, output);
    log("Set '" + property + "' to '" + output + "'", Project.MSG_VERBOSE);
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
   * Text for which the regexp should be replaced.
   *
   * @ant.required
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Regular expression to replace.
   *
   * @ant.required
   */
  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

  /**
   * The new replacement value.
   *
   * @ant.required
   */
  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }

}
