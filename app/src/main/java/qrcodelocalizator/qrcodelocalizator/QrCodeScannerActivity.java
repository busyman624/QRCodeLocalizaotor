package qrcodelocalizator.qrcodelocalizator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import java.io.ByteArrayOutputStream;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

//aktywnosc do skanowania qr kodu
public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    // metoda ktora tworzy nowy kod z informacji odkadowanej ze zdjecia kodu zrodlowego,
    // bo zrodlowego zdjecia nie mozna otrzymac ze skanera
    private byte[] getQRCodeBytes(String roomNumber){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(roomNumber, BarcodeFormat.QR_CODE,100,100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // sprawdza czy uzytkownik wyrazil zgode na dostep do aparatu i ustawia widok na kamere jesli tak,
    // jesli nie to prosi o pozolenie na dostep
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Constans.CAMERA_PERMISSION_CODE);
        }
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    //odpala ponownie kamere po zminimalizowaniu lub odblokowaniu telefonu
    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    //metoda, ktora pauzuje kamery gdy zminimalizujemy aplikacje lub zablokujemy telefon
    @Override
    public void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    //metoda, ktora obsluguje zeskanowany kod, zwraca obraz kodu i odkowana informacje zawarta w kodzie
    // do aktywnosci ktora odpalila ta aktywnosc czyli do AddRoomActivity lub GetRoomActivity
    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(Constans.QR_CODE_TEXT, rawResult.getText());
        intent.putExtra(Constans.QR_CODE_BYTES, getQRCodeBytes(rawResult.getText()));
        setResult(RESULT_OK, intent);
        finish();
    }

    //metoda ktora obsluguje pozwolenie na dostep do kamery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constans.CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
        }
    }
}
