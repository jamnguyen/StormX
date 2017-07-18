package com.jamnguyen.stormx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.InputMismatchException;
import java.util.UUID;

public class XBluetooth
{
    private Activity            m_appActivity;
    private Context             m_appContext;
    private String              m_deviceAddress = null;

    private String              m_readBuffer = "";
    private String              m_status;

    private boolean             m_isConnected = false;

    private Handler             m_Handler; // Our main handler that will receive callback notifications
    private ConnectedThread     m_ConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket     m_BTSocket = null; // bi-directional client-to-client data path
    private BluetoothAdapter    m_BTAdapter;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    public XBluetooth(String Address, Context context)
    {
        m_appContext = context;
        m_deviceAddress = Address;
    }

    public XBluetooth(String Address, Context context, Activity activity)
    {
        m_appContext = context;
        m_appActivity = activity;
        m_deviceAddress = Address;
    }

    public void Init()
    {
        m_BTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        m_Handler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if(msg.what == MESSAGE_READ)
                {
                    String readMessage = null;
                    try
                    {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
//                        m_readBufferTextView.setText(readMessage);
                        toastShort(readMessage);
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                if(msg.what == CONNECTING_STATUS)
                {
                    if(msg.arg1 == 1)
                    {
                        m_status = "Connected to Device: " + (msg.obj);
                        toastShort(m_status);
                        m_isConnected = true;
                    }
                    else
                    {
                        m_status = "Connection Failed";
                        toastShort(m_status);
                    }
                }
            }
        };

        //Connect
        m_status = "Connecting...";
        toastShort(m_status);

        // Spawn a new thread to avoid blocking the GUI one
        new Thread()
        {
            public void run()
            {
                boolean fail = false;

                BluetoothDevice device = m_BTAdapter.getRemoteDevice(m_deviceAddress);

                try
                {
                    m_BTSocket = createBluetoothSocket(device);
                } catch (IOException e)
                {
                    fail = true;
                    toastShort("Socket creation failed.");
                }
                // Establish the Bluetooth socket connection.
                try
                {
                    m_BTSocket.connect();
                }
                catch (IOException e)
                {
                    try
                    {
                        fail = true;
                        m_BTSocket.close();
                        m_Handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                    }
                    catch (IOException e2)
                    {
                        toastShort("Socket creation failed.");
                    }
                }
                if(!fail)
                {
                    m_ConnectedThread = new ConnectedThread(m_BTSocket);
                    m_ConnectedThread.start();
                    m_Handler.obtainMessage(CONNECTING_STATUS, 1, -1, m_deviceAddress).sendToTarget();
                }
            }
        }.start();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true)
            {
                try
                {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0)
                    {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        m_Handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input)
        {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try
            {
                mmOutStream.write(bytes);
            }
            catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void Disconnect()
    {
        if (m_BTSocket != null) //If the BTSocket is busy
        {
            try {
                m_BTSocket.close(); //close connection
            } catch (IOException e) {
                toastLong("Error");
            }
        }
    }

    public void TurnOffLed()
    {
        send("TF\n");
    }

    public void TurnOnLed()
    {
        send("TO\n");
    }

    public String getReadMessage()
    {
        return m_readBuffer;
    }

    public boolean isConnected()
    {
        return m_isConnected;
    }


    private void send(String s)
    {
        if(m_ConnectedThread != null) //First check to make sure thread created
            m_ConnectedThread.write(s);
    }

    //Fast way to call Toast
    private void toastLong(String s)
    {
        Toast.makeText(m_appContext, s, Toast.LENGTH_LONG).show();
    }

    private void toastShort(String s)
    {
        Toast.makeText(m_appContext, s, Toast.LENGTH_SHORT).show();
    }
}
