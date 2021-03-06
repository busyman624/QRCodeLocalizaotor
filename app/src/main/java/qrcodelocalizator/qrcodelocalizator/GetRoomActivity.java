package qrcodelocalizator.qrcodelocalizator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.HttpURLConnection;

//aktywnosc do pobierania informacji z bazy o danym pokoju
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

    //listener do obslugi przycisku Get.., odpala kamere
    public void onGetQRCodeClick(View view){
        startActivityForResult(new Intent(this, QrCodeScannerActivity.class),
                Constans.FIND_ROOM_CODE);
    }

    //listener do obslugi przycisku Get, pobierane sa wszystkie informacje z bazy danych dotyczace konkretnego pokoju
    public void onGetClick(View view){
        ResponseModel response = communicator.getRoom(roomNumber.getText().toString());

        if(response.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND){
            Toast.makeText(this, "Room not found", Toast.LENGTH_LONG).show();
            return;
        }
        type.setText(response.getRoom().getType());
        availability.setText(response.getRoom().getAvailability());
        description.setText(response.getRoom().getDescription());
        qrCode.setImageBitmap(communicator.getAttachment(roomNumber.getText().toString(), "qrCode"));
        Bitmap mapBimtap = communicator.getAttachment(roomNumber.getText().toString(), "map");
        if(mapBimtap == null)
            Toast.makeText(this, "Map is not available for this room", Toast.LENGTH_LONG).show();
        map.setImageBitmap(mapBimtap);
    }

    //listener do obslugi przycisku cancel
    public void onCancelClick(View view){
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        finish();
    }

    //metoda ktora odbiera z aktywnosci skanera, informacje odkodowana z zeskanowanego kodu qr
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constans.FIND_ROOM_CODE) {
            if (resultCode == RESULT_OK) {
                roomNumber.setText(intent.getStringExtra(Constans.QR_CODE_TEXT));
                getButton.setEnabled(true);
                type.setText(null);
                availability.setText(null);
                description.setText(null);
                qrCode.setImageBitmap(null);
                map.setImageBitmap(null);
            }
        }
    }
}
