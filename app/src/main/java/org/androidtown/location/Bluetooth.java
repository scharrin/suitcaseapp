package org.androidtown.location;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by inno on 2017-06-11.
 */

public class Bluetooth
{

    private BluetoothDevice connectDevice;//블루투스 장치변수

    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    private BluetoothSocket socket;//블루투스 통신소켓 객체
    private InputStream inputStream;//블루투스 소켓 송신 데이터

    private static Bluetooth bluetooth;

    public static Bluetooth getInstance() {
        if (bluetooth == null) {
            bluetooth = new Bluetooth();
        }
        return bluetooth;
    }


    private BluetoothAdapter bluetoothAdapter;//블루투스 어댑터 얻기
    private Set<BluetoothDevice> devices;// 블루투스장치설정변수


    //블루투스 유무확인
    public String setBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //BluetoothAdapter.getDefaultAdapter(); 만일 폰에 블루투스 모듈이 없다면 null을 리턴

        if (bluetoothAdapter == null) {  // 블루투스 미지원시
            return "error1";
        }//end 블루투스 미지원시

        else { // 블루투스 지원시

            if (!bluetoothAdapter.isEnabled()) { // // 블루투스 연결이 안됫을경우. .isEnabled 사용가능여부
                return "error2";
            }

        }//end 블루투스 지원시
        return "hasBluetooth";
    }//end setBluetooth

    //블루투스 장치 찾기
    //FindListDevice 는 AlertDialog 이용해서 블루투스 장치들의 목록을 보여준다.
    private int pairDeviceCount = 0;

    public int getPairDeviceCount() {
        return pairDeviceCount;
    }

    public String findListDevice() {

        devices = bluetoothAdapter.getBondedDevices(); // getBondedDevices() : 장치의 목록을 얻어온다
        pairDeviceCount = devices.size();//디바이스 변수

        if (pairDeviceCount == 0) { // 페어링할 장치가 없는 경우.
            return "emptyPair";
        } else {//페어링할 장치가 있는경우
            return "hasPairDevice";
        }//end else페어링할 장치가 있는경우
    }//end FindListDevice

    public Set<BluetoothDevice> getDevices() {
        return devices;
    }

    //end 블루투스 장치 찾기


    //선택된 블루투스 장치 에 수신받을 소켓 생성
    public String connectToSelectedDevice(String name) {//String name FindListDevice 메소드에서 선택한 장치 매개변수

        BluetoothDevice selectedDeviceName=null;//FindListDevice메소드에서 선택한 장치 저장객체변수


        //FindListDevice 메소드에서 선택한 장치를 사용하기위한 작업
        for(BluetoothDevice device : devices){//연결된장치를 device 변수로 사용
            if(name.equals(device.getName())){//장치이름이 선택한 장치와 이름이 같다면
                selectedDeviceName=device;//
                break;
            }
        }


        connectDevice = (selectedDeviceName);//연결할 장치 선택을 사용하기 위한 선언
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.

        try {
            socket = connectDevice.createRfcommSocketToServiceRecord(uuid);//안드로이드 블루투스 연결소켓생성
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            socket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료

            // 데이터 송수신을 위한 스트림 얻기.
            inputStream = socket.getInputStream();  //  데이터를 받기 위한 InputStream
            return "connectedBluetooth";



        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            return "cannotConnectedBluetooth";
        }


    }//end connectToSelectedDevice

    public InputStream getInputStream() {
        return inputStream;
    }
}