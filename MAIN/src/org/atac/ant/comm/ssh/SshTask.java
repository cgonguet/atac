package org.atac.ant.comm.ssh;

import com.jcraft.jsch.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * SSH<br>
 * Opens SSH connection to support exec and sftp channels.
 *
 * @ant.task name="ssh" category="comm" ignore="false"
 */
public class SshTask extends Task {
  private String server;
  private String userid;
  private String password;
  private int port = 22;

  private List<SshChannel> channels = new ArrayList<SshChannel>();
  private Session session;

  private void precheck() {
    checkMandatoryAttribute(server, "server");
    checkMandatoryAttribute(userid, "userid");
    checkMandatoryAttribute(password, "password");

    for (SshChannel channel : channels) {
      channel.check();
    }
  }

  private void checkMandatoryAttribute(String attribute, String name) {
    if (attribute == null || attribute.length() == 0) {
      throw new BuildException("'" + name + "' attribute is required.");
    }
  }

  public void execute() throws BuildException {
    precheck();
    try {
      connect();

      for (SshChannel channel : channels) {
        channel.run(session);
      }
    } catch (JSchException e) {
      throw new BuildException(e);
    } finally {
      disconnect();
    }
  }

  private void connect() throws JSchException {
    log("Connect SSH (server=" +
        server +
        " , userid=" +
        userid +
        " , password=" +
        password +
        " , port=" +
        port +
        ")",
        Project.MSG_VERBOSE);

    JSch sch = new JSch();
    session = sch.getSession(userid, server, port);
    session.setPassword(password);
    session.setConfig("StrictHostKeyChecking", "no");

    session.connect();
  }

  private void disconnect() {
    session.disconnect();
    log("Disconnected", Project.MSG_VERBOSE);
  }

  /**
   * Server ip or hostname
   *
   * @ant.required
   */
  public void setServer(String server) {
    this.server = server;
  }

  /**
   * User name
   *
   * @ant.required
   */
  public void setUserid(String userid) {
    this.userid = userid;
  }

  /**
   * Password
   *
   * @ant.required
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Port
   *
   * @ant.not-required  Default is: 22
   */
  public void setPort(int port) {
    this.port = port;
  }

  public void addExecCmd(ExecChannel channel) {
    channels.add(channel);
  }

  public void addSftpCmd(SftpChannel channel) {
    channels.add(channel);
  }

}
