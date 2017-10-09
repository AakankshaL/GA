package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.shaaan.ga_onlineorders.pojo.OrderData;


public class BuildOrder extends AppCompatActivity {

    private static final String TAG = "BuildOrder";
    private static final String REQUIRED = "This is required";
    @Bind(R.id.submit)
    FloatingActionButton submit;
    @Bind(R.id.scheme_checkbox)
    CheckBox checkBox;
    @Bind(R.id.custName)
    AutoCompleteTextView completeTextView;
    @Bind(R.id.autocompleteview)
    AutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.quantity)
    EditText editText;
    @Bind(R.id.orderList)
    RecyclerView recyclerView;
    @Bind(R.id.addProduct)
    Button addProduct;
    /*@Bind(R.id.prod_del)
    Button productDelete;*/
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private String k;
    private LinearLayoutManager linearLayoutManager;
    private List<OrderData> orderData = new ArrayList<>();
    private RecyclerAdapterFile mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);
        ButterKnife.bind(this);
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] drugArr = getResources().getStringArray(R.array.drugList);
        String[] custArr = getResources().getStringArray(R.array.custList);
        String[] strings = getResources().getStringArray(R.array.salesmen);
        List<String> salesmen = Arrays.asList(strings);
        List<String> drugList = Arrays.asList(drugArr);
        final List<String> custList = Arrays.asList(custArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutItemId, drugList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, layoutItemId, custList);

        autoCompleteTextView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerAdapterFile(orderData);
        recyclerView.setAdapter(mAdapter);

        completeTextView.setAdapter(adapter1);
        GaFirebase.isCalled();
        String s = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String bh = custList.toString();
        String s1 = salesmen.toString();
        StringTokenizer stringTokenizer = new StringTokenizer(s, "@");
        String partyTemp = stringTokenizer.nextToken().trim();

        if (s1.contains(s)) {
            completeTextView.setEnabled(true);
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
        } else if (bh.contains(partyTemp)) {
            completeTextView.setText(partyTemp);
            completeTextView.setEnabled(false);
        } else {
            completeTextView.setText("Not a valid user");
            completeTextView.setEnabled(false);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Signed in?", "We logged in" + user.getEmail());
            }
        };

    }

    public void addProduct(View view) {
        String quantity = editText.getText().toString();
        String drug = autoCompleteTextView.getText().toString();
        if (drug.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else {
//            prodList.append("" + drug + "     " + quantity + "\n");

            getDataA();
        }
        autoCompleteTextView.getText().clear();
        editText.getText().clear();
        autoCompleteTextView.requestFocus();
    }

    public OrderData getDataA() {
        OrderData instance = new OrderData();
        instance.setProduct(autoCompleteTextView.getText().toString());
        instance.setQuantity(editText.getText().toString());
        Log.d("I am doing something", "seriously?");
        if (checkBox.isChecked()) {
            instance.setScheme("With scheme");
        }
        return instance;
    }

//    public void sendOrder(View view) {
//        submitOrder();
//    }

    private void submitOrder() {
        final String customer = completeTextView.getText().toString();

        if (TextUtils.isEmpty(customer)) {
            completeTextView.setError(REQUIRED);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm");
        final String date = simpleDateFormat.format(calendar.getTime());
        Log.d("TAG", date);

        // Disable the submit button to prevent multiple orders
        setEditing(false);
        Toast.makeText(this, "Sending Order..", Toast.LENGTH_LONG).show();
        final String eMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String userId = getUid();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("salesman").child(userId);
                reference.keepSynced(true);
                String key = reference.child("salesman").child(userId).child("orders").push().getKey();
                reference.child(key).child("custName").setValue(customer);
//                reference.child(key).child("products").setValue(product);
                reference.child(key).child("email").setValue(eMail);
//                reference.child(key).child("expProducts").setValue(expProduct);
                reference.child(key).child("date").setValue(date);
            }
        });
        thread.start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("allOrders");
                reference.keepSynced(true);
                String key = reference.push().getKey();
                reference.child(key).child("email").setValue(eMail);
                reference.child(key).child("date").setValue(date);
                reference.child(key).child("custName").setValue(customer);
//                reference.child(key).child("products").setValue(product);
//                reference.child(key).child("expProducts").setValue(expProduct);
            }
        });
        t.start();

        setEditing(true);
        finish();

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setEditing(boolean enabled) {
        completeTextView.setEnabled(enabled);
//        prodList.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }
    }

}