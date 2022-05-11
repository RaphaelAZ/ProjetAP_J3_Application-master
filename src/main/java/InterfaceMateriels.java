import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class InterfaceMateriels extends JFrame {

    private ConnexionBdd requetes= new ConnexionBdd();
    private ArrayList<Client> listeMateriels = new ArrayList<>();

    private JButton button = new JButton();

    public InterfaceMateriels(Client cli) throws SQLException, IOException {
        setSize(800,600) ; //PARAMETRES INTERFACE
        setTitle( "Liste de matériel du client "+cli.getNom_prenom()) ;
        setVisible(true) ;
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = (JPanel) this.getContentPane();

        String columns[] = { "Numero_de_Serie", "Prix_de_Vente", "Date_de_Vente", "Emplacement", "Numero_de_Contrat", "Ajouter au contrat" };
        String data[][] = new String[cli.getListeMateriel().size()][6];

        int i = 0;
        for (Materiel mat : cli.getListeMateriel()) { //TANT QU'IL RESTE DES TECHNICIENS A AFFICHER

            data[i][0] = mat.getNumero_de_Serie() + ""; //ON AFFECTE CHAQUE VARIABLE CONTENANT UNE LIGNE DE LA TABLE TECHNICIEN
            data[i][1] = mat.getPrix_de_Vente() + "";
            data[i][2] = String.valueOf(mat.getDate_de_Vente());
            data[i][3] = mat.getEmplacement();
            if (mat.getContrat_de_maintenance().getNumero_de_Contrat() != null) {
                data[i][4] = mat.getNumero_de_Contrat(); /* mat.getContrat_de_maintenance().getNumero_de_Contrat() */
            }
            i++;
        }

        DefaultTableModel model = new DefaultTableModel(data, columns); //DEFINITION DU MODELE DU TABLEAU

        JTable table = new JTable(model);
        table.setShowGrid(true);
        table.setShowVerticalLines(true);

        //CREATION BOUTON MATERIEL
        table.getColumn("Ajouter au contrat").setCellRenderer(new ButtonAjouter());
        table.getColumn("Ajouter au contrat").setCellEditor(new ButtonAjouterEditor(new JCheckBox()));

        //DEFINIR L'ACTION DU BOUTON AJOUTER
        button.addActionListener( event -> {
            if (cli.getContrat_de_maintenance().getNumero_de_Contrat()!=null) {
                try {
                    //TODO
                    // Création et préparation du statement
                    String sql = "UPDATE `materiel` SET `Numero_de_Contrat` = ? WHERE `Numero_de_Serie` = ?;";
                    PreparedStatement stmt = requetes.getConnexion().prepareStatement(sql);
                    stmt.setString(1, cli.getContrat_de_maintenance().getNumero_de_Contrat());
                    stmt.setString(2, table.getValueAt(table.getSelectedRow(), 0).toString());
                    stmt.executeUpdate();
                    System.out.println("Contrat ajouté");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                System.out.println("Contrat Expiré ou invalide");
            }
        });

        JScrollPane pane = new JScrollPane(table); // FIXATION DU TABLEAU A LA JSCROLLPANE

        contentPane.add(pane);

        this.setSize(500, 250);

        this.setVisible(true);
    }

    class ButtonAjouter extends JButton implements TableCellRenderer
    {
        public ButtonAjouter() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Ajouter" : value.toString());
            return this;
        }
    }
    class ButtonAjouterEditor extends DefaultCellEditor
    {
        private String label;
        public ButtonAjouterEditor(JCheckBox checkBox)
        {
            super(checkBox);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            label = (value == null) ? "Ajouter" : value.toString();
            button.setText(label);
            return button;
        }
        public Object getCellEditorValue()
        {
            return new String(label);
        }
    }
}
