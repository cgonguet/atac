package org.atac.ant.comm.webdav;

import org.apache.tools.ant.Project;

/**
 * Webdav command to get a file.
 *
 * @ant.type name="getfile" category="comm" ignore="false"
 */
public class WebdavCmdGetFile extends WebdavCmd {

  protected void execute(WebdavMgr mgr) throws Exception {
    log("Get file " + remote + " to " + local, Project.MSG_INFO);
    mgr.get(local, remote);
    log("Done", Project.MSG_INFO);
  }

}
