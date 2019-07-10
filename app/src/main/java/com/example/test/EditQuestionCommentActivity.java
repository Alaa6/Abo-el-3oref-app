package com.example.test;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EditQuestionCommentActivity extends AppCompatActivity
{
    private Button btnSavedComment ,btnEditComment ,btnDeleteComment ;
    private DatabaseReference UserRef ,commentRef;
    private FirebaseAuth mAuth;
    private TextView commentQ;
    private  String currentUserId, commentContnet  ,selectedArea,selectedCategory,questionId;
    private ArrayList<String> savedCommentsList ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_comment);

        /*_________________________intent data_______________________*/
        Bundle bundle=getIntent().getExtras();
        final String commentId =bundle.getString("QuestionCommentKey");
        selectedArea =bundle.getString("SelectedArea");
        selectedCategory =bundle.getString("SelectedCategory");
        questionId =bundle.getString("QuestionKey");
        /*___________________________________________________________________*/
        commentQ = findViewById(R.id.questionComment_description);

        commentRef=FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("Questions").child(selectedArea).child(selectedCategory).child("categoryQuestions").child(questionId).child("QuestionCategoryComments").child(commentId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    commentContnet = dataSnapshot.child("questionCommentContent").getValue().toString();
                    commentQ.setText(commentContnet);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UserRef= FirebaseDatabase.getInstance().getReference().child("users");
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        /*________________________________________Save Comment ___________________________________*/

        savedCommentsList =new ArrayList<String>();

        btnSavedComment=findViewById(R.id.Button_save_comment);
        btnSavedComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!savedCommentsList.contains(commentId))
                {
                    commentRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String content =dataSnapshot.child("questionCommentContent").getValue().toString();
                            String date =dataSnapshot.child("questionCommentDate").getValue().toString();
                            // String disLikes =dataSnapshot.child("questionCommentDislikeCount").getValue().toString();
                            //String likes =dataSnapshot.child("questionCommentLikeCount").getValue().toString();
                            String time =dataSnapshot.child("questionCommentTime").getValue().toString();
                            // String userId =dataSnapshot.child("questionCommentUserId").getValue().toString();
                            String userName =dataSnapshot.child("questionCommentUserName").getValue().toString();
                            String pp =dataSnapshot.child("questionCommentUserPP").getValue().toString();



                            UserRef.child(currentUserId).child("SavedQuestionComment").child(commentId).child("questionCommentContent").setValue(content);
                            UserRef.child(currentUserId).child("SavedQuestionComment").child(commentId).child("questionCommentDate").setValue(date);
                            //UserRef.child(currentUserId).child("SavedQuestionComment").child("questionCommentDislikeCount").setValue(disLikes);
                            //UserRef.child(currentUserId).child("SavedQuestionComment").child("questionCommentLikeCount").setValue(likes);
                            UserRef.child(currentUserId).child("SavedQuestionComment").child(commentId).child("questionCommentTime").setValue(time);
                            //UserRef.child(currentUserId).child("SavedQuestionComment").child("questionCommentUserId").setValue(userId);
                            UserRef.child(currentUserId).child("SavedQuestionComment").child(commentId).child("questionCommentUserName").setValue(userName);
                            UserRef.child(currentUserId).child("SavedQuestionComment").child(commentId).child("questionCommentUserPP").setValue(pp);





                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    savedCommentsList.add(commentId);
                    Toast.makeText(EditQuestionCommentActivity.this, "New comment is saved ...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditQuestionCommentActivity.this, "this comment is already saved ..", Toast.LENGTH_SHORT).show();
                }
                sendUsertoQuestionCommentActicity();
            }
        });

        /*_______________________________________________________________________________________________________*/


        /*_____________________________    Edit and Delete Questions Comment    __________________________*/
//Button_delete_comment
        btnEditComment = findViewById(R.id.Button_edit_comment);
        btnDeleteComment = findViewById(R.id.Button_delete_comment);
        if(commentId.contains(currentUserId)){
            btnEditComment.setVisibility(View.VISIBLE);
            btnDeleteComment.setVisibility(View.VISIBLE);

            btnEditComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditCurrentQuestionCommentDialog(commentContnet);
                }
            });
            btnDeleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentRef.removeValue();
                    sendUsertoQuestionCommentsActicity();
                }
            });
        }
        else {
            btnEditComment.setVisibility(View.INVISIBLE);
            btnDeleteComment.setVisibility(View.INVISIBLE);
        }

        /*_______________________________________________________________________________________________________*/


    }

    private void EditCurrentQuestionCommentDialog(String questionDescriptionStr)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditQuestionCommentActivity.this);
        builder.setTitle("Edit Comment");

        final EditText inputFeild = new EditText(EditQuestionCommentActivity.this);
        inputFeild.setText(questionDescriptionStr);
        builder.setView(inputFeild);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                commentRef.child("questionCommentContent").setValue(inputFeild.getText().toString());
                Toast.makeText(EditQuestionCommentActivity.this, "Comment has been Updated", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();

            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.gradientbackground);

    }
    private void sendUsertoQuestionCommentActicity() {
        Intent questionCommentActicity=new Intent(getApplicationContext() ,SavedCommentActivity.class);
        startActivity(questionCommentActicity);
    }
    private void sendUsertoQuestionCommentsActicity() {
        Toast.makeText(getApplicationContext(),"comment deleted",Toast.LENGTH_LONG).show();
        Intent questionCommentsActicity=new Intent(getApplicationContext() ,QuestionCommentsActivity.class);
        questionCommentsActicity.putExtra("SelectedArea", selectedArea);
        questionCommentsActicity.putExtra("SelectedCategory", selectedCategory);
        questionCommentsActicity.putExtra("QuestionKey", questionId);
        startActivity(questionCommentsActicity);
        finish();
    }
}
