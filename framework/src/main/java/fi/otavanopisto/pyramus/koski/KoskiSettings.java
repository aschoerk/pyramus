package fi.otavanopisto.pyramus.koski;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import fi.otavanopisto.pyramus.dao.system.SettingDAO;
import fi.otavanopisto.pyramus.dao.system.SettingKeyDAO;
import fi.otavanopisto.pyramus.domainmodel.students.Student;
import fi.otavanopisto.pyramus.domainmodel.system.Setting;
import fi.otavanopisto.pyramus.domainmodel.system.SettingKey;
import fi.otavanopisto.pyramus.koski.koodisto.KoskiOppiaineetYleissivistava;
import fi.otavanopisto.pyramus.koski.koodisto.OpiskeluoikeudenTila;
import fi.otavanopisto.pyramus.koski.koodisto.OpiskeluoikeudenTyyppi;
import fi.otavanopisto.pyramus.koski.koodisto.SuorituksenTyyppi;
import net.sf.json.JSONObject;

@ApplicationScoped
public class KoskiSettings {

  // Global switch for Koski integration
  private static final String KOSKI_SETTINGKEY_ENABLED = "koski.enabled";
  // For test environment the saving of oid's needs to be turned off
  private static final String KOSKI_SETTINGKEY_TESTENVIRONMENT = "koski.testEnvironment";

  @Inject
  private Logger logger;

  @Inject
  private SettingDAO settingDAO;

  @Inject
  private SettingKeyDAO settingKeyDAO;
  
  @PostConstruct
  private void postConstruct() {
    enabled = Boolean.parseBoolean(getSetting(KOSKI_SETTINGKEY_ENABLED));
    testEnvironment = Boolean.parseBoolean(getSetting(KOSKI_SETTINGKEY_TESTENVIRONMENT));
    
    if (isEnabled()) {
      String fileName = System.getProperty("jboss.server.config.dir") + "/koski-config.json";
      File file = new File(fileName);
      if (file.exists()) {
        try {
          String json = FileUtils.readFileToString(file);
          JSONObject settings = JSONObject.fromObject(json);
          readSettings(settings);
        } catch (IOException e) {
          logger.log(Level.SEVERE, String.format("IO exception while reading Koski-integration configuration file.", fileName), e);
        }
      } else {
        logger.log(Level.SEVERE, String.format("Koski-integration is enabled but configuration file %s couldn't be loaded.", fileName));
      }
    }
  }
  
