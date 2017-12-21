package fi.otavanopisto.pyramus.json.applications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import fi.internetix.smvc.controllers.RequestContext;
import fi.otavanopisto.pyramus.dao.DAOFactory;
import fi.otavanopisto.pyramus.dao.system.SettingDAO;
import fi.otavanopisto.pyramus.dao.system.SettingKeyDAO;
import fi.otavanopisto.pyramus.dao.users.StaffMemberDAO;
import fi.otavanopisto.pyramus.domainmodel.system.Setting;
import fi.otavanopisto.pyramus.domainmodel.system.SettingKey;
import fi.otavanopisto.pyramus.domainmodel.users.StaffMember;
import net.sf.json.JSONObject;

public class OnnistuuClient {
  
  public static OnnistuuClient getInstance() {
    return INSTANCE;
  }
  
  public String createDocument(String name) throws OnnistuuClientException {

    // Payload
    
    JSONObject document = new JSONObject();
    document.put("name", name);
    JSONObject payload = new JSONObject();
    payload.put("document", document);
    String json = payload.toString(); 
    
    // Call
    
    Entity<String> entity = Entity.entity(json, MediaType.APPLICATION_JSON);
    String contentMd5 = getMd5(json);
    Response response = doPost("/api/v1/document/", contentMd5, MediaType.APPLICATION_JSON, entity);
    
    // Validation
    
    if (response.getStatus() == 201) {
      String location = response.getHeaderString("Location");
      String documentId = StringUtils.substringAfterLast(location, "/");
      if (StringUtils.length(documentId) != 36) { // should be uuid
        logger.severe(String.format("Invalid Onnistuu document uuid %s parsed from location %s", documentId, location));
        throw new OnnistuuClientException("Virheellinen dokumenttitunniste");
      }
      return documentId;
    }
    else {
      int status = response.getStatus();
      String reason = response.getStatusInfo().getReasonPhrase();
      logger.severe(String.format("Onnistuu response %d: %s", status, reason));
      throw new OnnistuuClientException(String.format("Onnistuu-dokumentin luonti epäonnistui (%d: %s)", status, reason));
    }
  }
  
  public void addPdf(String document, byte[] pdf) throws OnnistuuClientException {
    Entity<byte[]> entity = Entity.entity(pdf, "application/pdf");
    String contentMd5 = getMd5(pdf);
    Response response = doPost(String.format("/api/v1/document/%s/files", document), contentMd5, "application/pdf", entity);
    if (response.getStatus() != 201) {
      int status = response.getStatus();
      String reason = response.getStatusInfo().getReasonPhrase();
      logger.severe(String.format("Onnistuu response %d: %s", status, reason));
      throw new OnnistuuClientException(String.format("PDF-dokumentin lähettäminen epäonnistui (%d: %s)", status, reason));
    }
  }
  
