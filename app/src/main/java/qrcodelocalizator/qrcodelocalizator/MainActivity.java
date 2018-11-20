package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText IPAddressBox;
    private Button addRoom;
    private Button findRoom;
    private Button check;

    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gatherViews();
        communicator = new Communicator();
    }

    private void gatherViews(){
        IPAddressBox = findViewById(R.id.IPAddress);
        addRoom = findViewById(R.id.AddRoom);
        findRoom = findViewById(R.id.FindRoom);
        check = findViewById(R.id.Check);
    }

    public void onAddRoomClick(View view){
        startActivity(new Intent(this, AddRoomActivity.class));
    }

    public void onFindRoomClick(View view){
        startActivity(new Intent(this, GetRoomActivity.class));
    }

    public void onOKClick(View view){
        IPAddressBox.setEnabled(false);
        check.setEnabled(true);
        Config.serverIP = IPAddressBox.getText().toString();
    }

    public void onEditClick(View view){
        addRoom.setEnabled(false);
        findRoom.setEnabled(false);
        IPAddressBox.setEnabled(true);
        check.setEnabled(false);
    }

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
