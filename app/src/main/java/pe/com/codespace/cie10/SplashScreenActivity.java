package pe.com.codespace.cie10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class SplashScreenActivity extends Activity {

    private static final long SPLASH_SCREEN_DELAY = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        Thread start_splash = new Thread() {
            @Override
            public void run() {
                try{
                    sleep(SPLASH_SCREEN_DELAY);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        start_splash.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