  public byte[] getPdf(String document) throws OnnistuuClientException {
    Response response = doGet(String.format("/api/v1/document/%s/files/0", document));
    if (response.getStatus() != 200) {
      int status = response.getStatus();
      String reason = response.getStatusInfo().getReasonPhrase();
      logger.severe(String.format("Onnistuu response %d: %s", status, reason));
      throw new OnnistuuClientException(String.format("PDF-dokumentin hakeminen epäonnistui (%d: %s)", status, reason));
    }
    try {
      InputStream input = response.readEntity(InputStream.class);
      return IOUtils.toByteArray(input);
    }
    catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
      throw new OnnistuuClientException(String.format("PDF-dokumentin hakeminen epäonnistui (%s)", e.getMessage()));
    }
  }
  
  private Response doGet(String path) throws OnnistuuClientException {
    String contentMd5 = getMd5("");
    String date = dateToRFC2822(new Date());
    String auth = getAuthorizationHeader("GET", contentMd5, MediaType.APPLICATION_JSON, date, path);
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("https://www.onnistuu.fi%s", path));
    Builder request = target
      .request()
      .header("Content-Type", MediaType.APPLICATION_JSON)
      .header("Content-MD5", contentMd5)
      .header("Date", date)
      .header("Authorization", auth);
    return request.get();
  }

  @SuppressWarnings("rawtypes")
  private Response doPost(String path, String contentMd5, String contentType, Entity entity) throws OnnistuuClientException {
    String date = dateToRFC2822(new Date());
    String auth = getAuthorizationHeader("POST", contentMd5, contentType, date, path);
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("https://www.onnistuu.fi%s", path));
    Builder request = target
      .request()
      .header("Content-Type", contentType)
      .header("Content-MD5", contentMd5)
      .header("Date", date)
      .header("Authorization", auth);
    return request.post(entity);
  }
  
  private String getMd5(String s) throws OnnistuuClientException {
    try {
      if (s == null) {
        s = "";
      }
      return getMd5(s.getBytes("UTF-8"));
    }
    catch (Exception e) {
      throw new OnnistuuClientException(e.getMessage(), e);
    }
  }

  private String getMd5(byte[] bytes) throws OnnistuuClientException {
    try {
      return new String(Base64.getEncoder().encode(MessageDigest.getInstance("MD5").digest(bytes)));
    }
    catch (Exception e) {
      throw new OnnistuuClientException(e.getMessage(), e);
    }
  }
  
  private String getAuthorizationHeader(String method, String contentMd5, String contentType, String date, String path) throws OnnistuuClientException {
    String clientIdentifier = getClientIdentifier();
    if (clientIdentifier == null) {
      throw new OnnistuuClientException(String.format("Asetus %s puuttuu", SETTINGKEY_CLIENTIDENTIFIER));
    }
    String secretKey = getSecretKey();
    if (secretKey == null) {
      throw new OnnistuuClientException(String.format("Asetus %s puuttuu", SETTINGKEY_SECRETKEY));
    }
    String requestParts = String.format("%s\n%s\n%s\n%s\n%s", method, contentMd5, contentType, date, path);
    SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey.getBytes()), HMAC_SHA512);
    try {
      Mac mac = Mac.getInstance(HMAC_SHA512);
      mac.init(secretKeySpec);
      String authPostfix = new String(Base64.getEncoder().encode(mac.doFinal(requestParts.getBytes())));
      StringBuilder sb = new StringBuilder();
      sb.append("Onnistuu ");
      sb.append(clientIdentifier);
      sb.append(":");
      sb.append(authPostfix);
      return sb.toString();
    }
    catch (Exception e) {
      throw new OnnistuuClientException(e.getMessage(), e);
    }
  }

  private String getClientIdentifier() {
    SettingKeyDAO settingKeyDAO = DAOFactory.getInstance().getSettingKeyDAO();
    SettingKey settingKey = settingKeyDAO.findByName(SETTINGKEY_CLIENTIDENTIFIER);
    if (settingKey != null) {
      SettingDAO settingDAO = DAOFactory.getInstance().getSettingDAO();
      Setting setting = settingDAO.findByKey(settingKey);
      if (setting != null) {
        return setting.getValue();
      }
    }
    return null;
  }

  private String getSecretKey() {
    SettingKeyDAO settingKeyDAO = DAOFactory.getInstance().getSettingKeyDAO();
    SettingKey settingKey = settingKeyDAO.findByName(SETTINGKEY_SECRETKEY);
    if (settingKey != null) {
      SettingDAO settingDAO = DAOFactory.getInstance().getSettingDAO();
      Setting setting = settingDAO.findByKey(settingKey);
      if (setting != null) {
        return setting.getValue();
      }
    }
    return null;
  }
  
  private Long getPrimarySignerId() {
    SettingKeyDAO settingKeyDAO = DAOFactory.getInstance().getSettingKeyDAO();
    SettingKey settingKey = settingKeyDAO.findByName(SETTINGKEY_SIGNERID);
    if (settingKey != null) {
      SettingDAO settingDAO = DAOFactory.getInstance().getSettingDAO();
      Setting setting = settingDAO.findByKey(settingKey);
      if (setting != null) {
        return NumberUtils.toLong(setting.getValue());
      }
    }
    return null;
  }
  
  private String dateToRFC2822(Date date) {
    return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH).format(date);
  }
  
  public byte[] generateStaffSignatureDocument(RequestContext requestContext, String applicant, String line, StaffMember signer) throws OnnistuuClientException {
    try {
      HttpServletRequest httpRequest = requestContext.getRequest();
      StringBuilder baseUrl = new StringBuilder();
      baseUrl.append(httpRequest.getScheme());
      baseUrl.append("://");
      baseUrl.append(httpRequest.getServerName());
      baseUrl.append(":");
      baseUrl.append(httpRequest.getServerPort());
      
      // Staff signed document skeleton
      
      String document = IOUtils.toString(requestContext.getServletContext().getResourceAsStream(
          "/templates/applications/document-staff-signed.html"), "UTF-8");
      
      // Replace document date
      
      document = StringUtils.replace(document, "[DOCUMENT-DATE]", new SimpleDateFormat("d.M.yyyy").format(new Date()));
      
      // Replace applicant name
      
      document = StringUtils.replace(document, "[DOCUMENT-APPLICANT]", applicant);
      
      // Replace line specific welcome text
      
      String welcomeText = IOUtils.toString(requestContext.getServletContext().getResourceAsStream(
          String.format("/templates/applications/document-acceptance-%s.html", line)), "UTF-8");
      document = StringUtils.replace(document, "[DOCUMENT-TEXT]", welcomeText);
      
      // Replace primary and (optional) secondary signers
      
      Long primarySignerId = getPrimarySignerId();
      if (primarySignerId == null) {
        primarySignerId = signer.getId();
      }
      if (primarySignerId.equals(signer.getId())) {
        document = StringUtils.replace(document, "[DOCUMENT-PRIMARY-SIGNER]", getSignature(signer));
        document = StringUtils.replace(document, "[DOCUMENT-SECONDARY-SIGNER]", "");
      }
      else {
        StaffMemberDAO staffMemberDAO = DAOFactory.getInstance().getStaffMemberDAO();
        StaffMember primarySigner = staffMemberDAO.findById(primarySignerId);
        document = StringUtils.replace(document, "[DOCUMENT-PRIMARY-SIGNER]", getSignature(primarySigner));
        document = StringUtils.replace(document, "[DOCUMENT-SECONDARY-SIGNER]", "<p>Puolesta</p>" + getSignature(signer));
      }
      
      // Convert to PDF
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocumentFromString(document, baseUrl.toString());
      renderer.layout();
      renderer.createPDF(out);
      return out.toByteArray();
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to create staff document", e);
      throw new OnnistuuClientException(e.getMessage(), e);
    }
  }
  
  private String getSignature(StaffMember staffMember) {
    StringBuffer sb = new StringBuffer();
    sb.append(String.format("<p>%s</p>", staffMember.getFullName()));
    if (!StringUtils.isBlank(staffMember.getTitle())) {
      sb.append(String.format("<p>%s</p>", StringUtils.capitalize(staffMember.getTitle())));
    }
    sb.append("<p>Otavan Opisto</p>");
    return sb.toString();
  }

  private static final OnnistuuClient INSTANCE = new OnnistuuClient();
  private static final String APPLICATION_JSON = "application/json";
  private static final String SETTINGKEY_CLIENTIDENTIFIER = "applications.onnistuuClientIdentifier";
  private static final String SETTINGKEY_SECRETKEY = "applications.onnistuuSecretKey";
  private static final String SETTINGKEY_SIGNERID = "applications.defaultSignerId";
  private static final String HMAC_SHA512 = "HmacSHA512";
  private static final Logger logger = Logger.getLogger(OnnistuuClient.class.getName());

}
