package br.com.gamecep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    boolean temAcesso;
    String ipDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checando a rede
        temAcesso = checandoRede();

    } //fim onCreate

    private boolean checandoRede() {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities;
        networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);

        if ((networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) ||
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))){

            temAcesso = true;

            WifiManager wifiManager = (WifiManager) getApplicationContext().
                    getSystemService(WIFI_SERVICE);
            String macAddress = wifiManager.getConnectionInfo().getMacAddress();
            int ip = wifiManager.getConnectionInfo().getIpAddress();
            String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            ipDevice = ipAddress;
        }


        return temAcesso;
    }


    public void criarServidor(View v){
        String resultado = "";
        ServerSocket welcomeSocket;
        DataOutputStream socketOutput;
        BufferedReader socketEntrada;

        try {
            welcomeSocket = new ServerSocket(9090);
            Log.v("GameCep", "criou servidor");
            int i = 0;
            while (true){
                Socket socket = welcomeSocket.accept();
                i++;
                Log.v("GameCep", "Nova conexão n = " + i);
                DataInputStream fromClient = new DataInputStream(socket.getInputStream());
                resultado = fromClient.readUTF();
                Log.v("GameCep", resultado);

                socketOutput = new DataOutputStream(socket.getOutputStream());
                socketOutput.writeUTF(resultado);
                socketOutput.flush();
                socketOutput.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        Intent intent = new Intent(this, IdentificacaoActivity.class);
//        startActivity(intent);


    } //fim criarServidor

    public void entrarJogo(View v){

    } //fim entrarJogo


} //fim MainActivity