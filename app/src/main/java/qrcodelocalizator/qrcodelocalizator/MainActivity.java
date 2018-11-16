package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText IPAddressBox;
    private Button addRoom;
    private Button findRoom;

    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPAddressBox = (EditText)findViewById(R.id.IPAddress);
        addRoom = (Button)findViewById(R.id.AddRoom);
        findRoom = (Button)findViewById(R.id.FindRoom);

        communicator = new Communicator();
    }

    public void onAddRoomClick(View view){
        startActivityForResult(new Intent(this, QrCodeScannerActivity.class),
                Constans.ADD_ROOM_CODE);
    }

    public void onFindRoomClick(View view){

    }

    public void onOKClick(View view){
        IPAddressBox.setText("192.168.0.136:8080"); //DEBUG STUFF
        addRoom.setEnabled(true);
        findRoom.setEnabled(true);
        IPAddressBox.setEnabled(false);
        communicator.setServerIP(IPAddressBox.getText().toString());
    }

    public void onEditClick(View view){
        addRoom.setEnabled(false);
        findRoom.setEnabled(false);
        IPAddressBox.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constans.ADD_ROOM_CODE) {
            if (resultCode == RESULT_OK) {
                String roomNumber = intent.getStringExtra(Constans.QR_CODE_TEXT);
                RoomModel room = new RoomModel();
                room.setRoomNumber(roomNumber);
                String statusCode = communicator.addRoom(room);
                Toast.makeText(this, statusCode,
                        Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == Constans.FIND_ROOM_CODE){

        }
    }
}
