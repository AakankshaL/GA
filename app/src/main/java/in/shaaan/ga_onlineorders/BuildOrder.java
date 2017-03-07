package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildOrder extends AppCompatActivity {

    private static final String TAG = "BuildOrder";
    private static final String REQUIRED = "This is required";


    private DatabaseReference databaseReference;

    /*@Bind(R.id.custName)
    AutoCompleteTextView custName;
    @Bind(R.id.submit)
    Button mSend;
    @Bind(R.id.prodList)
    TextView prodName;*/

    private TextView mTextView;
    private AutoCompleteTextView mActv;
    private Button mButton;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);

        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] drugArr = getResources().getStringArray(R.array.drugList);
        String[] custArr = getResources().getStringArray(R.array.custList);
        List<String> drugList = Arrays.asList(drugArr);
        List<String> custList = Arrays.asList(custArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutItemId, drugList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, layoutItemId, custList);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteview);
        autoCompleteTextView.setAdapter(adapter);

        /*AutoCompleteTextView autoCompleteTextView1 = (AutoCompleteTextView) findViewById(R.id.custName);
        autoCompleteTextView1.setAdapter(adapter1);*/

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mTextView = (TextView) findViewById(R.id.prodList);
        mActv = (AutoCompleteTextView) findViewById(R.id.custName);
        mActv.setAdapter(adapter1);
        mButton = (Button) findViewById(R.id.submit);

    }


    public void addProduct(View view) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteview);
        EditText editText = (EditText) findViewById(R.id.quantity);
        String quantity = editText.getText().toString();
        String drug = autoCompleteTextView.getText().toString();
        TextView textView = (TextView) findViewById(R.id.prodList);
        if (drug.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else
            textView.append("" + drug + "     " + quantity + "\n");
        autoCompleteTextView.getText().clear();
        editText.getText().clear();
        autoCompleteTextView.requestFocus();
    }

    public void sendOrder(View view) {
        submitOrder();
    }


    private void submitOrder() {
        final String customer = mActv.getText().toString();
        final String product = mTextView.getText().toString();

        if (TextUtils.isEmpty(customer)) {
            mActv.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(product)) {
            mTextView.setError(REQUIRED);
        }

        // Disable the submit button to prevent multiple orders
        setEditing(false);
        Toast.makeText(this, "Sending Order..", Toast.LENGTH_LONG).show();

        final String eMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final String userId = getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("salesman").child(userId);
        reference.child("email").setValue(eMail);
        String key = reference.child("salesman").child(userId).child("orders").push().getKey();
        reference.child(key).child("custName").setValue(customer);
        reference.child(key).child("bill").setValue(product);
        setEditing(true);
        finish();

        /*final String userId = getUid();
        databaseReference.child("salesman").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    //Unknown user
                    Log.e(TAG, "User" + userId + "is null");
                    *//*Intent i = new Intent(BuildOrder.this, LoginActivity.class);
                    startActivity(i);*//*
                    finish();
                } else {
                    submitOrder(userId, user.username, customer, product);
                }

                setEditing(true);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                setEditing(true);
                // [END_EXCLUDE]
            }
        });*/

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setEditing(boolean enabled) {
        mActv.setEnabled(enabled);
        mTextView.setEnabled(enabled);
        if (enabled) {
            mButton.setVisibility(View.VISIBLE);
        } else {
            mButton.setVisibility(View.GONE);
        }
    }

    private void submitOrder(String userId, String username, String customerName, String orderProducts) {
        String key = databaseReference.child("salesman").child("order").push().getKey();
        Post post = new Post(userId, username, customerName, orderProducts);
        Map<String, Object> postValue = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/salesman/" + key, postValue);
        childUpdates.put("/salesman/" + userId + "/" + key, postValue);

        databaseReference.updateChildren(childUpdates);
    }

}
