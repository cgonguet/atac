package org.atac.ant.comm.frs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.List;


/**
 * Acos Feature Release System Get Releases<br>
 * Get Releases.
 *
 * @ant.task name="acosfrsget" category="comm" ignore="false"
 */
public class AcosFRSGetReleasesTask extends AcosFRSAbstractTask {

  private String property;
  private boolean last = false;

  protected void precheck() {
    super.precheck();

    checkMandatoryAttribute(property, "property");
  }

  public void execute() throws BuildException {
    precheck();
    List<String> releases = new AcosFeatureReleaseSystemMgr(getProject(), null,
                                                            userid, password, group,
                                                            releasePackage).getReleases();

    log("Get Releases: " + releases, Project.MSG_VERBOSE);
    String out = "";
    if (last) {
      out = releases.get(releases.size() - 1);
    } else {
      for (String release : releases) {
        out += release + ',';
      }
      out = out.substring(0, out.length() - 1);
    }

    getProject().setProperty(property, out);
    log("Set '" + property + "' to '" + out + "'", Project.MSG_VERBOSE);

  }

  /**
   * The name of a property in which the result should be stored.
   *
   * @ant.not-required If not set then just display the result to the console
   */
  public void setProperty(String property) {
    this.property = property;
  }

  /**
   * If true: get only the last release name
   *
   * @ant.not-required Default is: false
   */
  public void setLast(boolean last) {
    this.last = last;
  }

}
