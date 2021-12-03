package org.atac.ant.comm.webdav;

import org.apache.tools.ant.Project;

/**
 * Webdav command to put a file.
 *
 * @ant.type name="putfile" category="comm" ignore="false"
 */
public class WebdavCmdPutFile extends WebdavCmd{

  protected void execute(WebdavMgr mgr) throws Exception {
    log("Transfert file " + local + " to " + remote, Project.MSG_INFO);
    mgr.transfertFile(remote, local);
    log("Done", Project.MSG_INFO);
  }

}
