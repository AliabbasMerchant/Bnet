package com.betanet.betanet;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;
    Button registerButton;
    TextView signInFromRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.registerButton);
        signInFromRegisterButton = findViewById(R.id.signInFromRegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // checking credentials
                if (!editTextEmail.getText().toString().equals("") && !editTextName.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {

                    RequestQueue queue = Volley.newRequestQueue(Register.this);
                    StringRequest sr = new StringRequest(Request.Method.POST,"http://merchantaliabbas.pythonanywhere.com/register", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "onResponse: " + response);
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                if(jsonObj.getInt("success") == 1) {
                                    Toast.makeText(Register.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, QuickBio.class);

                                    intent.putExtra("user_name", editTextName.getText().toString());
                                    intent.putExtra("email", editTextEmail.getText().toString());

                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(Register.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onErrorResponse: ", error);
                        }
                    }){
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("user_name", editTextName.getText().toString());
                            params.put("email_id", editTextEmail.getText().toString());
                            Log.e(TAG, "getParams: Password " + editTextPassword.getText().toString() );
                            String hash = null;
                            try {
                                hash = getHash(editTextPassword.getText().toString());
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            Log.e(TAG, "getParams: hash " + hash);
                            params.put("password_hash", hash);
                            return params;
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("Content-Type","application/x-www-form-urlencoded");
                            return params;
                        }
                    };
                    queue.add(sr);
                }
                else {
                    Toast.makeText(Register.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

        signInFromRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String getHash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        return String.format("%064x", new BigInteger(1, digest));
    }
//    public static String generateStrongPasswordHashWithSHA256(String password) {
//        try {
//            char[] chars = password.toCharArray();
//            byte[] salt = getSalt();
//
//            PBEKeySpec spec = new PBEKeySpec(chars, salt, 1010101, 20 * Byte.SIZE);
//            SecretKeyFactory skf = SecretKeyFactory
//                    .getInstance("PBKDF2WithHmacSHA1");
//            byte[] hash = skf.generateSecret(spec).getEncoded();
//
//            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//            hash = sha256.digest();
//
//            return toHex(salt) + ":" + toHex(hash);
//        } catch (Exception e) {
//            System.out.println("Exception: Error in generating password"
//                    + e.toString());
//        }
//        return "";
//    }
}