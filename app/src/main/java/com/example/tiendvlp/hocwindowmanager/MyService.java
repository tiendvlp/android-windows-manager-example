package com.example.tiendvlp.hocwindowmanager;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyService extends Service implements View.OnTouchListener {
    private WindowManager windowManager;
    private MyGroupView myGroupView;
    private View myViewIcon;
    private View myViewContent;
    private TextView txtIcon;
    private WindowManager.LayoutParams layoutParamsIcon;
    private WindowManager.LayoutParams layoutParamsContent;
    private EditText txtNoidung;
    private EditText txtSdt;
    private Button btnSend;
    private float PreviousX;
    private float PreviousY;
    private float StartX;
    private float StartY;
    private int state;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InitView();
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void InitView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        myGroupView = new MyGroupView(getApplicationContext());
        createIconView();
    }

    private void createContentView() {
        myViewContent = View.inflate(getApplicationContext(),R.layout.content, myGroupView);
        myViewContent.setOnTouchListener(this);
        txtNoidung = myViewContent.findViewById(R.id.txtNotdung);
        btnSend = myViewContent.findViewById(R.id.btnSend);
        txtSdt = myViewContent.findViewById(R.id.txtSdt);
        layoutParamsContent = new WindowManager.LayoutParams();
        layoutParamsContent.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParamsContent.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParamsContent.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParamsContent.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//        layoutParamsContent.flags|= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParamsContent.gravity = Gravity.CENTER;
        windowManager.removeView(myViewIcon);
        windowManager.addView(myViewContent, layoutParamsContent);
        state = 1;
        myViewContent.setFocusable(true);
        myViewContent.setFocusableInTouchMode(true);
    }

    private void createIconView() {
        // vẽ ra cái view của nó
        myViewIcon = View.inflate(getApplicationContext(),R.layout.view_icon, null);
        // set onTouch cho nó để kéo thả các kiểu
        myViewIcon.setOnTouchListener(this);
        txtIcon = myViewIcon.findViewById(R.id.txtIcon);
        layoutParamsIcon = new WindowManager.LayoutParams();
        layoutParamsIcon.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParamsIcon.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // thuộc tính này rất quan trọng dùng để cho icon luôn luôn nằm trên màn hình điện thoại và đè lên các app khác
        // lưu ý là phải xin quyền SYSTEM_ALERT_WINDOW
        layoutParamsIcon.type = WindowManager.LayoutParams.TYPE_PHONE;
        // vị trí ban đầu của nó sẽ nằm ở đâu trên màn hình
        layoutParamsIcon.gravity = Gravity.CENTER;
        // cho backgroud phía sau nó trong suốt nhìn đẹp hơn
        layoutParamsIcon.format = PixelFormat.TRANSPARENT;
        // nếu không chỉnh thuộc tính này thì sẽ không tương tác được với các thành phần bên ngoài
        // chú ý là khi sử dụng cách này ta sẽ bị tình trạng là không tương tác được với các view bên trong
        // khắc phục bằng cách setOnTouch hay OnClick gì đó cho từng view bên trong rồi layoutParamsIcon.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // để bỏ đi thằng này rồi sau đó khi người dùng nhấn ra ngoài thì enable lại
        layoutParamsIcon.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParamsIcon.flags|= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParamsIcon.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        windowManager.addView(myViewIcon, layoutParamsIcon);
        state = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private boolean clicking = false;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (state == 0) {
                    PreviousX = layoutParamsIcon.x;
                    PreviousY = layoutParamsIcon.y;
                    clicking = true;
                }
                StartX = motionEvent.getRawX();
                StartY = motionEvent.getRawY();
                if (state == 1) {
                    layoutParamsContent.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                    windowManager.updateViewLayout(myViewContent, layoutParamsContent);
                    Log.d("Fuck", "fuck you" + (view.getId() == R.id.LnIcon));
                    Rect rectout = new Rect();
                    myViewContent.getDrawingRect(rectout);
                    if (!rectout.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY())) {
                    }
                    Log.d("Fuck", "x: " + myViewContent.getWidth());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == 0) {
                    float deltaX = motionEvent.getRawX() - StartX;
                    float deltaY = motionEvent.getRawY() - StartY;
                    layoutParamsIcon.x = (int) ((int) deltaX + PreviousX) - 10;
                    layoutParamsIcon.y = (int) ((int) deltaY + PreviousY) - 10;
                    windowManager.updateViewLayout(myViewIcon, layoutParamsIcon);
                    clicking = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state == 0) {
                    if (clicking) {
                        createContentView();
                        clicking = false;
                    }
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                    layoutParamsIcon.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    layoutParamsIcon.flags|= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                    layoutParamsIcon.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//                    windowManager.updateViewLayout(myViewContent, layoutParamsContent);
                    Log.d("Fuck", "fuck you2");
                break;
        }
        return false;
    }

}