  private void readSettings(JSONObject settings) {
    JSONObject koskiSettings = settings.getJSONObject("koski");
    this.academyIdentifier = koskiSettings.getString("academyIdentifier");
    JSONObject studyEndReasonMapping = koskiSettings.getJSONObject("studyEndReasonMapping");
    for (Object studyEndReasonKey : studyEndReasonMapping.keySet()) {
      Long studyEndReasonId = Long.parseLong(studyEndReasonKey.toString());
      JSONObject studyEndReasonSetting = studyEndReasonMapping.getJSONObject(studyEndReasonId.toString());
      OpiskeluoikeudenTila koskiStudyState = OpiskeluoikeudenTila.valueOf(studyEndReasonSetting.getString("koski-status"));
      studentStateMap.put(studyEndReasonId, koskiStudyState);
    }
    
    JSONObject studyProgrammeMappings = koskiSettings.getJSONObject("studyProgrammes");
    for (Object studyProgrammeKey : studyProgrammeMappings.keySet()) {
      Long studyProgrammeId = Long.parseLong(studyProgrammeKey.toString());
      JSONObject studyProgramme = studyProgrammeMappings.getJSONObject(studyProgrammeId.toString());
      
      if (studyProgramme.getBoolean("enabled"))
        enabledStudyProgrammes.add(studyProgrammeId);
      
      SuorituksenTyyppi suorituksenTyyppi = SuorituksenTyyppi.valueOf(studyProgramme.getString("suorituksentyyppi"));
      suoritustyypit.put(studyProgrammeId, suorituksenTyyppi);

      OpiskeluoikeudenTyyppi opiskeluoikeudenTyyppi = OpiskeluoikeudenTyyppi.valueOf(studyProgramme.getString("opiskeluoikeudentyyppi"));
      opiskeluoikeustyypit.put(studyProgrammeId, opiskeluoikeudenTyyppi);
      
      modulitunnisteet.put(studyProgrammeId, studyProgramme.getString("modulitunniste"));
      
      JSONObject diaariJSON = studyProgramme.getJSONObject("diaari");
      if (diaariJSON != null) {
        for (Object curriculumIdKey : diaariJSON.keySet()) {
          Long curriculumId = Long.parseLong(curriculumIdKey.toString());
          diaarinumerot.put(String.format("%d.%d", studyProgrammeId, curriculumId), diaariJSON.getString(curriculumIdKey.toString()));
        }
      }
      
      JSONObject vahvistajaJSON = studyProgramme.getJSONObject("vahvistaja");
      if (vahvistajaJSON != null) {
        vahvistaja.put(studyProgrammeId, vahvistajaJSON.getString("nimi"));
        vahvistajanTitteli.put(studyProgrammeId, vahvistajaJSON.getString("titteli"));
      }
      
      String pakollisetOppiaineet = studyProgramme.getString("pakollisetOppiaineet");
      if (StringUtils.isNotBlank(pakollisetOppiaineet)) {
        Set<KoskiOppiaineetYleissivistava> set = new HashSet<>();
        for (String s : StringUtils.split(pakollisetOppiaineet, ',')) {
          KoskiOppiaineetYleissivistava oppiaine = KoskiOppiaineetYleissivistava.valueOf(s);
          if (oppiaine != null)
            set.add(oppiaine);
          else
            logger.log(Level.WARNING, String.format("No equivalent enum found for value %s of studyprogramme %d.", s, studyProgrammeId));
        }
        this.pakollisetOppiaineet.put(studyProgrammeId, set);
      }
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isTestEnvironment() {
    return testEnvironment;
  }

  public boolean isEnabledStudyProgramme(Long studyProgrammeId) {
    return enabledStudyProgrammes.contains(studyProgrammeId);
  }
  
  public OpiskeluoikeudenTila getStudentState(Student student) {
    if (student.getStudyEndReason() != null) {
      return studentStateMap.get(student.getStudyEndReason().getId());
    } else
      return OpiskeluoikeudenTila.lasna;
  }
  
  private String getSetting(String settingName) {
    SettingKey key = settingKeyDAO.findByName(settingName);
    if (key != null) {
      Setting setting = settingDAO.findByKey(key);
      
      if (setting != null)
        return setting.getValue();
    }
    
    return null;
  }

  /**
   * Is this credit such that it is being reported to the system
   * 
   * @param ca
   * @return
   */
  public boolean isReportedCredit(CreditStub ca) {
    return true;
  }

  public OpiskeluoikeudenTyyppi getOpiskeluoikeudenTyyppi(Long studyProgrammeId) {
    return opiskeluoikeustyypit.get(studyProgrammeId);
  }

  public SuorituksenTyyppi getSuorituksenTyyppi(Long studyProgrammeId) {
    return suoritustyypit.get(studyProgrammeId);
  }

  public String getModuliTunniste(Long studyProgrammeId) {
    return modulitunnisteet.get(studyProgrammeId);
  }
  
  public String getDiaariNumero(Long studyProgrammeId, Long curriculumId) {
    String key = String.format("%d.%d", studyProgrammeId, curriculumId);
    return diaarinumerot.get(key);
  }

  public String getVahvistaja(Long studyProgrammeId) {
    return vahvistaja.get(studyProgrammeId);
  }

  public String getVahvistajanTitteli(Long studyProgrammeId) {
    return vahvistajanTitteli.get(studyProgrammeId);
  }

  public Set<KoskiOppiaineetYleissivistava> getPakollisetOppiaineet(Long studyProgrammeId) {
    return pakollisetOppiaineet.get(studyProgrammeId);
  }
  
  public String getAcademyIdentifier() {
    return academyIdentifier;
  }
  
  private boolean enabled;
  private boolean testEnvironment;
  private String academyIdentifier;
  private Set<Long> enabledStudyProgrammes = new HashSet<Long>();
  private Map<Long, OpiskeluoikeudenTila> studentStateMap = new HashMap<>();
  private Map<Long, SuorituksenTyyppi> suoritustyypit = new HashMap<>();
  private Map<Long, OpiskeluoikeudenTyyppi> opiskeluoikeustyypit = new HashMap<>();
  private Map<Long, String> modulitunnisteet = new HashMap<>();
  private Map<String, String> diaarinumerot = new HashMap<>();
  
  private Map<Long, String> vahvistaja = new HashMap<>();
  private Map<Long, String> vahvistajanTitteli = new HashMap<>();
  private Map<Long, Set<KoskiOppiaineetYleissivistava>> pakollisetOppiaineet = new HashMap<>();
}
