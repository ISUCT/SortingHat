package cheerfulpeach.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.bluetooth.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter bluetooth= BluetoothAdapter.getDefaultAdapter();

                // Проверка поддержки Bluetooth
                if(bluetooth!=null)
                {
                    //С Bluetooth все в порядке
                }

                // Проверяем включен ли Bluetooth
                if (bluetooth.isEnabled()) {
                    //Bluetooth включен
                }
                else
                {
                    // Bluetooth выключен. Предложим пользователю включить его.
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });
    }
}
