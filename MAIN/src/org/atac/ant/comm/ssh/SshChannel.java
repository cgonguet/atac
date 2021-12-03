package org.atac.ant.comm.ssh;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public abstract class SshChannel extends ProjectComponent {
  private int timeout = -1;
  private boolean failonerror = true;
  protected int traceLevel = Project.MSG_VERBOSE;
  Channel sshChannel;
  private String channelType;


  protected SshChannel(String channelType) {
    this.channelType = channelType;
  }

  public abstract void check();
  
  public void run(Session session) {
    Thread timeoutThread = null;
    try {
      sshChannel = session.openChannel(channelType);
      log("Run : " + channelType, Project.MSG_VERBOSE);

      sshChannel.setInputStream(null, true);
      sshChannel.setOutputStream(null, true);


      new ReaderThread(sshChannel.getInputStream(), new ReaderThread.OutputManager() {

        public void readLine(String line) {
          log(line, traceLevel);
        }
      }).start();

      if (timeout > 0) {
        timeoutThread = new Thread(new Runnable() {

          public void run() {
            try {
              Thread.sleep(timeout * 1000);
              log("Timeout occurs while executing command", Project.MSG_ERR);
              sshChannel.disconnect();
            } catch (InterruptedException e) {
            }
          }
        });
        timeoutThread.start();
      }

      runChannel();

    } catch (Exception e) {
      log("Execution fails: " + e.getMessage(), Project.MSG_WARN);
      if (failonerror) {
        throw new BuildException(e);
      }
    } finally {
      if (timeoutThread != null) {
        timeoutThread.interrupt();
      }
      sshChannel.disconnect();
    }

  }

  abstract void runChannel() throws Exception;


  /**
   * If true the task fails if the command fails
   *
   * @ant.not-required Default is: true
   */
  public void setFailonerror(boolean failonerror) {
    this.failonerror = failonerror;
  }

  /**
   * Timeout
   *
   * @ant.not-required Default is: no timeout
   */
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }


  /**
   * If true the output of the command is logged
   *
   * @ant.not-required Default is: false
   */
  public void setTrace(boolean enableTrace) {
    if (enableTrace) {
      traceLevel = Project.MSG_INFO;
    }
  }
}
