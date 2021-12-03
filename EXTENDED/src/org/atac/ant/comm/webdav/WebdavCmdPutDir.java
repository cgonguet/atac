package org.atac.ant.comm.webdav;

import org.apache.tools.ant.Project;

/**
 * Webdav command to put a directory.
 *
 * @ant.type name="putdir" category="comm" ignore="false"
 */
public class WebdavCmdPutDir extends WebdavCmd{

  protected void execute(WebdavMgr mgr) throws Exception {
    log("Transfert directory " + local + " to " + remote, Project.MSG_INFO);
    mgr.transfertDir(local, remote);
    log("Done", Project.MSG_INFO);
  }

}
