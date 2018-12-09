package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;

//pierwsza aktywność odpalana po uruchomieniu aplikacji
public class MainActivity extends AppCompatActivity {

    private EditText IPAddressBox;
    private Button addRoom;
    private Button findRoom;
    private Button check;

    //obiekt klasy odpowiadajacej za komunikacje z API
    private Communicator communicator;

    //metoda odpalana przy starcie aktywnosci po to, zeby zainicjalizowac zmienne
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gatherViews();
        communicator = new Communicator();
    }

    //metoda przypisuje poszczegolne kontrolki odpowiednim obiektom
    private void gatherViews(){
        IPAddressBox = findViewById(R.id.IPAddress);
        addRoom = findViewById(R.id.AddRoom);
        findRoom = findViewById(R.id.FindRoom);
        check = findViewById(R.id.Check);
    }

    //listener do obslugi przycisku dodawania pokoju
    public void onAddRoomClick(View view){
        startActivity(new Intent(this, AddRoomActivity.class));
    }

    //listener do obslugi przycisku znajdowania pokoju
    public void onFindRoomClick(View view){
        startActivity(new Intent(this, GetRoomActivity.class));
    }

    //listener do obslugi przycisku OK
    public void onOKClick(View view){
        IPAddressBox.setEnabled(false);
        check.setEnabled(true);
        Config.serverIP = IPAddressBox.getText().toString();
    }

    //listener do obslugi przycisku Edit
    public void onEditClick(View view){
        addRoom.setEnabled(false);
        findRoom.setEnabled(false);
        IPAddressBox.setEnabled(true);
        check.setEnabled(false);
    }

    //listener do obslugi przycisku Check
    public void onCheckClick(View view){
        if(communicator.getAvailability().getStatusCode() == HttpURLConnection.HTTP_OK){
            Toast.makeText(this, "Connection established", Toast.LENGTH_LONG).show();
            addRoom.setEnabled(true);
            findRoom.setEnabled(true);
        }
        else{
            IPAddressBox.setEnabled(true);
            Toast.makeText(this, "Cannot connect to the api. " +
                    "Check IP Address and try again", Toast.LENGTH_LONG).show();
        }
        check.setEnabled(false);
    }
}
