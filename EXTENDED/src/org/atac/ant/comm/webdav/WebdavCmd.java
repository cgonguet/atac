package org.atac.ant.comm.webdav;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;

import java.io.File;

public abstract class WebdavCmd extends ProjectComponent {
  protected File local;
  protected String remote;

  protected void precheck() {
    if (local == null) {
      throw new BuildException("'local' attribute is required");
    }

    if (remote == null) {
      throw new BuildException("'remote' attribute is required");
    }
    remote = remote.replaceAll("\\\\", "/");
  }

  public void run(WebdavMgr mgr) throws Exception {
    precheck();
    execute(mgr);
  }

  protected abstract void execute(WebdavMgr mgr) throws Exception;

  private void putfile(WebdavMgr mgr) throws Exception {
  }

  private void getfile(WebdavMgr mgr) throws Exception {
  }

  /**
   * Local file
   *
   * @ant.required
   */
  public void setLocal(File local) {
    this.local = local;
  }

  /**
   * remote path
   *
   * @ant.required
   */
  public void setRemote(String remote) {
    this.remote = remote;
  }
}
