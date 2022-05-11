import java.time.LocalDate;
import java.util.Objects;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Materiel {
    //ATRIBUT
    private String Numero_de_Serie, Emplacement, Numero_de_Contrat, Numero_Client;
    private int Prix_de_Vente;
    private LocalDate Date_de_Vente;
    private TypeMateriel TypeMateriel;
    private Contrat_de_maintenance contrat_de_maintenance;

    // ACCESSEURS (getter / setter)
    public Contrat_de_maintenance getContrat_de_maintenance() {
        return contrat_de_maintenance;
    }

    public void setContrat_de_maintenance(Contrat_de_maintenance contrat_de_maintenance) {
        this.contrat_de_maintenance = contrat_de_maintenance;
    }

    public TypeMateriel getTypeMateriel() {
        return TypeMateriel;
    }

    public void setTypeMateriel(TypeMateriel typeMateriel) {
        TypeMateriel = typeMateriel;
    }

    public String getNumero_de_Serie() {
        return Numero_de_Serie;
    }

    public void setNumero_de_Serie(String numero_de_Serie) {
        Numero_de_Serie = numero_de_Serie;
    }

    public String getEmplacement() {
        return Emplacement;
    }

    public void setEmplacement(String emplacement) {
        Emplacement = emplacement;
    }


    public String getNumero_de_Contrat() {
        return Numero_de_Contrat;
    }

    public void setNumero_de_Contrat(String numero_de_Contrat) {
        Numero_de_Contrat = numero_de_Contrat;
    }

    public String getNumero_Client() {
        return Numero_Client;
    }

    public void setNumero_Client(String numero_Client) {
        Numero_Client = numero_Client;
    }

    public int getPrix_de_Vente() {
        return Prix_de_Vente;
    }

    public void setPrix_de_Vente(int prix_de_Vente) {
        Prix_de_Vente = prix_de_Vente;
    }

    public LocalDate getDate_de_Vente() {
        return Date_de_Vente;
    }

    public void setDate_de_Vente(LocalDate date_de_Vente) {
        Date_de_Vente = date_de_Vente;
    }

    // CONSTRUCTEUR

    // METHODES
    public Node xmlMateriel() {
        try {
            // Création document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();//déclaration document

            // element racine
            Element root = document.createElement("materiel"); // création élément racine
            document.appendChild(root); // ajout dans le document

            // set an attribute to materiel element
            Attr attr = document.createAttribute("numSerie"); // création attribut
            attr.setValue(getNumero_de_Serie()); // attribution valeur
            root.setAttributeNode(attr); // passage de l'attribut à l'élément

            // type element
            Element type = document.createElement("type");
            attr = document.createAttribute("refInterne");
            attr.setValue(getTypeMateriel().getReference_Interne());
            type.setAttributeNode(attr);
            attr = document.createAttribute("libelle");
            attr.setValue(getTypeMateriel().getLibelle_Type_materiel());
            type.setAttributeNode(attr);
            root.appendChild(type);

            // date_vente elements
            Element dateVente = document.createElement("date_vente");
            dateVente.appendChild(document.createTextNode(getDate_de_Vente().toString()));
            root.appendChild(dateVente);



            // prix_vente elements
            Element prixVente = document.createElement("prix_vente");
            prixVente.appendChild(document.createTextNode(String.valueOf(getPrix_de_Vente())));
            root.appendChild(prixVente);

            // emplacement elements
            Element emplacement = document.createElement("emplacement");
            emplacement.appendChild(document.createTextNode(getEmplacement()));
            root.appendChild(emplacement);

            if (!Objects.isNull(getContrat_de_maintenance())) {
                // nbJoursAvantEcheance elements
                Element nbJoursAvantEcheance = document.createElement("nbJoursAvantEcheance");
                nbJoursAvantEcheance.appendChild(document.createTextNode(String.valueOf(getContrat_de_maintenance().calcNbJoursRestants())));
                root.appendChild(nbJoursAvantEcheance);
            }

            return document.getDocumentElement();

        } catch(ParserConfigurationException e) {
            System.err.println("Problème lors de la création du document");
            e.printStackTrace();
        }

        return null;
    }
}
