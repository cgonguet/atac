package org.atac.ant.comm.ssh;

import org.apache.tools.ant.Project;

import com.jcraft.jsch.ChannelExec;

/**
 * SSH EXEC Channel.
 *
 * @ant.type name="exec" category="comm" ignore="false"
 */
public  class ExecChannel extends SshChannel {
  private String execCommand = "";

  public ExecChannel() {
    super("exec");
  }

  /**
   * Commands to execute (one command per line).
   * 
   * @ant.required
   */
  public void addText(String command) {
    command = getProject().replaceProperties(command);
    log("Command: " + command, Project.MSG_VERBOSE);
    String[] lines = command.split("\n");
    for (String line : lines) {
      line = line.trim();
      if (!line.equals("")) {
        execCommand += line + ";";
      }
    }
    execCommand = execCommand.substring(0, execCommand.length() - 1);
  }

  public void check() {
    
  }

  void runChannel() throws Exception {
    ChannelExec channelExec = (ChannelExec) sshChannel;

    channelExec.setCommand(execCommand + "\n");

    new ReaderThread(channelExec.getErrStream(), new ReaderThread.OutputManager() {

      public void readLine(String line) {
        log(line, Project.MSG_WARN);
      }
    }).start();

    channelExec.connect();

    while (true) {
      if (channelExec.isClosed()) {
        if (channelExec.getExitStatus() != 0) {
          String errorMsg = "Execution fails with exit-status: " + channelExec.getExitStatus();
          throw new Exception(errorMsg);
        } else {
          log("Execution OK", Project.MSG_VERBOSE);
        }
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (Exception ee) {
      }
    }

  }
}
