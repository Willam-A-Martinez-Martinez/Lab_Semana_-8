/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab.semana.pkg8;

/**
 *
 * @author Mario
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Steam steam;
    private int currentUserCode;
    private String currentUserType;
    private String currentUsername;

    public MainGUI() {
        super("Steam GUI");
        steam = new Steam();
        ensureAdminAccount();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new LoginPanel(), "login");
        mainPanel.add(new RegisterPanel(), "register");
        mainPanel.add(new AdminPanel(), "admin");
        mainPanel.add(new UserPanel(), "user");
        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void ensureAdminAccount() {
        try {
            File playerFile = new File("steam/player.stm");
            if (playerFile.length() == 0) {
                Calendar adminNac = Calendar.getInstance();
                adminNac.set(1970, Calendar.JANUARY, 1);
                steam.addPlayer("admin", "admin123", "Administrador", adminNac, "admin.jpg", "admin");
                System.out.println("Cuenta admin preestablecida creada");
            }
        } catch(Exception e) {
            System.out.println("Error al crear admin: " + e.getMessage());
        }
    }

    private class LoginPanel extends JPanel {
        private JTextField tfUsername;
        private JPasswordField pfPassword;
        private JButton btnLogin, btnRegister;

        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            JLabel lblTitle = new JLabel("Inicio de sesion");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(lblTitle, gbc);
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            tfUsername = new JTextField(15);
            add(tfUsername, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            pfPassword = new JPasswordField(15);
            add(pfPassword, gbc);
            btnLogin = new JButton("Login");
            btnLogin.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    loginUser();
                }
            });
            gbc.gridx = 0; gbc.gridy = 3;
            add(btnLogin, gbc);
            btnRegister = new JButton("Registrar");
            btnRegister.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "register");
                }
            });
            gbc.gridx = 1; gbc.gridy = 3;
            add(btnRegister, gbc);
            addComponentListener(new ComponentAdapter(){
                public void componentShown(ComponentEvent e) {
                    tfUsername.setText("");
                    pfPassword.setText("");
                }
            });
        }

        private void loginUser() {
            String username = tfUsername.getText();
            String password = new String(pfPassword.getPassword());
            try {
                RandomAccessFile rafPlayers = new RandomAccessFile("steam/player.stm", "rw");
                boolean found = false;
                int userCode = -1;
                String nombre = "";
                String tipoUsuario = "";
                rafPlayers.seek(0);
                while (rafPlayers.getFilePointer() < rafPlayers.length()) {
                    int code = rafPlayers.readInt();
                    if (code == -1) {
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readLong();
                        rafPlayers.readInt();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        continue;
                    }
                    String usr = rafPlayers.readUTF();
                    String pass = rafPlayers.readUTF();
                    String nom = rafPlayers.readUTF();
                    long nacimiento = rafPlayers.readLong();
                    rafPlayers.readInt();
                    rafPlayers.readUTF();
                    String tipo = rafPlayers.readUTF();
                    if (usr.equals(username) && pass.equals(password)) {
                        found = true;
                        userCode = code;
                        nombre = nom;
                        tipoUsuario = tipo;
                        break;
                    }
                }
                rafPlayers.close();
                if (found) {
                    currentUserCode = userCode;
                    currentUserType = tipoUsuario;
                    currentUsername = username;
                    JOptionPane.showMessageDialog(MainGUI.this, "Bienvenido " + nombre + " (" + tipoUsuario + ")");
                    if (tipoUsuario.equalsIgnoreCase("admin")) {
                        cardLayout.show(mainPanel, "admin");
                    } else {
                        cardLayout.show(mainPanel, "user");
                    }
                } else {
                    JOptionPane.showMessageDialog(MainGUI.this, "Credenciales invalidas");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainGUI.this, "Error: " + ex.getMessage());
            }
        }
    }

    private class RegisterPanel extends JPanel {
        private JTextField tfUsername, tfNombre, tfFecha, tfImagen;
        private JPasswordField pfPassword;
        private JButton btnRegister, btnBack;

        public RegisterPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            JLabel lblTitle = new JLabel("Registro de usuario");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(lblTitle, gbc);
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            tfUsername = new JTextField(15);
            add(tfUsername, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            pfPassword = new JPasswordField(15);
            add(pfPassword, gbc);
            gbc.gridx = 0; gbc.gridy = 3;
            add(new JLabel("Nombre:"), gbc);
            gbc.gridx = 1;
            tfNombre = new JTextField(15);
            add(tfNombre, gbc);
            gbc.gridx = 0; gbc.gridy = 4;
            add(new JLabel("Fecha de nacimiento (dd/MM/yyyy):"), gbc);
            gbc.gridx = 1;
            tfFecha = new JTextField(15);
            add(tfFecha, gbc);
            gbc.gridx = 0; gbc.gridy = 5;
            add(new JLabel("Ruta de imagen:"), gbc);
            gbc.gridx = 1;
            tfImagen = new JTextField(15);
            add(tfImagen, gbc);
            btnRegister = new JButton("Registrar");
            btnRegister.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    registerUser();
                }
            });
            gbc.gridx = 0; gbc.gridy = 6;
            add(btnRegister, gbc);
            btnBack = new JButton("Atras");
            btnBack.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    clearFields();
                    cardLayout.show(mainPanel, "login");
                }
            });
            gbc.gridx = 1; gbc.gridy = 6;
            add(btnBack, gbc);
            addComponentListener(new ComponentAdapter(){
                public void componentShown(ComponentEvent e) {
                    clearFields();
                }
            });
        }

        private void clearFields() {
            tfUsername.setText("");
            pfPassword.setText("");
            tfNombre.setText("");
            tfFecha.setText("");
            tfImagen.setText("");
        }

        private void registerUser() {
            String username = tfUsername.getText();
            String password = new String(pfPassword.getPassword());
            String nombre = tfNombre.getText();
            String fechaStr = tfFecha.getText();
            String imagen = tfImagen.getText();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar nacimiento = Calendar.getInstance();
                nacimiento.setTime(sdf.parse(fechaStr));
                steam.addPlayer(username, password, nombre, nacimiento, imagen, "normal");
                JOptionPane.showMessageDialog(MainGUI.this, "Usuario registrado");
                clearFields();
                cardLayout.show(mainPanel, "login");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainGUI.this, "Error: " + ex.getMessage());
            }
        }
    }

    private class AdminPanel extends JPanel {
        public AdminPanel() {
            setLayout(new BorderLayout());
            JPanel panelButtons = new JPanel(new GridLayout(7, 1, 5, 5));
            JButton btnAddGame = new JButton("Agregar videojuego");
            btnAddGame.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new AddGameDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnAddGame);
            JButton btnUpdatePrice = new JButton("Modificar precio");
            btnUpdatePrice.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new UpdatePriceDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnUpdatePrice);
            JButton btnDeleteGame = new JButton("Eliminar videojuego");
            btnDeleteGame.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new DeleteGameDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnDeleteGame);
            JButton btnDeletePlayer = new JButton("Eliminar player");
            btnDeletePlayer.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new DeletePlayerDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnDeletePlayer);
            JButton btnReportClient = new JButton("Reporte de cliente");
            btnReportClient.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new ReportClientDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnReportClient);
            JButton btnCatalog = new JButton("Ver catalogo de juegos");
            btnCatalog.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new CatalogDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnCatalog);
            JButton btnLogout = new JButton("Cerrar sesion");
            btnLogout.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "login");
                }
            });
            panelButtons.add(btnLogout);
            add(panelButtons, BorderLayout.CENTER);
        }
    }

    private class UserPanel extends JPanel {
        public UserPanel() {
            setLayout(new BorderLayout());
            JPanel panelButtons = new JPanel(new GridLayout(5, 1, 5, 5));
            JButton btnCatalog = new JButton("Ver catalogo de juegos");
            btnCatalog.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new CatalogDialog(MainGUI.this, steam);
                }
            });
            panelButtons.add(btnCatalog);
            JButton btnDownload = new JButton("Descargar videojuego");
            btnDownload.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new DownloadDialog(MainGUI.this, steam, currentUserCode);
                }
            });
            panelButtons.add(btnDownload);
            JButton btnLibrary = new JButton("Ver biblioteca");
            btnLibrary.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new LibraryDialog(MainGUI.this, currentUsername);
                }
            });
            panelButtons.add(btnLibrary);
            JButton btnProfile = new JButton("Ver perfil");
            btnProfile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    new ProfileDialog(MainGUI.this, steam, currentUserCode);
                }
            });
            panelButtons.add(btnProfile);
            JButton btnLogout = new JButton("Cerrar sesion");
            btnLogout.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "login");
                }
            });
            panelButtons.add(btnLogout);
            add(panelButtons, BorderLayout.CENTER);
        }
    }
}
