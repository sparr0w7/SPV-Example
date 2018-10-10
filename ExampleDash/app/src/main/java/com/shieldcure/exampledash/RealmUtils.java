package com.shieldcure.exampledash;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmUtils {
    public static Realm realm;
    public RealmUtils(Context mContext)
    {
        realm.init(mContext);
        realm = Realm.getDefaultInstance();
    }

    public void addData(final Map<String,String> wallet)
    {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Data data = realm.createObject(Data.class);
                data.name = wallet.get("name");
                data.address = wallet.get("address");
                data.balance = wallet.get("balance");
                data.path = wallet.get("path");
            }
        });
    }

    public boolean isEmpty()
    {
        return realm.isEmpty();
    }

    public Data getData()
    {
        ArrayList<Data> wallets = new ArrayList<>();
        RealmResults<Data> results = realm.where(Data.class).findAll();

        return realm.copyFromRealm(results).get(0);
    }
}
