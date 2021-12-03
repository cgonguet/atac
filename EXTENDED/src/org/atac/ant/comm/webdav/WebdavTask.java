package org.atac.ant.comm.webdav;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Webdav connection for Acos.
 * <p/>
 * <p>Publish docs to Acos web site:</p>
 * <pre>
 * &lt;webdav basedir='groups/atac/'&gt;
 *   &lt;putdir local='${basedir}/output/html' remote='www'/&gt;
 * &lt;/webdav&gt;
 * </pre>
 *
 * @ant.task name="webdav" category="comm" ignore="false"
 */
public class WebdavTask extends Task {

  static final String HTTPS_PROTOCOL = "https";
  static final int SSL_PORT = 443;
  static final String ACOS_URL = "acos.alcatel-lucent.com";


  private List<WebdavCmd> cmds = new ArrayList<WebdavCmd>();

  private String protocol = HTTPS_PROTOCOL;
  private String server = ACOS_URL;
  private int port = SSL_PORT;
  private String basedir;


  protected void precheck() {
    if (basedir == null) {
      throw new BuildException("'basedir' attribute is required");
    }
    basedir = basedir.replaceAll("\\\\", "/");
  }


  public void execute() throws BuildException {
    precheck();
    try {
      WebdavMgr webdavMgr = new WebdavMgr(getProject(), protocol, server, port, basedir);
      webdavMgr.connect();
      for (WebdavCmd cmd : cmds) {
        cmd.run(webdavMgr);
      }

    } catch (Exception e) {
      throw new BuildException(e);
    }
  }


  public void addPutDir(WebdavCmdPutDir cmd) {
    cmds.add(cmd);
  }

  public void addPutFile(WebdavCmdPutFile cmd) {
    cmds.add(cmd);
  }

  public void addGetFile(WebdavCmdGetFile cmd) {
    cmds.add(cmd);
  }

  /**
   * Protocol (http, https)
   *
   * @ant.not-required Default is: https
   */
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  /**
   * Server address
   *
   * @ant.not-required Default is: acos.alcatel-lucent.com
   */
  public void setServer(String server) {
    this.server = server;
  }

  /**
   * Port
   *
   * @ant.not-required Default is: 443
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Remote basedir (ex: groups/myproject/www/)
   *
   * @ant.required
   */
  public void setBasedir(String basedir) {
    this.basedir = basedir;
  }


}
