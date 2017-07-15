package com.jamnguyen.stormx;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class XBluetooth
{
    private Context             m_appContext;
    private String              m_deviceAddress = null;
    private BluetoothAdapter    m_BTAdapter = null;
    private BluetoothSocket     m_BTSocket = null;
    private boolean             m_isConnected = false;
//    private String              m_command;
    private String              m_prevCommand;

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public XBluetooth(String Address, Context context)
    {
        m_appContext = context;
        m_deviceAddress = Address;
        m_prevCommand = "";
    }

    public boolean Init()
    {
        //Call the class to connect
        new ConnectBT().execute();
        return m_isConnected;
    }

    public void Disconnect()
    {
        if (m_BTSocket!=null) //If the BTSocket is busy
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

    //UI thread
    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            toastShort("Bluetooth connecting...");
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (m_BTSocket == null || !m_isConnected)
                {
                    //Get the mobile bluetooth device
                    m_BTAdapter = BluetoothAdapter.getDefaultAdapter();

                    //Connects to the device's address and checks if it's available
                    BluetoothDevice dispositivo = m_BTAdapter.getRemoteDevice(m_deviceAddress);

                    //Create a RFCOMM (SPP) connection
                    m_BTSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    //Start connection
                    m_BTSocket.connect();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        //After the doInBackground, it checks if everything went fine
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                toastLong("Connection Failed. Is it a SPP Bluetooth? Try again.");
                m_isConnected = false;
            }
            else
            {
                toastLong("Connected.");
                m_isConnected = true;
            }
        }
    }

    public boolean isConnected()
    {
        return m_isConnected;
    }


    private void send(String s)
    {
        if (m_BTSocket!=null)
        {
            try
            {
                m_BTSocket.getOutputStream().write(s.toString().getBytes());
                m_prevCommand = s;
            }
            catch (IOException e)
            {
                toastLong("Error");
            }
        }
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
