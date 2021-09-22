/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userdevice;

import java.io.IOException;
import static java.lang.System.exit;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author Kosta
 */
public class UserDevice {

    private String username;
    private String password;
    private boolean logged = false;
    private static Scanner input = new Scanner(System.in);

    private void login() {
        try {
            input.nextLine();
            System.out.println("Unesite username:");
            username = input.nextLine();
            System.out.println("Unesite password:");
            password = input.nextLine();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/login?username="
                            + username + "&password=" + password)
                    .build();
            Response response = client.newCall(request).execute();

            String jsonString = response.body().string();

            if (jsonString.equals("1")) {
                logged = true;
                System.out.println("Uspesno ste ulogovani");
            } else {
                System.out.println("Neispravni podaci");
            }
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playSong() {
        try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            input.nextLine();
            System.out.println("Unesite naziv pesme:");
            String songName = input.nextLine();
            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/playback?songName=" + songName)
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("Doslo je do greske pri pustanju pesme.");
            }
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void viewPlaylist() {
        try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/playlist")
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("Doslo je do greske pri slanju zahteva za plejlistu.");
            }

            request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/playlist/response")
                    .header("Authorization", credentials)
                    .build();
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("Doslo je do greske pri prijemu plejliste.");
            }
            String jsonString = response.body().string();
            System.out.println(jsonString);
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setAlarm() {
        try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            input.nextLine();
            System.out.println("Unesite vreme u formatu <MM dd yyyy HH:mm:ss> :");
            String timeString = input.nextLine();
            System.out.println("Unesite 1 za ponavljajuci, 0 za neponavljajuci :");
            short repeating = input.nextShort();
            input.nextLine();
            int repeatPeriod = 0, repeatCountTotal = 0;
            if (repeating == 1) {
                System.out.println("Unesite period ponavljanja u minutima:");
                repeatPeriod = input.nextInt();
                input.nextLine();
                System.out.println("Unesite zadati broj ponavljanja:");
                repeatCountTotal = input.nextInt();
                input.nextLine();
            }
            System.out.println("Unesite ime pesme za alarm. Ako zelite podrazumevano zvono ostavite prazno:");
            String songName = input.nextLine();
            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/alarm2"
                            + "?timeString=" + timeString
                            + "&repeating=" + repeating
                            + "&repeatPeriod=" + repeatPeriod
                            + "&repeatCountTotal=" + repeatCountTotal
                            + "&song=" + songName)
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (response.body().string().equals("song not found")) {
                    System.out.println("Pesma nije pronadjena, umesto nje je postavljeno default zvono.");
                } else {
                    System.out.println("Uspesno navijen alarm za " + timeString + " sa pesmom " + songName);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addJob() {
        try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            input.nextLine();
            System.out.println("Unesite vreme u formatu <MM dd yyyy HH:mm:ss> :");
            String timeString = input.nextLine();
            System.out.println("Unesite opis obaveze :");
            String description = input.nextLine();
            System.out.println("Unesite lokaciju obaveze (tekstualna adresa):");
            String location = input.nextLine();
            System.out.println("Unesite trajanje obaveze u minutima:");
            int duration = input.nextInt();
            input.nextLine();
            System.out.println("Da li zelite da navijete alarm za obavezu? (1-da, 0-ne) :");
            int alarmBool = input.nextInt();
            input.nextLine();

            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/planner"
                            + "?description=" + description
                            + "&startTime=" + timeString
                            + "&duration=" + duration
                            + "&location=" + location
                            + "&alarmbool=" + alarmBool)
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                System.out.println("Uspesno poslat zahtev za dodavanje obaveze");
            }
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void listJobs(){
         try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/planner/list")
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("Doslo je do greske pri slanju zahteva za listu obaveza.");
            }

            request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/planner/list/response")
                    .header("Authorization", credentials)
                    .build();
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("Doslo je do greske pri prijemu liste obaveza.");
            }
            String jsonString = response.body().string();
            System.out.println(jsonString);
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void removeJob(){
         try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            input.nextLine();
            System.out.println("Unesite Id posla za izbacivanje:");
            int idJob = input.nextInt(); input.nextLine();
           

            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/planner/remove"
                            + "?idJob=" + idJob)
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                System.out.println("Uspesno uklonjen posao "+idJob);
            }
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void editJob() {
        try {
            if (!logged) {
                System.out.println("Morate biti ulogovani za ovu aktivnost.");
                return;
            }
            input.nextLine();
            System.out.println("Unesite Id posla za izmenu:");
            int idJob = input.nextInt(); input.nextLine();
            System.out.println("Unesite vreme u formatu <MM dd yyyy HH:mm:ss> :");
            String timeString = input.nextLine();
            System.out.println("Unesite opis obaveze :");
            String description = input.nextLine();
            System.out.println("Unesite lokaciju obaveze (tekstualna adresa):");
            String location = input.nextLine();
            System.out.println("Unesite trajanje obaveze u minutima:");
            int duration = input.nextInt();
            input.nextLine();
            System.out.println("Da li zelite da navijete alarm za obavezu? (1-da, 0-ne) :");
            int alarmBool = input.nextInt();
            input.nextLine();

            OkHttpClient client = new OkHttpClient();
            String credentials = Credentials.basic(username, password);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/UserService/resources/planner/update"
                            + "?description=" + description
                            + "&startTime=" + timeString
                            + "&duration=" + duration
                            + "&location=" + location
                            + "&alarmbool=" + alarmBool
                            + "&idJob="+idJob)
                    .header("Authorization", credentials)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                System.out.println("Uspesno poslat zahtev za izmenu obaveze");
            }
        } catch (IOException ex) {
            Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        UserDevice ud = new UserDevice();
        while (true) {
            System.out.println("1.Login");
            System.out.println("2.Pusti pesmu");
            System.out.println("3.Pogledaj plejlistu");
            System.out.println("4.Navij alarm");
            System.out.println("5.Dodaj obavezu");
            System.out.println("6.Pregledaj obaveze");
            System.out.println("7.Izmeni obavezu");
            System.out.println("8.Ukloni obavezu");
            System.out.println("9.Kraj rada");

            int choice = input.nextInt();
            switch (choice) {
                case 1:
                    ud.login();
                    break;
                case 2:
                    ud.playSong();
                    break;
                case 3:
                    ud.viewPlaylist();
                    break;
                case 4:
                    ud.setAlarm();
                    break;
                case 5:
                    ud.addJob();
                    break;
                case 6:
                    ud.listJobs();
                    break;
                case 7:
                    ud.editJob();
                    break;
                case 8:
                    ud.removeJob();
                    break;
                case 9:
                    exit(0);
                    break;
            }

        }

    }

}
