package org.atac.ant.comm.webdav;

import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import edu.uci.ics.DAVExplorer.RelaxedX509TrustManager;
import edu.uci.ics.DAVExplorer.WebDAVConnection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebdavMgr {

  private WebDAVConnection webdavCnx;
  private Project project;
  private String protocol;
  private String server;
  private int port;
  private String basedir;

  public WebdavMgr(Project project, String protocol, String server, int port, String basedir) {
    this.project = project;
    this.protocol = protocol;
    this.server = server;
    this.port = port;
    this.basedir = basedir;
  }

  public void connect() throws Exception {

    project.log("Open Webdav Connection (url=" +
                protocol + "//" + server + ":" + port +
                " , dir=" + basedir + ")",
                Project.MSG_INFO);

    webdavCnx = new WebDAVConnection(protocol, server, port);
    if (webdavCnx == null) {
      throw new Exception("Webdav connection failed");
    }

    TrustManager[] tm = {new RelaxedX509TrustManager()};
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, tm, new java.security.SecureRandom());
    SSLSocketFactory sf = sslContext.getSocketFactory();
    webdavCnx.setSSLSocketFactory(sf);
    webdavCnx.setAllowAnyHostname(true);

    HTTPResponse httpResponse = webdavCnx.Generic("OPTIONS", basedir, null, getDefaultHeader());
    checkHttpResponse(httpResponse, "Connect");
  }

  private void delete(String path) throws IOException, ModuleException {
    project.log("Delete " + path, Project.MSG_VERBOSE);
    HTTPResponse httpResponse = webdavCnx.Delete(basedir + path + "/", getDefaultHeader());
    project.log("Delete result:" + httpResponse.getReasonLine(), Project.MSG_VERBOSE);
  }

  private void mkcol(String path) throws IOException, ModuleException {
    project.log("MkCol " + path, Project.MSG_VERBOSE);
    HTTPResponse httpResponse = webdavCnx.MkCol(basedir + path + "/", getDefaultHeader());
    checkHttpResponse(httpResponse, "MkCol");
  }

  private void put(String path, File node) throws IOException, ModuleException, ParseException {
    project.log("Put " + path, Project.MSG_VERBOSE);

    NVPair[] putHeader = new NVPair[3];
    putHeader[0] = new NVPair("Host", server + ":" + port);
    putHeader[1] = new NVPair("Content-Type", getContentType(node.getName()));
    putHeader[2] = new NVPair("Content-Length", new Long(node.length()).toString());

    HTTPResponse httpResponse = webdavCnx.Put(basedir + path, node.getPath().replaceAll("\\\\", "/"), putHeader);
    checkHttpResponse(httpResponse, "Put");
  }

  public void transfertDir(File dir, String dest) throws Exception {
    if (!dir.isDirectory()) {
      throw new Exception(dir + " is not a directory");
    }
    delete(dest);
    mkcol(dest);
    int offset = dir.getPath().length();
    for (File child : dir.listFiles()) {
      transfertFiles(offset, child, dest);
    }
  }

  public void transfertFiles(int offset, File node, String dest) throws Exception {
    String path = dest + "/" + node.getPath().substring(offset);
    path = path.replaceAll("\\\\", "/");
    if (node.isDirectory()) {
      mkcol(path);

      for (File child : node.listFiles()) {
        transfertFiles(offset, child, dest);
      }
    } else {
      put(path, node);
    }
  }

  public void get(File file, String path) throws Exception {

    project.log("Get " + path, Project.MSG_VERBOSE);

    HTTPResponse httpResponse = webdavCnx.Get(basedir + path, getDefaultHeader());
    checkHttpResponse(httpResponse, "Get");

    FileWriter writer = new FileWriter(file);
    writer.write(httpResponse.getText());
    writer.close();

  }

  public void transfertFile(String path, File node) throws Exception {
    delete(path);
    put(path, node);
  }

  private static Map<Integer, String> httpResponseSuccess = new HashMap<Integer, String>();

  static {
    httpResponseSuccess.put(200, "OK");
    httpResponseSuccess.put(201, "Created");
    httpResponseSuccess.put(202, "Accepted");
    httpResponseSuccess.put(203, "Non-Authoritative Information");
    httpResponseSuccess.put(204, "No Content");
    httpResponseSuccess.put(205, "Reset Content");
    httpResponseSuccess.put(206, "Partial Content");
  }

  private void checkHttpResponse(HTTPResponse httpResponse, String command) {
    String reason = "";
    int status = 0;
    try {
      reason = httpResponse.getReasonLine();
      status = httpResponse.getStatusCode();
    } catch (Exception e) {
      reason = e.getMessage();
    } finally {
      if (httpResponseSuccess.keySet().contains(status)) {
        project.log(command + " result: " + reason, Project.MSG_VERBOSE);
        return;
      }
      throw new BuildException(command + " result: " + reason);
    }
  }

  private static String[] extensions = {
          "htm", "text/html",
          "html", "text/html",
          "gif", "image/gif",
          "jpg", "image/jpeg",
          "jpeg", "image/jpeg",
          "css", "text/css",
          "pdf", "application/pdf",
          "doc", "application/msword",
          "ppt", "application/vnd.ms-powerpoint",
          "xls", "application/vnd.ms-excel",
          "ps", "application/postscript",
          "zip", "application/zip",
          "fm", "application/vnd.framemaker",
          "mif", "application/vnd.mif",
          "png", "image/png",
          "tif", "image/tiff",
          "tiff", "image/tiff",
          "rtf", "text/rtf",
          "xml", "text/xml",
          "mpg", "video/mpeg",
          "mpeg", "video/mpeg",
          "mov", "video/quicktime",
          "hqx", "application/mac-binhex40",
          "au", "audio/basic",
          "vrm", "model/vrml",
          "vrml", "model/vrml",
          "txt", "text/plain",
          "c", "text/plain",
          "cc", "text/plain",
          "cpp", "text/plain",
          "h", "text/plain",
          "sh", "text/plain",
          "bat", "text/plain",
          "ada", "text/plain",
          "java", "text/plain",
          "rc", "text/plain"
  };

  private String getContentType(String file) {
    String content = "application/octet-stream";

    int pos = file.lastIndexOf(".");
    if (pos >= 0) {
      for (int i = 0; i < extensions.length; i += 2) {
        String extension = file.substring(pos + 1).toLowerCase();
        if (extension.equals(extensions[i])) {
          content = extensions[i + 1];
          break;
        }
      }
    }
    return content;
  }


  private NVPair[] getDefaultHeader() {
    NVPair[] header = new NVPair[1];
    header[0] = new NVPair("Host", server + ":" + port);
    return header;
  }

}
