package org.atac.ant.misc;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.Vector;

/**
 * Calls the unix2dos command on a fileset.
 *
 * @ant.task name="unix2dos" category="misc" ignore="false"
 */
public class Unix2DosTask extends Task {
  private static final String COMMAND = "unix2dos";

  private Vector<FileSet> filesets = new Vector<FileSet>();

  /**
   * Fileset concerned by the command
   *
   * @ant.required
   */
  public void addFileset(FileSet fileset) {
    filesets.add(fileset);
  }

  protected void precheck() {
    if (filesets.size() < 1) {
      throw new BuildException("fileset not set");
    }
  }

  public void execute() {
    precheck();
    log("Start " + COMMAND + " on filesets", Project.MSG_VERBOSE);
    for (FileSet fileset : filesets) {
      DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
      String[] files = ds.getIncludedFiles();
      for (int i = 0; i < files.length; i++) {
        File file = new File(ds.getBasedir(), files[i]);
        log("Execute " + COMMAND + " on: " + file.getAbsolutePath(), Project.MSG_VERBOSE);
        ExecTask exec = new ExecTask();
        exec.setDir(ds.getBasedir());
        exec.setExecutable(COMMAND);
        exec.createArg().setValue(file.getPath());
        exec.execute();
      }
    }
    log("End " + COMMAND, Project.MSG_VERBOSE);
  }
}

