package com.shieldcure.exampledash;

import android.os.Environment;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.util.UUID;

public class BitcoinWallet extends DownloadProgressTracker {
     public WalletAppKit kit;
     public String path;


     ///지갑이 있을경우
     public BitcoinWallet(String path) {
          kit = new WalletAppKit(getTestNetNetwork(), new File(path), "test");
          kit.setAutoSave(true);
     }


     ///지갑이 없을경우
     public BitcoinWallet() {
          path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
          UUID uuid = UUID.randomUUID();

          path = path + "/dash-" + uuid + ".wallet";
          kit = new WalletAppKit(getTestNetNetwork(), new File(path), "test");
          kit.setAutoSave(true);

     }

     public String getBalance()
     {
          return kit.wallet().getBalance().toFriendlyString();
     }


     public void send(String toAddress , String amount)
     {
          System.out.println(amount);
          Coin value = Coin.parseCoin(amount);
          Address to = Address.fromBase58(getTestNetNetwork(),toAddress);

          try {
               Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(),to,value);
          } catch (InsufficientMoneyException e) {
               System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
               System.out.println("Send money to: " + kit.wallet().currentReceiveAddress().toString());

               // Bitcoinj allows you to define a BalanceFuture to execute a callback once your wallet has a certain balance.
               // Here we wait until the we have enough balance and display a notice.
               // Bitcoinj is using the ListenableFutures of the Guava library. Have a look here for more information: https://github.com/google/guava/wiki/ListenableFutureExplained
               ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, Wallet.BalanceType.AVAILABLE);
               FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                    @Override
                    public void onSuccess(Coin balance) {
                         System.out.println("coins arrived and the wallet now has enough balance");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                         System.out.println("something went wrong");
                    }
               };
               Futures.addCallback(balanceFuture, callback);
          }
     }


     private NetworkParameters getMainNetNetwork()
     {
          return MainNetParams.get();
     }

     private NetworkParameters getTestNetNetwork()
     {
          return TestNet3Params.get();
     }

}
