package com.example.funproduct;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText date,month,year,signup_email_editText,signup_password_editText,
            signup_repassword_editText,signup_mobile_no_editText;

    Button signup_next_button,signup_signup_button;

    EditText signup_first_name_editText,signup_last_name_editText;

    Spinner signup_gender_spinner,signup_profile_spinner,signup_marital_spinner;

    LinearLayout signup_personal_detail_layout,signup_account_detail_layout;


    boolean focus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = findViewById(R.id.date);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        signup_email_editText = findViewById(R.id.signup_email_editText);
        signup_password_editText = findViewById(R.id.signup_password_editText);
        signup_repassword_editText = findViewById(R.id.signup_repassword_editText);
        signup_mobile_no_editText = findViewById(R.id.signup_mobile_no_editText);
        signup_next_button = findViewById(R.id.signup_next_button);
        signup_personal_detail_layout = findViewById(R.id.signup_personal_detail_layout);
        signup_account_detail_layout = findViewById(R.id.signup_account_detail_layout);
        signup_signup_button = findViewById(R.id.signup_signup_button);
        signup_first_name_editText = findViewById(R.id.signup_first_name_editText);
        signup_last_name_editText = findViewById(R.id.signup_last_name_editText);
        signup_profile_spinner = findViewById(R.id.signup_profile_spinner);
        signup_gender_spinner = findViewById(R.id.signup_gender_spinner);
        signup_marital_spinner = findViewById(R.id.signup_marital_spinner);


        signup_next_button.setOnClickListener(this);
        signup_signup_button.setOnClickListener(this);

        signup_mobile_no_editText.setText(getIntent().getStringExtra("mobile"));

        passwordShow(signup_password_editText);
        passwordShow(signup_repassword_editText);

        year.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (year.getRight() - year.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        setDate(v);
                    }
                }
                return false;
            }
        });

        showDate(1997,01,01);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_next_button:
                nextStep();
                break;
            case R.id.signup_signup_button:
                signup();
                break;
        }
    }

    private void signup() {
        if (signup_first_name_editText.getText().toString().length()==0)
            makeToast("First name should be more then 1 letter");
        else if(signup_last_name_editText.getText().toString().length()<1)
            makeToast("Lase name should be more then 1 letter");
        else if(signup_profile_spinner.getSelectedItem().toString().equals("Profile"))
            makeToast("Profile should be different then Profile");
        else if(signup_gender_spinner.getSelectedItem().toString().equals("Gender"))
            makeToast("Gender Should be different then Gender");
        else if(signup_marital_spinner.getSelectedItem().toString().equals("Marital"))
            makeToast("Marital Should be different then Marital");
        else{
            sendSignUpData();
        }
    }

//    {"first_name":"Gyanendra","last_name":"Singh","email":"s.gyanendra1@gmail.com",
//            "profile":"SelfU","password":"123123123","mobile":"8791320067","gender":"M",
//            "marital":"U","dob":"08-01-1991","verify_mobile":1}

//    {"first_name":"Ankit","last_name":"Kishore","email":"ankitkishore12@gmail.com",
//            "profile":"SelfU","password":"ankit","mobile":"9971508288","gender":"M",
//            "marital":"U","dob":"2-8-1997","verify_mobile":1}

    private void sendSignUpData() {
        JSONObject j = new JSONObject();
        try {
            String m = signup_marital_spinner.getSelectedItem().toString();
            String marital = m.equals("Married")?"M":m.equals("Un-married")? "U":m.equals("Widowed")? "W":
                    m.equals("Awaiting Divorce")? "AD":m.equals("Divorce")? "D":"An";
            j.put("first_name", signup_first_name_editText.getText().toString());
            j.put("last_name",signup_last_name_editText.getText().toString());
            j.put("email",signup_email_editText.getText().toString());
            j.put("profile",signup_profile_spinner.getSelectedItem().toString());
            j.put("password",signup_password_editText.getText().toString());
            j.put("mobile",signup_mobile_no_editText.getText().toString());
            j.put("gender",signup_gender_spinner.getSelectedItem().toString().equals("Male")?"M":"F");
            j.put("marital",marital);
            j.put("dob",getDob());
            j.put("verify_mobile",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("json",j.toString());

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "http://rishtonkabandhan.com/webapi/userinic/doRegister",j,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("json",response.toString());
                        try {
                            if (response.getString("status").equals("1")) {
                                makeToast(response.getString("message"));
//                                startActivity(new Intent(SignUpDetails.this, LoginActivity.class));
                            }
                            else
                                makeToast(response.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        makeToast("SignUp error");
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void nextStep(){
        if(signup_email_editText.getText().toString().length()<1)
            makeToast("Email Should be more then 1 letter");
        else if(signup_password_editText.getText().toString().length()<1)
            makeToast("Password Should be more then 1 letter");
        else if(signup_repassword_editText.getText().toString().length()<1)
            makeToast("Repass Should be more then 1 letter");
        else if(signup_mobile_no_editText.getText().toString().length()<1)
            makeToast("Mobile Should be of 10 digit");
        else if(!signup_password_editText.getText().toString().equals(signup_repassword_editText.getText().toString()))
            makeToast("Re-password and password need to be same");
        else{
            signup_personal_detail_layout.setVisibility(View.VISIBLE);
            signup_account_detail_layout.setVisibility(View.GONE);
        }
    }

    private void makeToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }


    private void passwordShow(final EditText e){
        final boolean[] passShow = new boolean[1];
        e.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (e.getRight() - e.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!passShow[0]) {
                            e.setInputType(InputType.TYPE_CLASS_TEXT);
                            e.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_baseline_visibility_24px,0);
                            passShow[0] = true;
                        } else {
                            e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            e.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_baseline_visibility_off_24px,0);
                            passShow[0] = false;
                        }
                    }
                }
                return false;
            }
        });
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, 1997,01,01);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };


    private void showDate(int yy, int m, int d) {
        date.setText(d+"");
        month.setText(m+"");
        year.setText(yy+"");
    }

    private String getDob(){
        return date.getText().toString()+"-"+month.getText().toString()+"-"+year.getText().toString();
    }

    @Override
    public void onBackPressed() {
        if(signup_account_detail_layout.getVisibility() == View.GONE) {
            signup_personal_detail_layout.setVisibility(View.GONE);
            signup_account_detail_layout.setVisibility(View.VISIBLE);
        }else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
