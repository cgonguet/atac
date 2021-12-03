package org.atac.ant.comm.frs;

import org.apache.tools.ant.BuildException;

import java.io.File;


/**
 * Acos Feature Release System<br>
 * Deploy new Acos release file.
 *
 * <p>Create new release for atac.jar</p>
 * <pre>
 *     &lt;property file='${user.home}/private.properties'/&gt;
 *     &lt;acosfrs userid='${acos.username}'
 *              password='${acos.password}'
 *              group='atac'
 *              releasepackage='atac'
 *              releasename='1.8'
 *              releasefilename='atac.jar'
 *              releasefile='${basedir}/atac.jar'
 *              releasenotes='this is the new release note'
 *              releasechanges='here are the changes'/&gt;
 * </pre>
 * <p>With the private property file private.properties:</p>
 * <pre>
 * acos.username=__acos_user__
 * acos.password=__acos_password__
 * </pre>
 *
 * @ant.task name="acosfrs" category="comm" ignore="false"
 */
public class AcosFRSCreateTask extends AcosFRSAbstractTask {

  private String releaseName;
  private String releaseFilename;
  private File releaseFile;
  private String releaseNotes = "";
  private String releaseChanges = "";

  protected void precheck() {
    super.precheck();

    checkMandatoryAttribute(releaseName, "releaseName");
    checkMandatoryAttribute(releaseFilename, "releaseFilename");

    if (releaseFile == null) {
      throw new BuildException("'releaseFile' attribute is required");
    }
    if (!releaseFile.exists()) {
      throw new BuildException("'releaseFile' does not exist");
    }
  }

  public void execute() throws BuildException {
    precheck();
    new AcosFeatureReleaseSystemMgr(getProject(),null,
                                    userid,password,group,
                                    releasePackage,releaseName,
                                    releaseFilename,releaseFile.getAbsolutePath(),
                                    releaseNotes, releaseChanges).createRelease();
   }

  /**
   * Name of the remote release file.
   *
   * @ant.required
   */
  public void setReleaseFilename(String releaseFilename) {
    this.releaseFilename = releaseFilename;
  }

  /**
   * Name of the release.
   *
   * @ant.required
   */
  public void setReleaseName(String releaseName) {
    this.releaseName = releaseName;
  }

  /**
   * File to release.
   *
   * @ant.required
   */
  public void setReleaseFile(File releaseFile) {
    this.releaseFile = releaseFile;
  }


  /**
   * Notes for the release.
   *
   * @ant.not-required
   */
  public void setReleaseNotes(String releaseNotes) {
    this.releaseNotes = releaseNotes;
  }

  /**
   * Changes for the release.
   *
   * @ant.not-required
   */
  public void setReleaseChanges(String releaseChanges) {
    this.releaseChanges = releaseChanges;
  }
}
