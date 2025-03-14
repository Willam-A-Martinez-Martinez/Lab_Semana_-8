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
    private class AddGameDialog extends JDialog {
        private JTextField tfTitulo, tfEdadMin, tfPrecio, tfImagen;
        private JComboBox<String> cbSO;
        private JButton btnAdd;

        public AddGameDialog(Frame owner, Steam steam) {
            super(owner, "Agregar videojuego", true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Titulo:"), gbc);
            gbc.gridx = 1;
            tfTitulo = new JTextField(15);
            add(tfTitulo, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Sistema operativo:"), gbc);
            gbc.gridx = 1;
            cbSO = new JComboBox<>(new String[]{"W", "M", "L"});
            add(cbSO, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Edad minima:"), gbc);
            gbc.gridx = 1;
            tfEdadMin = new JTextField(5);
            add(tfEdadMin, gbc);
            gbc.gridx = 0; gbc.gridy = 3;
            add(new JLabel("Precio:"), gbc);
            gbc.gridx = 1;
            tfPrecio = new JTextField(10);
            add(tfPrecio, gbc);
            gbc.gridx = 0; gbc.gridy = 4;
            add(new JLabel("Ruta de imagen:"), gbc);
            gbc.gridx = 1;
            tfImagen = new JTextField(15);
            add(tfImagen, gbc);
            btnAdd = new JButton("Agregar");
            btnAdd.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    try {
                        String titulo = tfTitulo.getText();
                        char so = cbSO.getSelectedItem().toString().charAt(0);
                        int edadMin = Integer.parseInt(tfEdadMin.getText());
                        double precio = Double.parseDouble(tfPrecio.getText());
                        String imagen = tfImagen.getText();
                        steam.addGame(titulo, so, edadMin, precio, imagen);
                        JOptionPane.showMessageDialog(AddGameDialog.this, "Videojuego agregado");
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AddGameDialog.this, "Error: " + ex.getMessage());
                    }
                }
            });
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            add(btnAdd, gbc);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    private class UpdatePriceDialog extends JDialog {
        private JTextField tfCode, tfNewPrice;
        private JButton btnUpdate;

        public UpdatePriceDialog(Frame owner, Steam steam) {
            super(owner, "Modificar precio", true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Codigo del videojuego:"), gbc);
            gbc.gridx = 1;
            tfCode = new JTextField(5);
            add(tfCode, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Nuevo precio:"), gbc);
            gbc.gridx = 1;
            tfNewPrice = new JTextField(10);
            add(tfNewPrice, gbc);
            btnUpdate = new JButton("Modificar");
            btnUpdate.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    try {
                        int code = Integer.parseInt(tfCode.getText());
                        double newPrice = Double.parseDouble(tfNewPrice.getText());
                        steam.updatePriceFor(code, newPrice);
                        JOptionPane.showMessageDialog(UpdatePriceDialog.this, "Precio actualizado");
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(UpdatePriceDialog.this, "Error: " + ex.getMessage());
                    }
                }
            });
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            add(btnUpdate, gbc);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    private class DeleteGameDialog extends JDialog {
        private JTextField tfCode;
        private JButton btnDelete;

        public DeleteGameDialog(Frame owner, Steam steam) {
            super(owner, "Eliminar videojuego", true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Codigo del videojuego:"), gbc);
            gbc.gridx = 1;
            tfCode = new JTextField(5);
            add(tfCode, gbc);
            btnDelete = new JButton("Eliminar");
            btnDelete.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    try {
                        int code = Integer.parseInt(tfCode.getText());
                        boolean res = steam.deleteGame(code);
                        if (res) {
                            JOptionPane.showMessageDialog(DeleteGameDialog.this, "Videojuego eliminado");
                        } else {
                            JOptionPane.showMessageDialog(DeleteGameDialog.this, "Videojuego no encontrado");
                        }
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DeleteGameDialog.this, "Error: " + ex.getMessage());
                    }
                }
            });
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            add(btnDelete, gbc);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    private class DeletePlayerDialog extends JDialog {
        private JTextField tfCode;
        private JButton btnDelete;

        public DeletePlayerDialog(Frame owner, Steam steam) {
            super(owner, "Eliminar player", true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Codigo del player:"), gbc);
            gbc.gridx = 1;
            tfCode = new JTextField(5);
            add(tfCode, gbc);
            btnDelete = new JButton("Eliminar");
            btnDelete.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    try {
                        int code = Integer.parseInt(tfCode.getText());
                        boolean res = steam.deletePlayer(code);
                        if (res) {
                            JOptionPane.showMessageDialog(DeletePlayerDialog.this, "Player eliminado");
                        } else {
                            JOptionPane.showMessageDialog(DeletePlayerDialog.this, "Player no encontrado");
                        }
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DeletePlayerDialog.this, "Error: " + ex.getMessage());
                    }
                }
            });
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            add(btnDelete, gbc);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    private class ReportClientDialog extends JDialog {
        private JTextField tfCode;
        private JButton btnReport;
        private int clientCode = -1;

        public ReportClientDialog(Frame owner, Steam steam) {
            super(owner, "Reporte de cliente", true);
            initComponents();
        }

        public ReportClientDialog(Frame owner, Steam steam, int code) {
            super(owner, "Mi Perfil", true);
            this.clientCode = code;
            initComponents();
            tfCode.setText(String.valueOf(code));
            tfCode.setEditable(false);
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Codigo del cliente:"), gbc);
            gbc.gridx = 1;
            tfCode = new JTextField(5);
            add(tfCode, gbc);
            btnReport = new JButton("Generar reporte");
            btnReport.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    try {
                        int code = Integer.parseInt(tfCode.getText());
                        String reportFileName = "reporte_" + code + ".txt";
                        steam.reportForClient(code, reportFileName);
                        JOptionPane.showMessageDialog(ReportClientDialog.this, "Reporte generado (revisa la carpeta del usuario)");
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReportClientDialog.this, "Error: " + ex.getMessage());
                    }
                }
            });
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            add(btnReport, gbc);
            pack();
            setLocationRelativeTo(getOwner());
            setVisible(true);
        }
    }

    private class CatalogDialog extends JDialog {
        public CatalogDialog(Frame owner, Steam steam) {
            super(owner, "Catalogo de juegos", true);
            setLayout(new BorderLayout());
            JPanel catalogPanel = new JPanel();
            catalogPanel.setLayout(new BoxLayout(catalogPanel, BoxLayout.Y_AXIS));
            try {
                RandomAccessFile raf = new RandomAccessFile("steam/games.stm", "rw");
                raf.seek(0);
                while (raf.getFilePointer() < raf.length()) {
                    int code = raf.readInt();
                    if (code == -1) {
                        raf.readUTF();
                        raf.readChar();
                        raf.readInt();
                        raf.readDouble();
                        raf.readInt();
                        raf.readUTF();
                        continue;
                    }
                    String titulo = raf.readUTF();
                    char so = raf.readChar();
                    int edadMin = raf.readInt();
                    double precio = raf.readDouble();
                    int downloads = raf.readInt();
                    String imagenPath = raf.readUTF();
                    JPanel gamePanel = new JPanel(new BorderLayout(5,5));
                    gamePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    ImageIcon icon = new ImageIcon(imagenPath);
                    if(icon.getIconWidth() > 0) {
                        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(img);
                    } else {
                        icon = new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
                    }
                    JLabel lblImage = new JLabel(icon);
                    gamePanel.add(lblImage, BorderLayout.WEST);
                    JPanel detailsPanel = new JPanel();
                    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                    JLabel lblTitulo = new JLabel("Titulo: " + titulo);
                    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
                    detailsPanel.add(lblTitulo);
                    detailsPanel.add(new JLabel("Codigo: " + code));
                    detailsPanel.add(new JLabel("SO: " + so));
                    detailsPanel.add(new JLabel("Edad Minima: " + edadMin));
                    detailsPanel.add(new JLabel("Precio: $" + precio));
                    detailsPanel.add(new JLabel("Descargas: " + downloads));
                    gamePanel.add(detailsPanel, BorderLayout.CENTER);
                    catalogPanel.add(gamePanel);
                    catalogPanel.add(Box.createRigidArea(new Dimension(0,5)));
                }
                raf.close();
            } catch (Exception ex) {
                catalogPanel.add(new JLabel("Error: " + ex.getMessage()));
            }
            JScrollPane scrollPane = new JScrollPane(catalogPanel);
            add(scrollPane, BorderLayout.CENTER);
            JButton btnClose = new JButton("Cerrar");
            btnClose.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            add(btnClose, BorderLayout.SOUTH);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    private class LibraryDialog extends JDialog {
        public LibraryDialog(Frame owner, String username) {
            super(owner, "Mi Biblioteca", true);
            setLayout(new BorderLayout());
            JPanel libraryPanel = new JPanel();
            libraryPanel.setLayout(new BoxLayout(libraryPanel, BoxLayout.Y_AXIS));
            File libFolder = new File("steam/" + username + "/biblioteca");
            if(libFolder.exists() && libFolder.isDirectory()){
                File[] files = libFolder.listFiles();
                if(files != null && files.length > 0){
                    for(File f : files){
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(f));
                            String titulo = br.readLine();
                            String imagenPath = br.readLine();
                            String precioStr = br.readLine();
                            br.close();
                            JPanel gamePanel = new JPanel(new BorderLayout(5,5));
                            gamePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                            ImageIcon icon = new ImageIcon(imagenPath);
                            if(icon.getIconWidth() > 0) {
                                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                icon = new ImageIcon(img);
                            } else {
                                icon = new ImageIcon(new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB));
                            }
                            JLabel lblImage = new JLabel(icon);
                            gamePanel.add(lblImage, BorderLayout.WEST);
                            JPanel detailsPanel = new JPanel();
                            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                            JLabel lblTitulo = new JLabel("Titulo: " + titulo);
                            lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
                            detailsPanel.add(lblTitulo);
                            detailsPanel.add(new JLabel("Precio: $" + precioStr));
                            gamePanel.add(detailsPanel, BorderLayout.CENTER);
                            libraryPanel.add(gamePanel);
                            libraryPanel.add(Box.createRigidArea(new Dimension(0,5)));
                        } catch(Exception ex){
                        }
                    }
                } else {
                    libraryPanel.add(new JLabel("No hay juegos en tu biblioteca."));
                }
            } else {
                libraryPanel.add(new JLabel("No hay juegos en tu biblioteca."));
            }
            JScrollPane scrollPane = new JScrollPane(libraryPanel);
            add(scrollPane, BorderLayout.CENTER);
            JButton btnClose = new JButton("Cerrar");
            btnClose.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            add(btnClose, BorderLayout.SOUTH);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }
    
    private class ProfileDialog extends JDialog {
        public ProfileDialog(Frame owner, Steam steam, int userCode) {
            super(owner, "Mi Perfil", true);
            String username = "";
            String password = "";
            String nombre = "";
            long nacimiento = 0;
            int downloads = 0;
            String imagenPath = "";
            String tipoUsuario = "";
            try {
                RandomAccessFile rafPlayers = new RandomAccessFile("steam/player.stm", "rw");
                rafPlayers.seek(0);
                boolean found = false;
                while(rafPlayers.getFilePointer() < rafPlayers.length()){
                    int code = rafPlayers.readInt();
                    if(code == -1) {
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readLong();
                        rafPlayers.readInt();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        continue;
                    }
                    if(code == userCode) {
                        username = rafPlayers.readUTF();
                        password = rafPlayers.readUTF();
                        nombre = rafPlayers.readUTF();
                        nacimiento = rafPlayers.readLong();
                        downloads = rafPlayers.readInt();
                        imagenPath = rafPlayers.readUTF();
                        tipoUsuario = rafPlayers.readUTF();
                        found = true;
                        break;
                    } else {
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readLong();
                        rafPlayers.readInt();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                    }
                }
                rafPlayers.close();
                if(!found) {
                    JOptionPane.showMessageDialog(owner, "Usuario no encontrado");
                    dispose();
                    return;
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(owner, "Error al leer perfil: " + ex.getMessage());
                dispose();
                return;
            }
            JPanel profilePanel = new JPanel(new BorderLayout(10,10));
            String basePath = "imagenes/";
            if(!imagenPath.startsWith(basePath)) {
                imagenPath = basePath + imagenPath;
            }
            ImageIcon icon = new ImageIcon(imagenPath);
            if(icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            } else {
                icon = new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
            }
            JLabel lblImage = new JLabel(icon);
            profilePanel.add(lblImage, BorderLayout.WEST);
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            JLabel lblNombre = new JLabel("Nombre: " + nombre);
            lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
            detailsPanel.add(lblNombre);
            detailsPanel.add(new JLabel("Codigo: " + userCode));
            detailsPanel.add(new JLabel("Username: " + username));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaNacimiento = sdf.format(new Date(nacimiento));
            detailsPanel.add(new JLabel("Nacimiento: " + fechaNacimiento));
            detailsPanel.add(new JLabel("Descargas: " + downloads));
            detailsPanel.add(new JLabel("Tipo: " + tipoUsuario));
            profilePanel.add(detailsPanel, BorderLayout.CENTER);
            File userFolder = new File("steam/" + username);
            if(!userFolder.exists()){
                userFolder.mkdirs();
            }
            File profileReport = new File(userFolder, "perfil.txt");
            try(PrintWriter pw = new PrintWriter(profileReport)) {
                pw.println("Codigo: " + userCode);
                pw.println("Username: " + username);
                pw.println("Password: " + password);
                pw.println("Nombre: " + nombre);
                pw.println("Nacimiento: " + fechaNacimiento);
                pw.println("Descargas: " + downloads);
                pw.println("Imagen: " + imagenPath);
                pw.println("Tipo: " + tipoUsuario);
            } catch(IOException ioe) {
                System.out.println("Error guardando perfil: " + ioe.getMessage());
            }
            JButton btnClose = new JButton("Cerrar");
            btnClose.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            getContentPane().add(profilePanel, BorderLayout.CENTER);
            getContentPane().add(btnClose, BorderLayout.SOUTH);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    private class DownloadDialog extends JDialog {
        private JTextField tfCode, tfSO;
        private JButton btnDownload;
        private int userCode;

        public DownloadDialog(Frame owner, Steam steam, int userCode) {
            super(owner, "Descargar videojuego", true);
            this.userCode = userCode;
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Codigo del videojuego:"), gbc);
            gbc.gridx = 1;
            tfCode = new JTextField(5);
            add(tfCode, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Sistema operativo (Window/Mac/Linux):"), gbc);
            gbc.gridx = 1;
            tfSO = new JTextField(10);
            add(tfSO, gbc);
            btnDownload = new JButton("Descargar");
            btnDownload.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    try {
                        int codeGame = Integer.parseInt(tfCode.getText());
                        String so = tfSO.getText();
                        boolean res = steam.downloadGame(codeGame, userCode, so);
                        if (res) {
                            JOptionPane.showMessageDialog(DownloadDialog.this, "Descarga exitosa");
                        } else {
                            JOptionPane.showMessageDialog(DownloadDialog.this, "No se pudo descargar el videojuego (posible compra previa)");
                        }
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DownloadDialog.this, "Error: " + ex.getMessage());
                    }
                }
            });
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            add(btnDownload, gbc);
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }
}
