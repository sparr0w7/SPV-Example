package com.shieldcure.examplebitcoincash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.bitcoinj.core.listeners.DownloadProgressTracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements PermissionListener,View.OnClickListener{

    Context mContext;
    RealmUtils realm;
    Button btn_send , btn_copy, btn_refrash, btn_download;
    TextView tv_address , tv_balance;
    EditText toAddress , amount;
    Data mWallet;
    ProgressDialog mProgressDialog;
    ProgressBar mProgressBar;
    BitcoinWallet wallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        btn_send = (Button) findViewById(R.id.send);
        btn_copy = (Button) findViewById(R.id.copy);
        btn_refrash = (Button) findViewById(R.id.refrash);
        btn_download = (Button) findViewById(R.id.Download);

        tv_address = (TextView) findViewById(R.id.address);
        tv_balance = (TextView) findViewById(R.id.balance);
        toAddress = (EditText) findViewById(R.id.to_address);
        amount = (EditText) findViewById(R.id.amount);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        btn_send.setOnClickListener(this);
        btn_copy.setOnClickListener(this);
        btn_refrash.setOnClickListener(this);
        btn_download.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);

        realm = new RealmUtils(mContext);
        TedPermission.with(mContext)
                .setPermissionListener(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send:
                send();
                break;
            case R.id.copy:
                saveToClipBoard(wallet.kit.wallet().currentReceiveAddress().toBase58());
                break;
            case R.id.refrash: {
                tv_balance.setText(wallet.getBalance());
                break;
            }
            case R.id.Download: {
                startBlockDownload();
                break;
            }
        }
    }

    public void send()
    {
        wallet.send(toAddress.getText().toString() , amount.getText().toString());
    }



    @Override
    public void onPermissionGranted() {
        if(realm.isEmpty()) {
            /*new CreateWalletTask().execute();*/
            wallet = new BitcoinWallet();
            startBlockDownload();
            HashMap<String,String> data = new HashMap<String, String>();
            data.put("name","test");
            data.put("address",wallet.kit.wallet().currentReceiveAddress().toBase58());
            data.put("balance",wallet.getBalance());
            data.put("path",wallet.path);
            new RealmUtils(MainActivity.this).addData(data);
            tv_address.setText(wallet.kit.wallet().currentReceiveAddress().toBase58());

        }else {

            mWallet = realm.getData();
            wallet = new BitcoinWallet(mWallet.path);
            startBlockDownload();
           /* try {
                //tv_balance.setText(new Bitcoin().getBalance(mWallet.path + "/" + mWallet.name));
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }*/
            tv_address.setText(mWallet.address);
        }
        wallet.export();

    }


    //블록 동기화
    public void startBlockDownload()
    {
        wallet.kit.setDownloadListener(new DownloadProgressTracker(){
            @Override
            protected void progress(double pct, int blocksSoFar, Date date) {
                super.progress(pct, blocksSoFar, date);
                Log.d("progress", "progress: "+ pct);
                mProgressBar.setProgress((int) pct);
            }

            @Override
            protected void doneDownload()
            {
                super.doneDownload();
                mProgressBar.setProgress(100);
                wallet.uncomfirm();

            }
        });
        wallet.kit.setBlockingStartup(false);
        wallet.kit.startAsync();
        wallet.kit.awaitRunning();
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

    }

    public void saveToClipBoard(String text)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

}
