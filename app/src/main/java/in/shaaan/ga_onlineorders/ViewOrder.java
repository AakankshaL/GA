package in.shaaan.ga_onlineorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewOrder extends AppCompatActivity {

    @Bind(R.id.vieworder_custName)
    TextView mCustName;
    @Bind(R.id.vieworder_date)
    TextView mDate;
    @Bind(R.id.vieworder_order)
    TextView mOrder;
    @Bind(R.id.vieworder_exp)
    TextView mExpiry;
    @Bind(R.id.vieworder_salesman)
    TextView mSalesman;
    @Bind(R.id.delete_order)
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String customer = intent.getStringExtra("custName");
        String order = intent.getStringExtra("order");
        String date = intent.getStringExtra("date");
        String by = intent.getStringExtra("by");
        final String orderURL = intent.getStringExtra("orderURL");
        String exp = intent.getStringExtra("exp");

        StringTokenizer stringTokenizer = new StringTokenizer(by, "@");
        String salesman = stringTokenizer.nextToken().trim();

        if (customer != null && date != null && order != null && by != null) {
            mCustName.setText(customer);
            mDate.setText(date);
            mOrder.setText(order);
            mSalesman.setText(salesman);
        } /*else {
            Toast.makeText(this, "Its null", Toast.LENGTH_SHORT).show();
        }*/

        if (exp != null) {
            mExpiry.setText(exp);
        } else {
            mExpiry.setText("Not Available");
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = GaFirebase.isCalled().getReference(orderURL);
                databaseReference.removeValue();
                Log.d("Removed,", orderURL);
                finish();
            }
        });
    }
}
