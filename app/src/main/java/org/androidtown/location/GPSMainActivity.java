package org.androidtown.location;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GPSMainActivity extends Activity {

    private static final int MAIN_ACTIVITY_CODE = 1002;

    private Bluetooth bluetooth;

    public static final int REQUEST_ENABLE_BT = 1;// REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)

    private String bluetoothMessage;

    private boolean setBluetoothSign = false;

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_main);

        bluetooth = Bluetooth.getInstance();

        Button connectBluetooth_BT = (Button)findViewById(R.id.connectBluetooth);
        connectBluetooth_BT.setOnClickListener(this::connectBluetoothButtonClick);

        textView = (TextView)findViewById(R.id.data);



    }


    private void connectBluetoothButtonClick(View view) {
        bluetoothMessage = bluetooth.setBluetooth();

        if (bluetoothMessage == "error1")
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();

        else if (bluetoothMessage == "error2") {
            Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);//startActivityForResult에  REQUEST_ENABLE_BT 전달

        } else if (bluetoothMessage == "hasBluetooth") {
            createDevicesDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //RESULT_OK : -1 RESULT_CANCLE : 0

        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                createDevicesDialog();
            }
        }

    }

    private void createDevicesDialog() {

        bluetoothMessage = bluetooth.findListDevice();

        if (bluetoothMessage == "emptyPair")
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();

        else if (bluetoothMessage == "hasPairDevice") {

            AlertDialog.Builder AlertDialogbuilder = new AlertDialog.Builder(this);//AlertDialog 이용 블루투스 목록표시
            AlertDialogbuilder.setTitle("블루투스 장치 선택");//목록title


            // 블루투스 목록을 표시하고 각 다른 이름을 가진 디바이스들을 표시한다.
            List<String> listItems = new ArrayList<>();//ArrayList 객체생성
            for (BluetoothDevice device : bluetooth.getDevices()) {
                listItems.add(device.getName()); // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            }
            listItems.add("취소");  // 취소 항목 추가.


            // CharSequence : 변경 가능한 문자열 (객체)
            // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);//리스트 배열을 사용하기위한 선언

            AlertDialogbuilder.setItems(items, new DialogInterface.OnClickListener() {//클릭시 setItems 리스트추가 메소드

                @Override
                public void onClick(DialogInterface dialog, int item) {//리스트내 아이템 클릭
                    // TODO Auto-generated method stub
                    if (item == bluetooth.getPairDeviceCount()) { // 취소 버튼을 누른경우
                        Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    } else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                        bluetoothMessage = bluetooth.connectToSelectedDevice(items[item].toString());//생성된 장치목록을 ConnectToSelectedDevice 메소드에 보냄
                        if (bluetoothMessage == "connectedBluetooth") {
                            Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.", Toast.LENGTH_LONG).show();
                            setBluetoothSign = true;

                            MyThread myThread = new MyThread();
                            myThread.startThread();

                        } else if (bluetoothMessage == "cannotConnectedBluetooth") {
                            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                            setBluetoothSign = false;
                        }
                    }
                }

            });//end setItems

            AlertDialogbuilder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
            AlertDialog alert = AlertDialogbuilder.create();//알림창 객체 생성
            alert.show();//알림창 띄우기
        }
    }//end createDevicesDialog

    class MyThread extends Thread{
        private Bluetooth bluetooth;
        private InputStream inputStream;
        private boolean threadRunSign = false;
        private Handler handler;

        public MyThread(){

            handler = new Handler();
            bluetooth = Bluetooth.getInstance();
            inputStream = bluetooth.getInputStream();
        }

        public void startThread(){
            try{
                this.start();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public void stopThread(){
            threadRunSign = true;
        }


        @Override
        public void run() {
            super.run();


            byte[] readBuffer = new byte[30];
            int readBufferIndex = 0;

            while (!threadRunSign) {
                try {
                    this.sleep(1000);

                    int inputStreamAvailable = inputStream.available();
                    System.out.println("inputStreamAvailable"+inputStreamAvailable);
                    if(inputStreamAvailable >= 0){
                        byte[] inputStreamData = new byte[inputStreamAvailable];
                        inputStream.read(inputStreamData);

                        for(int i=0; i<inputStreamAvailable; i++){
                            byte getByteData = inputStreamData[i];
                            char lineAlignment = '\n';
                            if(getByteData==lineAlignment){
                                byte[] enCodedByteDatas = new byte[readBufferIndex];
                                System.arraycopy(readBuffer, 0, enCodedByteDatas, 0, enCodedByteDatas.length);
                                String data = new String(enCodedByteDatas,"utf-8");
                                readBufferIndex = 0;
                                handlerMessage(data);
                            }//end getByteData==lineAlignment if

                            else{
                                readBuffer[readBufferIndex++] = getByteData;
                            }

                        }//end for
                    }


                }//end try
                catch (Exception e){
                    e.printStackTrace();
                }
            }//end while
        }//end


        public void handlerMessage(String data){
            handler.post(new Runnable() {
                @Override
                public void run() {

                    textView.setText(data);

                    String[] location = data.split(",");
                    String lat=location[0];
                    String lon=location[1];

                    //버튼 이벤트 위도 경도 값 MainActivity로 intent
                    Button mapButton=(Button)findViewById(R.id.mapButton);
                    mapButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent=new Intent(GPSMainActivity.this, MainActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("lat",lat);
                            intent.putExtra("lon",lon);

                            startActivity(intent);

                        }
                    });

                }//end run
            });//end post
        }
    }


}