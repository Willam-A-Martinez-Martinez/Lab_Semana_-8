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

    
}