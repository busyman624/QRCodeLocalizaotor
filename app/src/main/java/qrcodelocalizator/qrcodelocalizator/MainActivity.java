package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText IPAddressBox;
    private Button addRoom;
    private Button findRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gatherViews();
    }

    private void gatherViews(){
        IPAddressBox = (EditText)findViewById(R.id.IPAddress);
        addRoom = (Button)findViewById(R.id.AddRoom);
        findRoom = (Button)findViewById(R.id.FindRoom);
    }

    public void onAddRoomClick(View view){
        startActivity(new Intent(this, AddRoomActivity.class));
    }

    public void onFindRoomClick(View view){

    }

    public void onOKClick(View view){
        IPAddressBox.setText("192.168.0.136:8080"); //DEBUG STUFF
        addRoom.setEnabled(true);
        findRoom.setEnabled(true);
        IPAddressBox.setEnabled(false);
        Config.serverIP = IPAddressBox.getText().toString();
    }

    public void onEditClick(View view){
        addRoom.setEnabled(false);
        findRoom.setEnabled(false);
        IPAddressBox.setEnabled(true);
    }
}
