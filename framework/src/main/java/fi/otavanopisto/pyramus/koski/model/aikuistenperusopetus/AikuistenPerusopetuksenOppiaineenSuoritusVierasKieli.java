package fi.otavanopisto.pyramus.koski.model.aikuistenperusopetus;

import fi.otavanopisto.pyramus.koski.KoodistoViite;
import fi.otavanopisto.pyramus.koski.koodisto.Kielivalikoima;
import fi.otavanopisto.pyramus.koski.koodisto.KoskiOppiaineetYleissivistava;

public class AikuistenPerusopetuksenOppiaineenSuoritusVierasKieli extends AikuistenPerusopetuksenValtakunnallinenOppiaineenTunniste {

  public AikuistenPerusopetuksenOppiaineenSuoritusVierasKieli(KoskiOppiaineetYleissivistava tunniste, Kielivalikoima kieli, boolean pakollinen) {
    super(pakollinen);
    this.tunniste.setValue(tunniste);
    this.kieli.setValue(kieli);
  }
  
  public KoodistoViite<KoskiOppiaineetYleissivistava> getTunniste() {
    return tunniste;
  }
  
  public KoodistoViite<Kielivalikoima> getKieli() {
    return kieli;
  }

  private final KoodistoViite<KoskiOppiaineetYleissivistava> tunniste = new KoodistoViite<>();
  private final KoodistoViite<Kielivalikoima> kieli = new KoodistoViite<>();
}
