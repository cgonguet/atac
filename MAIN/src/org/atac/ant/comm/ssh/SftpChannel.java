package org.atac.ant.comm.ssh;

import com.jcraft.jsch.ChannelSftp;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * SSH SFTP Channel<br>
 * Allow to get or put a file.
 *
 * @ant.type name="sftp" category="comm" ignore="false"
 */
public class SftpChannel extends SshChannel {
  private File local;
  private String remote;
  private String action;

  public SftpChannel() {
    super("sftp");
  }

  /**
   * Local file
   *
   * @ant.required
   */
  public void setLocal(File local) {
    this.local = local.getAbsoluteFile();
  }

  /**
   * Remote path
   *
   * @ant.required
   */
  public void setRemote(String remote) {
    this.remote = remote;
  }

  /**
   * Sftp action (get|put)
   *
   * @ant.required
   */
  public void setAction(String action) {
    if ("get".equals(action) || "put".equals(action)) {
      this.action = action;
      return;
    }
    throw new BuildException("'action' attribute should be in: [get,put]");
  }


  public void check() {
    if (local == null || !local.exists()) {
      throw new BuildException("'local' attribute is mandatory");
    }

    if (remote == null || remote.length() == 0) {
      throw new BuildException("'local' attribute is mandatory");
    }

    if (action == null) {
      throw new BuildException("'action' attribute is mandatory");
    }

  }

  void runChannel() throws Exception {
    ChannelSftp channelSftp = (ChannelSftp) sshChannel;
    channelSftp.connect();
    if (action.equals("put")) {
      log("Sftp put local:'" + local + "' to remote:'" + remote + "'", traceLevel);
      channelSftp.put(local.getPath(), remote);
    } else {
      log("Sftp get remote:'" + remote + "' to local:'" + local + "'", traceLevel);
      channelSftp.get(remote, local.getPath());
    }
  }

}
