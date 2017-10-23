package fi.otavanopisto.pyramus.koski.model.lukio;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.otavanopisto.pyramus.koski.KoodistoViite;
import fi.otavanopisto.pyramus.koski.koodisto.Kieli;
import fi.otavanopisto.pyramus.koski.koodisto.Koulutus;
import fi.otavanopisto.pyramus.koski.koodisto.LukionOppimaara;
import fi.otavanopisto.pyramus.koski.koodisto.SuorituksenTila;
import fi.otavanopisto.pyramus.koski.koodisto.SuorituksenTyyppi;
import fi.otavanopisto.pyramus.koski.model.Koulutusmoduuli;
import fi.otavanopisto.pyramus.koski.model.OrganisaationToimipiste;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LukionOppimaaranSuoritus extends LukionSuoritus {
  
  public LukionOppimaaranSuoritus(LukionOppimaara oppimaara, Kieli suorituskieli, 
      OrganisaationToimipiste toimipiste, SuorituksenTila tila) {
    super(tila, SuorituksenTyyppi.lukionoppimaara, suorituskieli, toimipiste);
    this.oppimaara.setValue(oppimaara);
    this.getSuorituskieli().setValue(suorituskieli);
  }
  
  public void addOsasuoritus(LukionOsasuoritus osasuoritus) {
    osasuoritukset.add(osasuoritus);
  }
  
  public Set<LukionOsasuoritus> getOsasuoritukset() {
    return osasuoritukset;
  }
  
  public Koulutusmoduuli getKoulutusmoduuli() {
    return koulutusmoduuli;
  }
  
  @JsonProperty("oppimäärä")
  public KoodistoViite<LukionOppimaara> getOppimaara() {
    return oppimaara;
  }
  
  private final Koulutusmoduuli koulutusmoduuli = new Koulutusmoduuli(Koulutus.K309902);
  private final KoodistoViite<LukionOppimaara> oppimaara = new KoodistoViite<>();
  private final Set<LukionOsasuoritus> osasuoritukset = new HashSet<>();
}
