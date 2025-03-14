package lab.semana.pkg8;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Steam {
    private RandomAccessFile rafCodes;
    private RandomAccessFile rafGames;
    private RandomAccessFile rafPlayers;

    public Steam() {
        try {
            File folderSteam = new File("steam");
            if (!folderSteam.exists()) {
                folderSteam.mkdir();
            }
            File folderDownloads = new File("steam/downloads");
            if (!folderDownloads.exists()) {
                folderDownloads.mkdir();
            }
            rafCodes = new RandomAccessFile("steam/codes.stm", "rw");
            rafGames = new RandomAccessFile("steam/games.stm", "rw");
            rafPlayers = new RandomAccessFile("steam/player.stm", "rw");
            if (rafCodes.length() == 0) {
                rafCodes.writeInt(1);
                rafCodes.writeInt(1);
                rafCodes.writeInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error en el constructor: " + e.getMessage());
        }
    }

    private int getNextCode(int index) throws IOException {
        rafCodes.seek(index * 4);
        int code = rafCodes.readInt();
        rafCodes.seek(index * 4);
        rafCodes.writeInt(code + 1);
        return code;
    }

    public void addGame(String titulo, char so, int edadMin, double precio, String imagen) throws IOException {
        String basePath = "imagenes/";
        if (!imagen.startsWith(basePath)) {
            imagen = basePath + imagen;
        }
        int code = getNextCode(0);
        rafGames.seek(rafGames.length());
        rafGames.writeInt(code);
        rafGames.writeUTF(titulo);
        rafGames.writeChar(so);
        rafGames.writeInt(edadMin);
        rafGames.writeDouble(precio);
        rafGames.writeInt(0);
        rafGames.writeUTF(imagen);
    }

    public void addPlayer(String username, String password, String nombre, Calendar nacimiento, String imagen, String tipoUsuario) throws IOException {
        int code = getNextCode(1);
        rafPlayers.seek(rafPlayers.length());
        rafPlayers.writeInt(code);
        rafPlayers.writeUTF(username);
        rafPlayers.writeUTF(password);
        rafPlayers.writeUTF(nombre);
        rafPlayers.writeLong(nacimiento.getTimeInMillis());
        rafPlayers.writeInt(0);
        rafPlayers.writeUTF(imagen);
        rafPlayers.writeUTF(tipoUsuario);
    }

    public boolean downloadGame(int codeGame, int codePlayer, String so) throws IOException {
        long posContadorGame = -1;
        int gameCode, edadMin, contadorDownloads;
        char gameSO;
        double precio;
        String titulo, imagenGame;
        boolean gameFound = false;
        rafGames.seek(0);
        while (rafGames.getFilePointer() < rafGames.length()) {
            long posGame = rafGames.getFilePointer();
            gameCode = rafGames.readInt();
            if (gameCode == -1) {
                rafGames.readUTF();
                rafGames.readChar();
                rafGames.readInt();
                rafGames.readDouble();
                rafGames.readInt();
                rafGames.readUTF();
                continue;
            }
            titulo = rafGames.readUTF();
            gameSO = rafGames.readChar();
            edadMin = rafGames.readInt();
            precio = rafGames.readDouble();
            posContadorGame = rafGames.getFilePointer();
            contadorDownloads = rafGames.readInt();
            imagenGame = rafGames.readUTF();
            if (gameCode == codeGame) {
                gameFound = true;
                char expectedSO = ' ';
                if (so.equalsIgnoreCase("Window")) {
                    expectedSO = 'W';
                } else if (so.equalsIgnoreCase("Mac")) {
                    expectedSO = 'M';
                } else if (so.equalsIgnoreCase("Linux")) {
                    expectedSO = 'L';
                }
                if (gameSO != expectedSO) {
                    return false;
                }
                long posContadorPlayer = -1;
                int playerCode, playerDownloads;
                String username = "";
                String password, nombre;
                long nacimiento;
                String imagenPlayer, tipoUsuario;
                boolean playerFound = false;
                rafPlayers.seek(0);
                while (rafPlayers.getFilePointer() < rafPlayers.length()) {
                    long posPlayer = rafPlayers.getFilePointer();
                    playerCode = rafPlayers.readInt();
                    if (playerCode == -1) {
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        rafPlayers.readLong();
                        rafPlayers.readInt();
                        rafPlayers.readUTF();
                        rafPlayers.readUTF();
                        continue;
                    }
                    username = rafPlayers.readUTF();
                    password = rafPlayers.readUTF();
                    nombre = rafPlayers.readUTF();
                    nacimiento = rafPlayers.readLong();
                    posContadorPlayer = rafPlayers.getFilePointer();
                    playerDownloads = rafPlayers.readInt();
                    imagenPlayer = rafPlayers.readUTF();
                    tipoUsuario = rafPlayers.readUTF();
                    if (playerCode == codePlayer) {
                        playerFound = true;
                        Calendar cal = Calendar.getInstance();
                        long currentTime = cal.getTimeInMillis();
                        long diff = currentTime - nacimiento;
                        int age = (int) (diff / (1000L * 60 * 60 * 24 * 365));
                        if (age < edadMin) {
                            return false;
                        }
                        File userLibraryFolder = new File("steam/" + username + "/biblioteca");
                        if(userLibraryFolder.exists() && userLibraryFolder.isDirectory()){
                            File[] libraryFiles = userLibraryFolder.listFiles();
                            if(libraryFiles != null) {
                                for(File libFile: libraryFiles){
                                    if(libFile.getName().startsWith("game_" + codeGame + "_")){
                                        return false;
                                    }
                                }
                            }
                        }
                        int downloadCode = getNextCode(2);
                        File downloadFile = new File("steam/downloads/download_" + downloadCode + ".stm");
                        PrintWriter pw = new PrintWriter(downloadFile);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String fecha = sdf.format(new Date());
                        pw.println(fecha);
                        pw.println("IMAGE GAME");
                        pw.println();
                        pw.println("Download #" + downloadCode);
                        pw.println(nombre + " has bajado " + titulo + " a un precio de $ " + precio);
                        pw.close();
                        rafGames.seek(posContadorGame);
                        rafGames.writeInt(contadorDownloads + 1);
                        rafPlayers.seek(posContadorPlayer);
                        rafPlayers.writeInt(playerDownloads + 1);
                        File userLibFolder = new File("steam/" + username + "/biblioteca");
                        if (!userLibFolder.exists()) {
                            userLibFolder.mkdirs();
                        }
                        File libraryFile = new File(userLibFolder, "game_" + codeGame + "_" + downloadCode + ".stm");
                        PrintWriter libPw = new PrintWriter(libraryFile);
                        libPw.println(titulo);
                        libPw.println(imagenGame);
                        libPw.println(precio);
                        libPw.close();
                        return true;
                    }
                }
                if (!playerFound) {
                    return false;
                }
            }
        }
        return false;
    }

    public void updatePriceFor(int codeGame, double newPrice) throws IOException {
        rafGames.seek(0);
        while (rafGames.getFilePointer() < rafGames.length()) {
            long posGame = rafGames.getFilePointer();
            int code = rafGames.readInt();
            if (code == -1) {
                rafGames.readUTF();
                rafGames.readChar();
                rafGames.readInt();
                rafGames.readDouble();
                rafGames.readInt();
                rafGames.readUTF();
                continue;
            }
            String titulo = rafGames.readUTF();
            char so = rafGames.readChar();
            int edadMin = rafGames.readInt();
            long posPrice = rafGames.getFilePointer();
            double precio = rafGames.readDouble();
            rafGames.readInt();
            rafGames.readUTF();
            if (code == codeGame) {
                rafGames.seek(posPrice);
                rafGames.writeDouble(newPrice);
                return;
            }
        }
    }

    public void reportForClient(int codeClient, String txtFile) throws IOException {
        int playerCode, contadorDownloads;
        String username, password, nombre, imagenPlayer, tipoUsuario;
        long nacimiento;
        boolean found = false;
        rafPlayers.seek(0);
        while (rafPlayers.getFilePointer() < rafPlayers.length()) {
            long posPlayer = rafPlayers.getFilePointer();
            playerCode = rafPlayers.readInt();
            if (playerCode == -1) {
                rafPlayers.readUTF();
                rafPlayers.readUTF();
                rafPlayers.readUTF();
                rafPlayers.readLong();
                rafPlayers.readInt();
                rafPlayers.readUTF();
                rafPlayers.readUTF();
                continue;
            }
            username = rafPlayers.readUTF();
            password = rafPlayers.readUTF();
            nombre = rafPlayers.readUTF();
            nacimiento = rafPlayers.readLong();
            long posContador = rafPlayers.getFilePointer();
            contadorDownloads = rafPlayers.readInt();
            imagenPlayer = rafPlayers.readUTF();
            tipoUsuario = rafPlayers.readUTF();
            if (playerCode == codeClient) {
                found = true;
                File userFolder = new File("steam/" + username);
                if (!userFolder.exists()) {
                    userFolder.mkdirs();
                }
                File reportFile = new File(userFolder, txtFile);
                PrintWriter pw = new PrintWriter(reportFile);
                pw.println("Codigo: " + playerCode);
                pw.println("Username: " + username);
                pw.println("Password: " + password);
                pw.println("Nombre: " + nombre);
                pw.println("Nacimiento: " + new Date(nacimiento));
                pw.println("Descargas: " + contadorDownloads);
                pw.println("Imagen: " + imagenPlayer);
                pw.println("Tipo de Usuario: " + tipoUsuario);
                pw.close();
                System.out.println("REPORTE CREADO");
                return;
            }
        }
        if (!found) {
            System.out.println("NO SE PUEDE CREAR REPORTE");
        }
    }

    public void printGames() throws IOException {
        rafGames.seek(0);
        while (rafGames.getFilePointer() < rafGames.length()) {
            long pos = rafGames.getFilePointer();
            int code = rafGames.readInt();
            if (code == -1) {
                rafGames.readUTF();
                rafGames.readChar();
                rafGames.readInt();
                rafGames.readDouble();
                rafGames.readInt();
                rafGames.readUTF();
                continue;
            }
            String titulo = rafGames.readUTF();
            char so = rafGames.readChar();
            int edadMin = rafGames.readInt();
            double precio = rafGames.readDouble();
            int contador = rafGames.readInt();
            String imagen = rafGames.readUTF();
            System.out.println("Codigo: " + code + " | Titulo: " + titulo + " | SO: " + so +
                    " | Edad Minima: " + edadMin + " | Precio: " + precio + " | Descargas: " + contador +
                    " | Imagen: " + imagen);
        }
    }

    public boolean deleteGame(int codeToDelete) throws IOException {
        boolean found = false;
        rafGames.seek(0);
        while (rafGames.getFilePointer() < rafGames.length()) {
            long pos = rafGames.getFilePointer();
            int code = rafGames.readInt();
            if (code != -1 && code == codeToDelete) {
                rafGames.seek(pos);
                rafGames.writeInt(-1);
                found = true;
                break;
            }
            rafGames.readUTF();
            rafGames.readChar();
            rafGames.readInt();
            rafGames.readDouble();
            rafGames.readInt();
            rafGames.readUTF();
        }
        return found;
    }

    public boolean deletePlayer(int codeToDelete) throws IOException {
        boolean found = false;
        rafPlayers.seek(0);
        while (rafPlayers.getFilePointer() < rafPlayers.length()) {
            long pos = rafPlayers.getFilePointer();
            int code = rafPlayers.readInt();
            if (code != -1 && code == codeToDelete) {
                rafPlayers.seek(pos);
                rafPlayers.writeInt(-1);
                found = true;
                break;
            }
            rafPlayers.readUTF();
            rafPlayers.readUTF();
            rafPlayers.readUTF();
            rafPlayers.readLong();
            rafPlayers.readInt();
            rafPlayers.readUTF();
            rafPlayers.readUTF();
        }
        return found;
    }
}