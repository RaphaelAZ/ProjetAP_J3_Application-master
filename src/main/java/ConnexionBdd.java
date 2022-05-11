import java.sql.*;
import java.time.LocalDate;

public class ConnexionBdd {

    //ATRIBUT
    private static final String url = "jdbc:mysql://localhost:3306/ap2";
    private static final String user = "root";
    private static final String password = "";
    private final Connection connexion = DriverManager.getConnection(url, user, password);;

    public Connection getConnexion() {
        return connexion;
    }

    /*public void setConnexion(Connection connexion) {
        this.connexion = connexion;
    }*/

    // CONSTRUCTEUR
    public ConnexionBdd() throws SQLException {
        //this.connexion = DriverManager.getConnection(url, user, password);
    }

    // METHODE
    public TypeContrat rechercherTypeContrat(String id) throws SQLException {

        //Création string requete
        String requete = "SELECT * FROM type_contrat WHERE RefTypeContrat = ?";

        // Création preparedStatement
        PreparedStatement stmt = connexion.prepareStatement(requete);

        // Définit valeur à rechercher (point d'interrogation de la requête)
        stmt.setString(1, id);

        // Execution
        stmt.execute();

        // Récupération du résultat
        ResultSet rs = stmt.executeQuery();

        // Itération
        rs.next();

        TypeContrat resultat = new TypeContrat();
        resultat.setRefTypeContrat(rs.getString("RefTypeContrat"));
        resultat.setDelaintervention(rs.getString("DelaiIntervention"));
        resultat.setTauxApplicable(rs.getInt("TauxApplicable"));
        return resultat;

    }

    public TypeMateriel rechercherTypeMateriel(String id) throws SQLException {

        //Création string requete
        String requete = "SELECT * FROM type_materiel WHERE Reference_Interne = ?";

        // Création preparedStatement
        PreparedStatement stmt = connexion.prepareStatement(requete);

        // Définit valeur à rechercher (point d'interrogation de la requête)
        stmt.setString(1, id);

        // Execution
        stmt.execute();

        // Récupération du résultat
        ResultSet rs = stmt.executeQuery();

        // Itération
        rs.next();

        TypeMateriel resultat = new TypeMateriel();
        resultat.setReference_Interne(rs.getString("Reference_Interne"));
        resultat.setLibelle_Type_materiel(rs.getString("Libelle_Type_materiel"));
        return resultat;
    }

    public Contrat_de_maintenance rechercherContrat(String id) throws SQLException {

        //Création string requete
        String requete = "SELECT * FROM contrat_de_maintenance WHERE Numero_de_Contrat = ?";

        // Création preparedStatement
        PreparedStatement stmt = connexion.prepareStatement(requete);

        // Définit valeur à rechercher (point d'interrogation de la requête)
        stmt.setString(1, id);

        // Execution
        stmt.execute();

        // Récupération du résultat
        ResultSet rs = stmt.executeQuery();

        // Itération
        rs.next();

        Contrat_de_maintenance resultat = new Contrat_de_maintenance();
        resultat.setNumero_de_Contrat(rs.getString("Numero_de_Contrat"));
        resultat.setDate_signature(LocalDate.parse(rs.getString("Date_signature")));
        resultat.setDate_echeance(LocalDate.parse(rs.getString("Date_echeance")));
        resultat.setRefTypeContrat(rechercherTypeContrat(rs.getString("RefTypeContrat")));
        return resultat;
    }

    public Materiel rechercherMateriel(String id) throws SQLException {

        //Création string requete
        String requete = "SELECT * FROM materiel WHERE Numero_de_Serie = ?";

        // Création preparedStatement
        PreparedStatement stmt = connexion.prepareStatement(requete);

        // Définit valeur à rechercher (point d'interrogation de la requête)
        stmt.setString(1, id);

        // Execution
        stmt.execute();

        // Récupération du résultat
        ResultSet rs = stmt.executeQuery();

        // Itération
        rs.next();

        Materiel resultat = new Materiel();
        resultat.setNumero_de_Serie(rs.getString("Numero_de_Serie"));
        resultat.setDate_de_Vente(LocalDate.parse(rs.getString("Date_de_Vente")));
        resultat.setEmplacement(rs.getString("Emplacement"));
        resultat.setTypeMateriel(rechercherTypeMateriel(rs.getString("Reference_Interne")));
        if(rs.getString("Numero_de_Contrat") != null)
            resultat.setContrat_de_maintenance(rechercherContrat(rs.getString("Numero_de_Contrat")));
        return resultat;
    }

    public Client rechercherClient(String id) throws SQLException {

        //Création string requete
        String requete = "SELECT * FROM client WHERE Numero_Client = ?";

        // Création preparedStatement
        PreparedStatement stmt = connexion.prepareStatement(requete);

        // Définit valeur à rechercher (point d'interrogation de la requête)
        stmt.setString(1, id);

        // Execution
        stmt.execute();

        // Récupération du résultat
        ResultSet rs = stmt.executeQuery();

        // Itération
        rs.next();

        Client resultat = new Client();
        resultat.setNumero_client(rs.getString("Numero_Client"));
        resultat.setRaison_sociale(rs.getString("Raison_Sociale"));
        resultat.setSiren(rs.getString("Siren"));
        resultat.setCode_ape(rs.getString("Code_Ape"));
        resultat.setNom_prenom(rs.getString("Nom_Prenom"));
        resultat.setAdresse(rs.getString("Adresse"));
        resultat.setTel(rs.getString("Telephone_Client"));
        resultat.setMail(rs.getString("Email"));
        if(rs.getString("Numero_de_Contrat") != null)
            resultat.setContrat_de_maintenance(rechercherContrat(rs.getString("Numero_de_Contrat")));

        // Requête pour récup les matériels d'un client
        String requete2 = "SELECT * FROM materiel WHERE Numero_Client = ?";
        PreparedStatement stmt2 = connexion.prepareStatement(requete2);
        stmt2.setString(1, resultat.getNumero_client());
        stmt2.execute();
        ResultSet rs2 = stmt2.executeQuery();

        while (rs2.next()) {
            resultat.getListeMateriel().add(rechercherMateriel(rs2.getString("Numero_de_Serie")));
        }

        return resultat;
    }
}
