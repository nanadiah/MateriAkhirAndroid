package com.example.asus.authandmessaging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
GoogleApiClient.OnConnectionFailedListener{
    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @BindView(R.id.btn_sign_out)
    Button btnSignOut;
    @BindView(R.id.prof_section)
    LinearLayout profSection;
    @BindView(R.id.btn_login)
    SignInButton btnLogin;
    private static final int REQ_CODE = 3;


    private GoogleApiClient googleApiClient;

    private FirebaseAuth mAuth;

    private FirebashAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        profSection.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){

            }
        };

        GoogleSignInOptions signInOptions = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }
    @OnClick(R.id.btn_sign_out)
    public void onBtnSignOutClicked(){
        signOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connnectionResult){

    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultItCallback<Status>(){
            @Override
            public void onResult(@NonNull Status statUs){
                updateUI(false);
            }
        });
    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(gooogleApiClient);
        startActivityForResult(intents, REQ_CODE);
    }

    private void handleResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                txtName.setText(firebaseUser.getDisplayName());
                                txtEmail.setText(firebaseUser.getEmail());

                                Glide.with(MainActivity.this).load(firebaseUser.getPhotoUrl().toString().into9imgProfile);
                                updateUI(true);
                            } else
                            {
                                updateUI(false);
                            }

                        }
                    });
        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(boolean isLogin){
        if(isLogin){
            profSection.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        } else {
            profSection.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE){
            GoogleSiginInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);

            @OnClick(R.id.btn_login)
                    public void onViewClicked(){
                signIn();
            }
        }
    }
}

