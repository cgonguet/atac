package org.atac.ant.comm.frs;

import com.soebes.jagosi.api.JaGoSI;
import com.soebes.jagosi.api.core.JaGoSIGroup;
import com.soebes.jagosi.api.frs.JaGoSIFRSFileProcessorType;
import com.soebes.jagosi.api.frs.JaGoSIFRSFileType;
import com.soebes.jagosi.api.frs.JaGoSIFRSPackage;
import com.soebes.jagosi.api.frs.JaGoSIFRSRelease;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.*;

public class AcosFeatureReleaseSystemMgr {

  private String url;
  private String user;
  private String password;
  private String group;
  private String releasePackage;
  private String releaseName;
  private String releaseFilename;
  private String releaseFile;
  private String releaseNotes;
  private String releaseChanges;

  private JaGoSI jg;
  private Project project;


  public AcosFeatureReleaseSystemMgr(Project project,
                                     String url,
                                     String user, String password,
                                     String group,
                                     String releasePackage) {
    this.project = project;
    if (url == null) {
      this.url = "https://acos.alcatel-lucent.com/soap/";
    } else {
      this.url = url;
    }
    this.user = user;
    this.password = password;
    this.group = group;
    this.releasePackage = releasePackage;
  }

  public AcosFeatureReleaseSystemMgr(Project project,
                                     String url,
                                     String user, String password,
                                     String group,
                                     String releasePackage, String releaseName,
                                     String releaseFilename, String releaseFile,
                                     String releaseNotes, String releaseChanges) {
    this(project,
         url,
         user, password,
         group,
         releasePackage);
    this.releaseName = releaseName;
    this.releaseFilename = releaseFilename;
    this.releaseFile = releaseFile;
    this.releaseNotes = releaseNotes;
    this.releaseChanges = releaseChanges;
  }


  public void createRelease() {
    jg = new JaGoSI(url);
    jg.login(user, password);
    try {
      createReleaseOnline();
    } finally {
      jg.logout();
    }
  }

  public List<String> getReleases() {
    jg = new JaGoSI(url);
    jg.login(user, password);
    try {
      return getReleasesOnline();
    } finally {
      jg.logout();
    }
  }

  private List<String> getReleasesOnline() {
    JaGoSIGroup jggroup = getGroup();
    JaGoSIFRSPackage jgpackage = getPackage(jggroup);

    Map<Date,String> releases = new TreeMap<Date,String>();
    HashMap<String, JaGoSIFRSRelease> jgReleases = jg.getFRSReleases(jggroup, jgpackage);
    for (JaGoSIFRSRelease jgRelease : jgReleases.values()) {
      releases.put(jgRelease.getReleaseDate(),jgRelease.getName());
    }

    return new ArrayList<String>(releases.values());
  }

  private void createReleaseOnline() {
    JaGoSIGroup jggroup = getGroup();

    JaGoSIFRSPackage jgpackage = getPackage(jggroup);

    if (releaseName == null || releaseName.equals("")) {
      return;
    }

    JaGoSIFRSRelease release = getRelease(jggroup, jgpackage);

    if (release == null) {
      release = createRelease(jggroup, jgpackage);
    }

    if (releaseFilename == null || releaseFilename.equals("")) {
      return;
    }

    addFileToRelease(jggroup, jgpackage, release);
  }

  private JaGoSIGroup getGroup() {
    JaGoSIGroup jggroup = (JaGoSIGroup) jg.getGroups().get(group);
    if (jggroup == null) {
      throw new BuildException("Group: " + group + " not found");
    }
    project.log("Group: " + jggroup.toString());
    return jggroup;
  }

  private JaGoSIFRSPackage getPackage(JaGoSIGroup jggroup) {
    HashMap<String, JaGoSIFRSPackage> packages = jg.getFRSPackages(jggroup);
    for (JaGoSIFRSPackage jgPackage : packages.values()) {
      if (jgPackage.getName().equals(releasePackage)) {
        return jgPackage;
      }
    }
    throw new BuildException("Package: " + releasePackage + " not found in group: " + jggroup.getGroupName());
  }

  private JaGoSIFRSRelease getRelease(JaGoSIGroup jggroup, JaGoSIFRSPackage jgpackage) {
    HashMap<String, JaGoSIFRSRelease> releases = jg.getFRSReleases(jggroup, jgpackage);
    for (JaGoSIFRSRelease jgRelease : releases.values()) {
      if (jgRelease.getName().equals(releaseName)) {
        return jgRelease;
      }
    }
    return null;
  }

  private JaGoSIFRSRelease createRelease(JaGoSIGroup jggroup, JaGoSIFRSPackage jgpackage) {
    JaGoSIFRSRelease release;
    release = jg.createFRSRelease(jggroup, jgpackage,
                                  releaseName, releaseNotes, releaseChanges,
                                  new Date());
    project.log("Release: " + releaseName + " created");
    return release;
  }

  private void addFileToRelease(JaGoSIGroup jggroup, JaGoSIFRSPackage jgpackage, JaGoSIFRSRelease release) {
    JaGoSIFRSFileType fileType = new JaGoSIFRSFileType(9999);
    JaGoSIFRSFileProcessorType fileProcessorType = new JaGoSIFRSFileProcessorType(9999);
    String result = jg.createFRSFile(jggroup, jgpackage,
                                     release,
                                     releaseFilename, releaseFile,
                                     fileType, fileProcessorType, new Date());
    if (result != null && !result.equals("")) {
      project.log("File: " + releaseFilename + " created");
    } else {
      throw new BuildException("Can't create " + releaseFilename);
    }
  }

}



