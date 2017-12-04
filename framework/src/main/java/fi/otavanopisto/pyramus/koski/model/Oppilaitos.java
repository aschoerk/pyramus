package fi.otavanopisto.pyramus.koski.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.otavanopisto.pyramus.koski.KoodistoViite;
import fi.otavanopisto.pyramus.koski.koodisto.Kunta;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Oppilaitos {

  public Oppilaitos(String oid) {
    this.oid = oid;
  }
  
  public String getOid() {
    return oid;
  }
  
  public void setOid(String oid) {
    this.oid = oid;
  }

  public KoodistoViite<Kunta> getKotipaikka() {
    return kotipaikka;
  }

  public void setKotipaikka(Kunta kotipaikka) {
    this.kotipaikka.setValue(kotipaikka);
  }

  private String oid;
  private KoodistoViite<Kunta> kotipaikka;
}