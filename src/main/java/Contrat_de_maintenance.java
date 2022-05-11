import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Contrat_de_maintenance {
    //ATRIBUT
    private String Numero_de_Contrat;
    private LocalDate Date_signature, Date_echeance;
    private TypeContrat RefTypeContrat;


    // ACCESSEURS (getter / setter)
    public String getNumero_de_Contrat() {
        return Numero_de_Contrat;
    }

    public void setNumero_de_Contrat(String numero_de_Contrat) {
        Numero_de_Contrat = numero_de_Contrat;
    }

    public LocalDate getDate_signature() {
        return Date_signature;
    }

    public void setDate_signature(LocalDate date_signature) {
        Date_signature = date_signature;
    }

    public LocalDate getDate_echeance() {
        return Date_echeance;
    }

    public void setDate_echeance(LocalDate date_echeance) {
        Date_echeance = date_echeance;
    }

    public TypeContrat getRefTypeContrat() {
        return RefTypeContrat;
    }

    public void setRefTypeContrat(TypeContrat refTypeContrat) {
        RefTypeContrat = refTypeContrat;
    }
    // CONSTRUCTEUR
    // METHODES
    public boolean verif_contrat(){

        if(LocalDate.now().isAfter(Date_echeance))
            return false;
        else
            return true;
    }

    public long calcNbJoursRestants() {
        return ChronoUnit.DAYS.between(LocalDate.now(), getDate_echeance());
    }
}
