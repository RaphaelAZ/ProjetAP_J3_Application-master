import com.itextpdf.html2pdf.HtmlConverter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static j2html.TagCreator.*;

public class Interface extends JFrame  { //HERITE DE LA LIBRAIRIE JFRAME

    private ConnexionBdd requetes= new ConnexionBdd();
    private Connection connexion  = new ConnexionBdd().getConnexion();
    private ArrayList<Client> listeClients = new ArrayList<>();

    private JButton button_pdf= new JButton();
    private JButton button_XML= new JButton();
    private JButton button_Materiels_Clients= new JButton();

    public Interface() throws SQLException, IOException {

        setSize(800,600) ; //PARAMETRES INTERFACE
        setTitle( "ma première fenêtre") ;
        setVisible(true) ;
        setLocationRelativeTo(null);

        this.addMouseListener(new EcouteurSouris());// APPEL DE LA CLASSE ECOUTEURS
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.add(ToolBar(),BorderLayout.NORTH);

        String requete = "SELECT * FROM client";
        PreparedStatement stmt2 = connexion.prepareStatement(requete);
        stmt2.execute();
        ResultSet rs2 = stmt2.executeQuery();

        // Permet de récupérer le nombre de lignes du tableau
        rs2.last();
        int size = rs2.getRow();
        rs2.beforeFirst();

        while (rs2.next()) {
            listeClients.add(requetes.rechercherClient(rs2.getString("Numero_Client")));
        }

        try {
            String columns[] = { "Numero_Client", "Raison_Sociale", "Siren","Code_Ape", "Nom_Prenom", "Adresse","Telephone_Client","Email","Numero_de_Contrat","Validité","PDF","XML","Voir Matériels" };
            String data[][] = new String[size][13];

            int i = 0;
            for (Client cli : listeClients) { //TANT QU'IL RESTE DES TECHNICIENS A AFFICHER

                data[i][0] = cli.getNumero_client() + ""; //ON AFFECTE CHAQUE VARIABLE CONTENANT UNE LIGNE DE LA TABLE TECHNICIEN
                data[i][1] = cli.getRaison_sociale();
                data[i][2] = cli.getSiren();
                data[i][3] = cli.getCode_ape();
                data[i][4] = cli.getNom_prenom();
                data[i][5] = cli.getAdresse();
                data[i][6] = cli.getTel();
                data[i][7] = cli.getMail();
                if (cli.getContrat_de_maintenance() != null) {
                    data[i][8] = cli.getContrat_de_maintenance().getNumero_de_Contrat();
                    if(cli.getContrat_de_maintenance().verif_contrat())
                        data[i][9] = "Valide";
                    else
                        data[i][9] = "Expiré";
                }
                i++;
            }


            DefaultTableModel model = new DefaultTableModel(data, columns); //DEFINITION DU MODELE DU TABLEAU

            JTable table = new JTable(model); //CREATION DU TABLEAU

            //CREATION BOUTON PDF
            table.getColumn("PDF").setCellRenderer(new ButtonPdf());
            table.getColumn("PDF").setCellEditor(new ButtonPdfEditor(new JCheckBox()));

            //CREATION BOUTON XML
            table.getColumn("XML").setCellRenderer(new ButtonXml());
            table.getColumn("XML").setCellEditor(new ButtonXmlEditor(new JCheckBox()));

            //CREATION BOUTON MATERIEL
            table.getColumn("Voir Matériels").setCellRenderer(new ButtonMateriel());
            table.getColumn("Voir Matériels").setCellEditor(new ButtonMaterielEditor(new JCheckBox()));

            //DEFINIR L'ACTION DU  BOUTON PDF
            button_pdf.addActionListener( event -> {
                try {
                    String id_client = table.getValueAt(table.getSelectedRow(), 0).toString();
                    genererPDF(id_client);
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            });

            //DEFINIR L'ACTION DU  BOUTON XML
            button_XML.addActionListener( event -> {
                try{
                    String id_client = table.getValueAt(table.getSelectedRow(), 0).toString();
                    genererXML(id_client);
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
            );

            //DEFINIR L'ACTION DU BOUTON MATERIEL
            button_Materiels_Clients.addActionListener( event -> {
                        try{
                            System.out.println("btn mat");
                            String id_client = table.getValueAt(table.getSelectedRow(), 0).toString();
                            InterfaceMateriels mat = new InterfaceMateriels(requetes.rechercherClient(id_client));
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
            );

            //table.getColumnModel().getColumn(7).setCellRenderer(new JTableButtonRenderer());
            table.setShowGrid(true);
            table.setShowVerticalLines(true);

            JScrollPane pane = new JScrollPane(table); // FIXATION DU TABLEAU A LA JSCROLLPANE

            contentPane.add(pane);

            this.setSize(500, 250);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);
        }
        catch (Exception exception) { //RENVOI L'EXECEPTION SI UNE ERREUR SE PRODUIT
            exception.printStackTrace();

        }

        JLabel labelHead = new JLabel("Liste des techniciens"); //TITRE DE L'INTERFACE
        labelHead.setFont(new Font("Arial",Font.TRUETYPE_FONT,20));
    }

    private void voirMateriel(String id) {

    }

    private JToolBar ToolBar(){ //BARRE DE NAVIGATIONS ET SES BOUTONS
        JToolBar toolBar = new JToolBar();

        JButton btnDeco= new JButton("Déconnexion");

        btnDeco.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }

        });


        toolBar.add(btnDeco);

        return toolBar;

    }

    public void InterfacePrincipale() throws UnsupportedLookAndFeelException, SQLException, IOException { //PARTIE CODE PRINCIPAL
        UIManager.setLookAndFeel(new NimbusLookAndFeel());//IMPORT DU TEMPLATE NIMBUS

        Interface fen=new Interface();//objet = variable sans type ( il récupère le constructeur)
    }

    public void genererPDF(String id) throws SQLException, IOException {

        Client cli = requetes.rechercherClient(id);

        // Génération HTML
        String html;
        html = body(
                p("CashCash"),
                p("1 rue des Potiers"),
                p("+336 25 73 84 99"),
                br(),
                p(cli.getNom_prenom()).withStyle("text-align: right"),
                p(cli.getRaison_sociale()).withStyle("text-align: right"),
                p(cli.getAdresse()).withStyle("text-align: right"),
                br(),br(),
                p("Objet : Relance - Contrat n°"+cli.getContrat_de_maintenance().getNumero_de_Contrat()),
                br(),
                p("Lille, le "+ LocalDate.now()),
                br(),
                p("Madame, Monsieur,"),
                br(),
                p("Sauf erreur ou omission de notre part, le paiement pour le renouvellement du contrat n°"
                        +cli.getContrat_de_maintenance().getNumero_de_Contrat()+" daté du "+cli.getContrat_de_maintenance().getDate_echeance()
                        +" n'a pas été effectué à ce jour. Ainsi, en cas d'incident, votre contrat de maintenance " +
                        "ne vous protège plus."),
                br(),
                p("Si vous souhaitez toujours bénéficier d'un contrat de maintenance pour protéger vos matériels, " +
                        "merci de procéder à un règlement dans les plus brefs délais."),
                br(),
                p("Veuillez agréer, Madame, Monsieur, l'expression de mes salutations distinguées")
        ).toString();
        // Conversion String HTML vers fichier PDF
        HtmlConverter.convertToPdf(html, Files.newOutputStream(Paths.get(cli.getNom_prenom() + ".pdf")));
    }

    public void genererXML(String id) {

        try {
            // Définition client
            Client cli = requetes.rechercherClient(id);

            // Récupération des matériels hors contrat
            String query = "SELECT `Numero_de_Serie`, `Numero_de_Contrat` FROM `materiel` WHERE `Numero_Client` = ? AND `Numero_de_Contrat` IS null;";
            PreparedStatement stmt = connexion.prepareStatement(query);
            stmt.setString(1, id);
            stmt.execute();
            ResultSet rs = stmt.executeQuery();

            // Création document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("listeMateriel");
            document.appendChild(root);

            // set an attribute to root element
            Attr attr = document.createAttribute("idClient");
            attr.setValue(cli.getNumero_client());
            root.setAttributeNode(attr);

            // sous contrat element
            Element sousContrat = document.createElement("sousContrat");
            root.appendChild(sousContrat);

            // get liste mat sous contrat
            ArrayList<Materiel> matSousContrat = cli.getMaterielSousContrat();

            for(Materiel m : matSousContrat) {
                Node firstDocImportedNode = document.importNode(m.xmlMateriel(), true);
                sousContrat.appendChild(firstDocImportedNode);
            }

            // hors contrat element
            Element horsContrat = document.createElement("horsContrat");
            root.appendChild(horsContrat);

            // Itération prochain résultat
            while (rs.next()) {
                Materiel mat = requetes.rechercherMateriel(rs.getString("Numero_de_Serie"));
                Node firstDocImportedNode = document.importNode(mat.xmlMateriel(), true);
                horsContrat.appendChild(firstDocImportedNode);
            }

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(cli.getNom_prenom()+".xml"));

            transformer.transform(domSource, streamResult);
            //CashcashApplication.alert("Génération XML", "Le fichier a correctement été généré !", Alert.AlertType.INFORMATION);

        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Erreur rencontrée lors de la création du document");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur SQL. Peut provenir d'une requête ou d'un résultat vide");
            e.printStackTrace();
        }
    }



    // CLASSES POUR LES BOUTONS DU TABLEAU
    class ButtonPdf extends JButton implements TableCellRenderer
    {
        public ButtonPdf() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Générer PDF" : value.toString());
            return this;
        }
    }
    class ButtonPdfEditor extends DefaultCellEditor
    {
        private String label;
        public ButtonPdfEditor(JCheckBox checkBox)
        {
            super(checkBox);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            label = (value == null) ? "Générer PDF" : value.toString();
            button_pdf.setText(label);
            return button_pdf;
        }
        public Object getCellEditorValue()
        {
            return new String(label);
        }
    }
    class ButtonXml extends JButton implements TableCellRenderer
    {
        public ButtonXml() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Générer XML" : value.toString());
            return this;
        }
    }
    class ButtonXmlEditor extends DefaultCellEditor
    {
        private String label;
        public ButtonXmlEditor(JCheckBox checkBox)
        {
            super(checkBox);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            label = (value == null) ? "Générer XML" : value.toString();
            button_XML.setText(label);
            return button_XML;
        }
        public Object getCellEditorValue()
        {
            return new String(label);
        }
    }
    class ButtonMateriel extends JButton implements TableCellRenderer
    {
        public ButtonMateriel() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Afficher" : value.toString());
            return this;
        }
    }
    class ButtonMaterielEditor extends DefaultCellEditor
    {
        private String label;
        public ButtonMaterielEditor(JCheckBox checkBox)
        {
            super(checkBox);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            label = (value == null) ? "Afficher" : value.toString();
            button_Materiels_Clients.setText(label);
            return button_Materiels_Clients;
        }
        public Object getCellEditorValue()
        {
            return new String(label);
        }
    }
}