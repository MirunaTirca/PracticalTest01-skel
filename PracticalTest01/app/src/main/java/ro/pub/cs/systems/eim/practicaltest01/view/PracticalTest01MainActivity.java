package ro.pub.cs.systems.eim.practicaltest01.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ro.pub.cs.systems.eim.practicaltest01.R;
import ro.pub.cs.systems.eim.practicaltest01.general.Constants;
import ro.pub.cs.systems.eim.practicaltest01.service.PracticalTest01Service;

public class PracticalTest01MainActivity extends AppCompatActivity {

    // A2 - elementele din interfata grafica
    // ex:
    private EditText leftEditText, rightEditText;
    private Button leftButton, rightButton, navigateButton;

    // D - servicii
    private int serviceStatus = Constants.SERVICE_STOPPED;
    private IntentFilter intentFilter = new IntentFilter();

    // B1 - ascultator pe butoane: trb clasa + instantiata +usa pe butoane
    private ButtonClickListener buttonClickListener = new ButtonClickListener();
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {
            int leftNumberOfClicks = Integer.valueOf(leftEditText.getText().toString());
            int rightNumberOfClicks = Integer.valueOf(rightEditText.getText().toString());

            switch (view.getId()) {
                case R.id.left_button:
                    leftNumberOfClicks++;
                    leftEditText.setText(String.valueOf(leftNumberOfClicks));
                    break;
                case R.id.right_button:
                    rightNumberOfClicks++;
                    rightEditText.setText(String.valueOf(rightNumberOfClicks));
                    break;
                case R.id.navigate_to_secondary_activity:
                    // C2 - intentii
                    Intent intent = new Intent(getApplicationContext(), PracticalTest01SecondaryActivity.class);
                    int total = Integer.valueOf(leftEditText.getText().toString()) +
                            Integer.valueOf(rightEditText.getText().toString());
                    intent.putExtra(Constants.NUMBER_OF_CLICKS, total);
                    startActivityForResult(intent, Constants.SECONDARY_ACTIVITY_REQUEST_CODE);
                    break;
            }

            // D1 - start service
//            Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
//            if (leftNumberOfClicks + rightNumberOfClicks >= Constants.NUMBER_OF_CLICKS_THRESHOLD &&
//                    serviceStatus == Constants.SERVICE_STOPPED) {
//                serviceStatus = Constants.SERVICE_STARTED;
//
//            }
//
//            if (serviceStatus == Constants.SERVICE_STARTED) {
//                intent.putExtra(Constants.FIRST_NUMBER, leftNumberOfClicks);
//                intent.putExtra(Constants.SECOND_NUMBER, rightNumberOfClicks);
//                getApplicationContext().startService(intent);
//            }

            if (leftNumberOfClicks + rightNumberOfClicks >= Constants.NUMBER_OF_CLICKS_THRESHOLD &&
                    serviceStatus == Constants.SERVICE_STOPPED) {

                Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
                intent.putExtra(Constants.FIRST_NUMBER, leftNumberOfClicks);
                intent.putExtra(Constants.SECOND_NUMBER, rightNumberOfClicks);
                getApplicationContext().startService(intent);

                serviceStatus = Constants.SERVICE_STARTED;

            }

        }
    }

    // B2 b) - salvarea starii
    // obs: pt a nu se salva - in .xml -> saveEnabled=false
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.LEFT_COUNT, String.valueOf(leftEditText.getText()));
        savedInstanceState.putString(Constants.RIGHT_COUNT, String.valueOf(rightEditText.getText()));
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(Constants.LEFT_COUNT)) {
            leftEditText.setText(savedInstanceState.getString(Constants.LEFT_COUNT));
        } else {
            leftEditText.setText("0");
        }
        if (savedInstanceState.containsKey(Constants.RIGHT_COUNT)) {
            rightEditText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
        } else {
            rightEditText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
        }
    }

    // C2 - intentii
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constants.SECONDARY_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "THE ACTIVITY RETURNED WITH CODE " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    // D1 - destroy service
    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, PracticalTest01Service.class);
        stopService(intent);
        super.onDestroy();
    }

    // D2 -broadcast msg
    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.BROADCAST_RECEIVER_TAG, ">>>>>>");
            Log.d(Constants.BROADCAST_RECEIVER_TAG, intent.getStringExtra(Constants.BROADCAST_RECEIVER_EXTRA));
        }
    }
    // D2 - activarea/dezactivarea ascultatorului pt intentii cu difuzarre
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageBroadcastReceiver, intentFilter);
    }
    @Override
    protected void onPause() {
        unregisterReceiver(messageBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_main);

        // A2 - findviewbyid
        leftEditText = (EditText) findViewById(R.id.left_edit_text);
        rightEditText = (EditText) findViewById(R.id.right_edit_text);
        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);
        leftEditText.setText("0");
        rightEditText.setText("0");

        // B1 - listener pe butoane
        leftButton.setOnClickListener(buttonClickListener);
        rightButton.setOnClickListener(buttonClickListener);

        // B2 - enable saved state (+ implement onSaveInstanceState + onRestoreInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.LEFT_COUNT)) {
                leftEditText.setText(savedInstanceState.getString(Constants.LEFT_COUNT));
            } else {
                leftEditText.setText("0");
            }
            if (savedInstanceState.containsKey(Constants.RIGHT_COUNT)) {
                rightEditText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
            } else {
                rightEditText.setText("0");
            }
        } else {
            leftEditText.setText("0");
            rightEditText.setText("0");
        }

        // C1 + C2 - intentii
        navigateButton = (Button) findViewById(R.id.navigate_to_secondary_activity);
        navigateButton.setOnClickListener(buttonClickListener);


        // D - service
        // filtru pentru acultatorul pentru intentii
        for (int index = 0; index < Constants.actionTypes.length; index++) {
            intentFilter.addAction(Constants.actionTypes[index]);
        }
    }
}