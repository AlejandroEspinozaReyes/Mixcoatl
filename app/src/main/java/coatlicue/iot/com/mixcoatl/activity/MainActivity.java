package coatlicue.iot.com.mixcoatl.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import coatlicue.iot.com.mixcoatl.api.LightManager;
import coatlicue.iot.com.mixcoatl.callback.LockCallback;
import coatlicue.iot.com.mixcoatl.models.Light;
import coatlicue.iot.com.mixcoatl.models.Lock;
import coatlicue.iot.com.mixcoatl.R;
import coatlicue.iot.com.mixcoatl.api.LockManager;
import coatlicue.iot.com.mixcoatl.callback.LightCallback;
import coatlicue.iot.com.mixcoatl.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LockCallback, LightCallback {

    ActivityMainBinding mBinding;

    private Light mLight;
    private Lock mLock;

    private Call<Boolean> mLightRequest;
    private Call<Integer> mLockRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mLight = new Light();
        mLock = new Lock();
        mBinding.setLight(mLight);
        mBinding.setLock(mLock);
        mBinding.setLightCallback(this);
        mBinding.setLockCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG) != null) {
            processNdefTag(getIntent());
        } else if (getIntent().getParcelableExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) != null) {
            processNdefTag(getIntent());
        } else if (getIntent().getParcelableExtra(NfcAdapter.EXTRA_ID) != null) {
            processNdefTag(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNdefTag(intent);
    }

    private void processNdefTag(Intent intent) {
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                for (int i = 0; i < rawMessages.length; i++) {
                    NdefMessage message = (NdefMessage) rawMessages[i];
                    String payload = new String(message.getRecords()[0].getPayload());

                    System.out.println("NFC: EXTRA_TAG " + payload);

                    if (payload.contains("SWITCH")) {
                        changeLightState();
                    } else if (payload.contains("LOCK")) {
                        changeLockState();
                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLightRequest != null && !mLightRequest.isCanceled() && !mLightRequest.isExecuted()) {
            mLightRequest.cancel();
            mLightRequest = null;
        }

        if (mLockRequest != null && !mLockRequest.isCanceled() && !mLockRequest.isExecuted()) {
            mLockRequest.cancel();
            mLockRequest = null;
        }
    }

    @Override
    public void changeLightState() {
        if (mLightRequest != null && !mLightRequest.isCanceled() && !mLightRequest.isExecuted()) {
            return;
        }

        mLightRequest = LightManager.setState(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                updateLightState(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(MainActivity.class.getCanonicalName(), t.getMessage());
            }
        }, !mLight.getState());
    }

    public void updateLightState(Boolean state) {
        mLight.setState(state);
    }

    @Override
    public void changeLockState() {
        if (mLockRequest != null && !mLockRequest.isCanceled() && !mLockRequest.isExecuted()) {
            return;
        }

        mLockRequest = LockManager.setState(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                updateLockState(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e(MainActivity.class.getCanonicalName(), t.getMessage());
            }
        }, (mLock.getState() == 2)? 3 : 2);
    }

    public void updateLockState(Integer state) {
        mLock.setState(state);
    }


    int state = 0;
    final int unknown = -1;
    final int locked = 1;
    final int unlocked = 2;
    final int open = 3;
    final int moving = 4; // State used when the user insert a key (No sensot at this moment)

    void switchState(int target) {
        // Validate the current state of the lock before move
        updateLockState();

        // Validate the target state is valid and a movement required
        if (state == target || // Not move if the state is already reached
                state == moving || // Not move if the user is moving
                state == unknown || // Not move if a error was detected
                target < locked ||
                target > open) {
            return;
        }

        // The method of each state will move to the following state
        // Bigger or lower, as required
        switch(state) {
            case locked:
                lock(target);
                break;
            case unlocked:
                unlock(target);
                break;
            case open:
                open(target);
                break;
            default:
                // No other state to move to
                return;
        }
    }

    void lock(int target) {
        if (state != locked) {
            switchState(target);
        }

        if (target > state) {
            // TODO: Unlock
            // Move blocker to open
            // Read blocker open sensor
            // If error -> state = unknown
            // else Move lock motor to open
            // Read lock open sensor
            // If error -> state = unknown
            // else Move blocker motor to close
            // Read blocker close sensor
            // If error -> state = unknown
            // else move to next state
            // Move state to unlocked
            state = unlocked;
            // go to unlock method
            unlock(target);
        } else {
            // Validate sensors state
            updateLockState();
        }
    }

    void unlock(int target) {
        if (state != unlocked) {
            switchState(target);
        }

        if (target > state) {
            // TODO: open
            // Move pin motor to open
            // Read pin open sensor
            // If error -> state = unknown
            // else
            // Move to next state
            state = open;
            // Go to next method
            open(target);
        } else if (target < state) {
            // TODO: Lock
            // Move blocker to open
            // Read blocker open sensor
            // If error -> state = unknown
            // else Move lock motor to close
            // Read lock close sensor
            // If error -> state = unknown
            // else Move blocker motor to close
            // Read blocker close sensor
            // If error -> state = unknown
            // else move to next state
            // Move state to locked
            state = locked;
            // go to unlock method
            lock(target);
        } else {
            // Validate sensors state
            updateLockState();
        }
    }

    void open(int target) {
        if (state != open) {
            switchState(target);
        }

        if (target < state) {
            // TODO: close
            // Move pin motor back
            // Read pin close sensor
            // If error -> state = unknown
            // else
            // Move state to unlocked
            state = unlocked;
            // Go to next method
            unlock(target);
        } else {
            // Validate sensors state
            updateLockState();
        }
    }

    // Update the lock state based on the sensor's read
    // Update the state periodically to validate the current state of the lock
    void updateLockState() {
        // TODO: Get state base on sensors read
        // Get pin state
        // Get lock state
        // Get blocker state
        // On error set state unknown
    }
}