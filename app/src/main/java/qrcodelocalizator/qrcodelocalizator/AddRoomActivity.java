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
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;

//aktywnosc sluzaca do dodawania nowego pokoju, wpisywania wszystkich danych dotyczacych pokoju
//na podstawie ktorych pozniej jest tworzony obiekt modelu
public class AddRoomActivity extends AppCompatActivity {
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
        roomNumber = findViewById(R.id.AddRoomNumber);
        type = findViewById(R.id.AddType);
        availability = findViewById(R.id.AddAvailability);
        description = findViewById(R.id.AddDescription);
        qrCode = findViewById(R.id.AddedQRCode);
        addButton = findViewById(R.id.AddButton);
    }

    //listener do obslugi przycisku AddQRCode
    public void onAddQRCodeClick(View view){
        startActivityForResult(new Intent(this, QrCodeScannerActivity.class),
                Constans.ADD_ROOM_CODE);
    }

    //listener do obslugi przycisku Add
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
            Toast.makeText(this, "Room already exists", Toast.LENGTH_LONG).show();

        finish();
    }

    //listner do obslugi przycisku cancel
    public void onCancelClick(View view){
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        finish();
    }

    //metoda, ktora odbiera zdjecie qr i zapisany w nim tekst z aktywnosci do skanowania kodu
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
