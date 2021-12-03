package org.atac.ant.comm.frs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;


public abstract class AcosFRSAbstractTask extends Task {

  protected String userid;
  protected String password;
  protected String group;
  protected String releasePackage;

  protected void precheck() {
    checkMandatoryAttribute(userid, "userid");
    checkMandatoryAttribute(password, "password");
    checkMandatoryAttribute(group, "group");
    checkMandatoryAttribute(releasePackage, "releaseKey");
  }

  protected void checkMandatoryAttribute(String attribute, String name) {
    if (attribute == null || attribute.length() == 0) {
      throw new BuildException("'" + name + "' attribute is required.");
    }
  }


  /**
   * Acos user
   *
   * @ant.required
   */
  public void setUserid(String userid) {
    this.userid = userid;
  }

  /**
   * Acos password
   *
   * @ant.required
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Acos basedir
   *
   * @ant.required
   */
  public void setGroup(String group) {
    this.group = group;
  }


  /**
   * Key or Package ID of the release.
   *
   * @ant.required
   */
  public void setReleasePackage(String releasePackage) {
    this.releasePackage = releasePackage;
  }

}
