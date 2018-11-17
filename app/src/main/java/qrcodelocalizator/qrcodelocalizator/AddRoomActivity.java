package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;

public class AddRoomActivity extends AppCompatActivity {

    private LinearLayout layout;
    private EditText roomNumber;
    private EditText type;
    private EditText availability;
    private EditText description;
    private ImageView qrCode;
    private Button addButton;

    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        gatherViews();
        communicator = new Communicator();
    }

    private void gatherViews(){
        layout = (LinearLayout)findViewById(R.id.AddLayout);
        roomNumber = (EditText)findViewById(R.id.AddRoomNumber);
        type = (EditText)findViewById(R.id.AddType);
        availability = (EditText)findViewById(R.id.AddAvailability);
        description = (EditText)findViewById(R.id.AddDescription);
        qrCode = (ImageView)findViewById(R.id.AddedQRCode);
        addButton = (Button)findViewById(R.id.AddButton);
    }

    public void onAddQRCodeClick(View view){
        startActivityForResult(new Intent(this, QrCodeScannerActivity.class),
                Constans.ADD_ROOM_CODE);
    }

    public void onAddClick(View view){
        RoomModel room = new RoomModel(roomNumber.getText().toString(), type.getText().toString(),
                availability.getText().toString(), description.getText().toString());

        ResponseModel postRoomResponse = communicator.addRoom(room);
        if(postRoomResponse.getStatusCode() == HttpURLConnection.HTTP_CREATED){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap qrCodeBitmap = ((BitmapDrawable)qrCode.getDrawable()).getBitmap();
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            ResponseModel postQRCodeResponse = communicator.addQRCode(postRoomResponse.getMessage(),
                    stream.toByteArray());

            if(postQRCodeResponse.getStatusCode() == HttpURLConnection.HTTP_OK)
                Toast.makeText(this, "Room successfully added", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, postQRCodeResponse.getMessage(), Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this, postRoomResponse.getMessage(), Toast.LENGTH_LONG).show();
        finish();
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
                addButton.setEnabled(true);
            }
        }
    }
}
