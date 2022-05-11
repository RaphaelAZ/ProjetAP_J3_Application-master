import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class interfaceConnexion {

    public static class UserLogin extends JFrame {

        private static final long serialVersionUID = 1L;
        private JTextField textField;
        private JPasswordField passwordField;
        private JButton btnNewButton;
        private JLabel label;
        private JPanel contentPane;

        /**
         * Create the frame.
         */
        public UserLogin() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(450, 190, 1014, 597);
            setResizable(false);
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(contentPane);
            contentPane.setLayout(null);

            JLabel lblNewLabel = new JLabel("Login");
            lblNewLabel.setForeground(Color.BLACK);
            lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 46));
            lblNewLabel.setBounds(423, 13, 273, 93);
            contentPane.add(lblNewLabel);

            textField = new JTextField();
            textField.setFont(new Font("Tahoma", Font.PLAIN, 32));
            textField.setBounds(481, 170, 281, 68);
            contentPane.add(textField);
            textField.setColumns(10);

            passwordField = new JPasswordField();
            passwordField.setFont(new Font("Tahoma", Font.PLAIN, 32));
            passwordField.setBounds(481, 286, 281, 68);
            contentPane.add(passwordField);

            JLabel lblUsername = new JLabel("Username");
            lblUsername.setBackground(Color.BLACK);
            lblUsername.setForeground(Color.BLACK);
            lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 31));
            lblUsername.setBounds(250, 166, 193, 52);
            contentPane.add(lblUsername);

            JLabel lblPassword = new JLabel("Password");
            lblPassword.setForeground(Color.BLACK);
            lblPassword.setBackground(Color.CYAN);
            lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 31));
            lblPassword.setBounds(250, 286, 193, 52);
            contentPane.add(lblPassword);

            btnNewButton = new JButton("Login");
            btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 26));
            btnNewButton.setBounds(545, 392, 162, 73);
            btnNewButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String userName = textField.getText();
                    String password = passwordField.getText();

                    byte[] bytesOfMessage = new byte[0];
                    try {
                        bytesOfMessage = password.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        throw new RuntimeException(ex);
                    }

                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException ex) {
                        throw new RuntimeException(ex);
                    }
                    byte[] thedigest = md.digest(bytesOfMessage);

                    BigInteger bigInt = new BigInteger(1,thedigest);
                    String hashtext = bigInt.toString(16);
                    while(hashtext.length() < 32 ){
                        hashtext = "0"+hashtext;
                    }

                    String driver= "com.mysql.jdbc.Driver";
                    try {
                        Class.forName(driver);
                        Connection connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/ap2", "root", "");

                        PreparedStatement st = (PreparedStatement) connection.prepareStatement("SELECT count(*) FROM utilisateurs WHERE email=? and passworld=?");
                        st.setString(1, userName);
                        st.setString(2, hashtext);
                        ResultSet rsc = st.executeQuery();
                        rsc.next();

                        PreparedStatement st2 = (PreparedStatement) connection.prepareStatement("SELECT Matricule FROM utilisateurs WHERE email=? and passworld=?");
                        st2.setString(1, userName);
                        st2.setString(2, hashtext);
                        ResultSet rscm = st2.executeQuery();
                        rscm.next();

                        PreparedStatement st3 = (PreparedStatement) connection.prepareStatement("SELECT count(*) FROM technicien WHERE Matricule=?");

                        st3.setString(1, rscm.getString(1));
                        ResultSet rsct = st3.executeQuery();
                        rsct.next();


                        if (rsc.getInt(1)==1&&rsct.getInt(1)==0) {
                            dispose();
                            //JOptionPane.showMessageDialog(btnNewButton, "Vous êtes bien connecté ");
                            Interface Main=new Interface();

                        } else {
                            JOptionPane.showMessageDialog(btnNewButton, "Mauvais nom d'utilisateur ou mot de passe");
                        }
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                        System.out.println("sqlexception");
                    } catch (ClassNotFoundException ex) {
                        System.out.println("message2");
                        throw new RuntimeException(ex);

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }

            });

            contentPane.add(btnNewButton);

            label = new JLabel("");
            label.setBounds(0, 0, 1008, 562);
            contentPane.add(label);
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args)throws UnsupportedLookAndFeelException, SQLException {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UserLogin frame = new UserLogin();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        UIManager.setLookAndFeel(new NimbusLookAndFeel());

    }
}
