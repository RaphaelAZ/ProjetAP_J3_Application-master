import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Client {

    // ATTRIBUTS (variables)
    private String numero_client, raison_sociale, siren, code_ape, nom_prenom, adresse, tel, mail;
    private ArrayList<Materiel> listeMateriel = new ArrayList<>();
    private Contrat_de_maintenance contrat_de_maintenance;


    // ACCESSEURS (getter / setter)


    public ArrayList<Materiel> getListeMateriel() {
        return listeMateriel;
    }

    public void setListeMateriel(ArrayList<Materiel> listeMateriel) {
        this.listeMateriel = listeMateriel;
    }

    public Contrat_de_maintenance getContrat_de_maintenance() {
        return contrat_de_maintenance;
    }

    public void setContrat_de_maintenance(Contrat_de_maintenance contrat_de_maintenance) {
        this.contrat_de_maintenance = contrat_de_maintenance;
    }

    public String getNumero_client() {
        return numero_client;
    }

    public void setNumero_client(String numero_client) {
        this.numero_client = numero_client;
    }

    public String getRaison_sociale() {
        return raison_sociale;
    }

    public void setRaison_sociale(String raison_sociale) {
        this.raison_sociale = raison_sociale;
    }

    public String getSiren() {

        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getCode_ape() {
        return code_ape;
    }

    public void setCode_ape(String code_ape) {
        this.code_ape = code_ape;
    }

    public String getNom_prenom() {
        return nom_prenom;
    }

    public void setNom_prenom(String nom_prenom) {
        this.nom_prenom = nom_prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    // CONSTRUCTEUR
    public Client() {

    }

    // METHODES
    public ArrayList<Materiel> getMaterielSousContrat() {

        try {
            ArrayList<Materiel> matSousContrat = new ArrayList<>();
            ConnexionBdd requetes = new ConnexionBdd();
            Connection con = new ConnexionBdd().getConnexion();

            // Requête sql
            String sql = "SELECT `materiel`.*, `contrat_de_maintenance`.`RefTypeContrat`" +
                    "FROM `materiel`" +
                    "LEFT JOIN `contrat_de_maintenance` ON `materiel`.`Numero_de_Serie` = `contrat_de_maintenance`.`Numero_de_Contrat`" +
                    "WHERE `materiel`.`Numero_Client` = ? AND `materiel`.`Numero_de_Contrat` IS NOT NULL;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1,numero_client);
            stmt.execute();
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String numSerie = rs.getString("Numero_de_Serie");
                matSousContrat.add(requetes.rechercherMateriel(numSerie));
            }

            return matSousContrat;

        } catch (SQLException e) {
            System.err.println("Erreur SQL");
            e.printStackTrace();
        }

        return null;
    }
}
