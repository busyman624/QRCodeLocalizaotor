package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class GetRoomActivity extends AppCompatActivity {
    private EditText roomNumber;
    private EditText type;
    private EditText availability;
    private EditText description;
    private ImageView qrCode;
    private ImageView map;
    private Button getButton;

    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_room);

        gatherViews();
        communicator = new Communicator();
    }

    private void gatherViews(){
        roomNumber = findViewById(R.id.GetRoomNumber);
        type = findViewById(R.id.GetType);
        availability = findViewById(R.id.GetAvailability);
        description = findViewById(R.id.GetDescription);
        qrCode = findViewById(R.id.GetQRCode);
        map = findViewById(R.id.GetMap);
        getButton = findViewById(R.id.GetButton);
    }

    public void onGetQRCodeClick(View view){
        startActivityForResult(new Intent(this, QrCodeScannerActivity.class),
                Constans.ADD_ROOM_CODE);
    }

    public void onGetClick(View view){
        ResponseModel response = communicator.getRoom(roomNumber.getText().toString());

        if(response.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND){
            Toast.makeText(this, "Room not found", Toast.LENGTH_LONG).show();
            return;
        }
        type.setText(response.getRoom().getType());
        availability.setText(response.getRoom().getAvailability());
        description.setText(response.getRoom().getDescription());

    }

    public void onCancelClick(View view){
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constans.ADD_ROOM_CODE) {
            if (resultCode == RESULT_OK) {
                roomNumber.setText(intent.getStringExtra(Constans.QR_CODE_TEXT));
                byte[] qrCodeBytes = intent.getByteArrayExtra(Constans.QR_CODE_BYTES);
                qrCode.setImageBitmap(BitmapFactory.decodeByteArray(qrCodeBytes, 0, qrCodeBytes.length));
                getButton.setEnabled(true);
            }
        }
    }
}
