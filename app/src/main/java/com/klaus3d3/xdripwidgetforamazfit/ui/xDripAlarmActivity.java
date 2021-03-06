package com.klaus3d3.xdripwidgetforamazfit.ui;

import android.app.Activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import com.huami.watch.transport.DataBundle;

import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.klaus3d3.xdripwidgetforamazfit.Constants;
import com.klaus3d3.xdripwidgetforamazfit.R2;
import com.klaus3d3.xdripwidgetforamazfit.events.Snoozed;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class xDripAlarmActivity extends Activity {


    private Vibrator vibrator;
    private String Alarmtext_view;
    private String SGV_view;
    private boolean eventBusConnected;
    private int snooze_time;
    private int default_snooze;
    private boolean snooze;
    private String Alerttype;

    @BindView(R2.id.Alarm_text)
    TextView Alarmtext;
    @BindView(R2.id.SGV)
    TextView sgv;
    Context context;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            finish();
        }

    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        registerReceiver(mMessageReceiver, new IntentFilter("close_alarm_dialog"));
                   getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        Alarmtext_view =getIntent().getStringExtra("Alarmtext");
        SGV_view= getIntent().getStringExtra("sgv");
        Alerttype=getIntent().getStringExtra("Alerttype");

        default_snooze=getIntent().getIntExtra("default_snooze",30);
            setContentView(R2.layout.xdripalarmactivity);
            ButterKnife.bind(this);
        try {
            HermesEventBus.getDefault().init(this);
            HermesEventBus.getDefault().register(this);

            eventBusConnected = true;

        } catch (Exception ex) {}

    }


    @Override
    public void onStart() {
        super.onStart();
        sgv.setText(SGV_view);
        Alarmtext.setText(Alarmtext_view);
        snooze=false;
        vibrate();

    }



    private void vibrate(){if(vibrator != null) {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 200, 100,200,100,200,100,200,600};
        vibrator.vibrate(pattern, 0);               };
    };

    @Override
    public void finish() {
        if(vibrator != null) {
            vibrator.cancel();                };
       if (!snooze){snooze_time=default_snooze;Snooze(snooze_time);}

        super.finish();
    }

    @OnClick(R2.id.Snooze_Button)
    public void clicksnooze() {
        Button button =findViewById(R2.id.Snooze_Button);
        didTapButton(button);
        try{wait(300);}catch (Exception e){};

        if(vibrator != null) vibrator.cancel();
        Intent intent = new Intent(this, xDripSnoozePickerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        intent.putExtra("default_snooze",default_snooze);
        this.startActivity(intent);
        snooze=true;
    finish();
    }


    private void Snooze(int Snooze_Minutes){
        DataBundle db = new DataBundle();
        db.putInt("snoozetime",Snooze_Minutes);
        Snoozed event = new Snoozed(db);
        HermesEventBus.getDefault().post(event);

    }

    public void didTapButton(Button button) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R2.anim.bounce);
        button.startAnimation(myAnim);}
    }
