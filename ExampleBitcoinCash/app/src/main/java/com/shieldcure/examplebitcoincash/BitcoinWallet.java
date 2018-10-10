package com.shieldcure.examplebitcoincash;

import android.os.Environment;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BitcoinWallet extends DownloadProgressTracker {
     public WalletAppKit kit;
     public String path;
     String TAG = "BitcoinWallet";


     ///지갑이 있을경우
     public BitcoinWallet(String path) {
          kit = new WalletAppKit(getMainNetNetwork(), new File(path), "test");
          kit.setAutoSave(true);
     }


     ///지갑이 없을경우
     public BitcoinWallet() {
          path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
          UUID uuid = UUID.randomUUID();

          path = path + "/cash-" + uuid + ".wallet";
          kit = new WalletAppKit(getMainNetNetwork(), new File(path), "test");
          kit.setAutoSave(true);

     }

     public String getBalance()
     {
          return kit.wallet().getBalance().toFriendlyString();
     }


     public void send(String toAddress , String amount)
     {
          try {
               SendRequest request = SendRequest.to(Address.fromBase58(getMainNetNetwork(), toAddress), Coin.parseCoin(amount));
               request.setUseForkId(true);
               kit.wallet().completeTx(request);
               kit.wallet().commitTx(request.tx);
               kit.peerGroup().broadcastTransaction(request.tx).broadcast();
          } catch (InsufficientMoneyException e) {
               e.printStackTrace();
          }
     }

     public void uncomfirm()
     {

          System.out.println(kit.wallet().toString());
          List<TransactionOutput> outputs = kit.wallet().getUnspents();
          List<TransactionOutput>  outputs2 = kit.wallet().calculateAllSpendCandidates();
          System.out.println(kit.wallet().getBalance());
          Collection<Transaction> pendingTransactions =  kit.wallet().getPendingTransactions();

          System.out.println(pendingTransactions.size());
          for (Transaction transaction: pendingTransactions) {
               Log.d(TAG, "transatcion: "+transaction.toString());
          }
     }

     public void export()
     {
          Log.d(TAG, "export: ");
          List<ECKey> keys = kit.wallet().getIssuedReceiveKeys();
          for (ECKey key: keys) {
               Log.d(TAG, "getPrivateKeyAsWiF(main) : " + key.getPrivateKeyAsWiF(MainNetParams.get()) + " ("+  key.getPrivateKeyAsWiF(MainNetParams.get()).length() + ")");
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
