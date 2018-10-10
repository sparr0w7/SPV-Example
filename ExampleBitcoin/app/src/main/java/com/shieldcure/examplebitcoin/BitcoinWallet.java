package com.shieldcure.examplebitcoin;

import android.net.Network;
import android.os.Environment;
import android.util.Log;

import com.squareup.okhttp.internal.Util;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
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

          path = path + "/bitcoin-" + uuid + ".wallet";
          kit = new WalletAppKit(getTestNetNetwork(), new File(path), "test");
          kit.setAutoSave(true);

     }

     public String getBalance() {
          return kit.wallet().getBalance(Wallet.BalanceType.AVAILABLE).toString();
     }


     public void send(String toAddress, String amount) {
          try {
               SendRequest request = SendRequest.to(Address.fromBase58(getTestNetNetwork(), toAddress), Coin.parseCoin(amount));
               kit.wallet().completeTx(request);
               kit.wallet().commitTx(request.tx);
               kit.peerGroup().broadcastTransaction(request.tx).broadcast();
          } catch (InsufficientMoneyException e) {
               e.printStackTrace();
          }
     }

     public void test()
     {
          String hex  = "b582544761533209a3ad16e4d0c8267695b9b2d1f2739d198799b07a10140de4";
          BigInteger big = new BigInteger(hex, 16);

          // ECKey key = new ECKey();

          ECKey key = ECKey.fromPrivate(big, true);
          Log.e(TAG, "mainnet ***************************************************************************************");
          Log.e(TAG, "getPrivateKeyAsHex : " + key.getPrivateKeyAsHex()+ " ("+  key.getPrivateKeyAsHex().length() + ")");
          Log.e(TAG, "getPublicKeyAsHex : " + key.getPublicKeyAsHex()+ " ("+  key.getPublicKeyAsHex().length() + ")");

          Log.e(TAG, "getPrivateKeyAsWiF(main) : " + key.getPrivateKeyAsWiF(MainNetParams.get()) + " ("+  key.getPrivateKeyAsWiF(MainNetParams.get()).length() + ")");
          Log.e(TAG, "toAddress(main) : " +  key.toAddress(MainNetParams.get()).toString()+ " ("+  key.toAddress(MainNetParams.get()).toString().length() + ")");

          Log.e(TAG, "getPrivateKeyAsWiF(test) : " + key.getPrivateKeyAsWiF(TestNet3Params.get()) + " ("+  key.getPrivateKeyAsWiF(TestNet3Params.get()).length() + ")");
          Log.e(TAG, "toAddress(test) : " +  key.toAddress(TestNet3Params.get()).toString()+ " ("+  key.toAddress(TestNet3Params.get()).toString().length() + ")");
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
