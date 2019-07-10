package com.example.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.stream.Collectors.toMap;

public class QuestionCommentsActivity extends AppCompatActivity
{
    private Toolbar update_q_comment_toolbar;
    private DatabaseReference userRef , AbuEl3orifRefFinal , QuestionRef , calcRef ,CategoryQuestionsCommentsRef , ReactsRef ;
    private FirebaseAuth mAuth ;
    private String CurrentuserId , CommentuserPP , CommentuserUsername , QuestionCommentStringContent , currentQuestionCommentDate , currentQuestionCommentTime ,PostCommentRandomKey ;
    private EditText questionCommentContent;
    private Button Question_comment_submit_btn;
    private ProgressDialog loadingBar;
    private RecyclerView Question_Comments_CategoryList;
    private FirebaseRecyclerAdapter<QuestionComments, QuestionsCommentViewHolder> adapter;
    public Context context;
    private CircleImageView q_Upp;
    Boolean likeChacker = false;
    Boolean dislikeChecker = false;
    private ArrayList<String> AllAreaNamesComments = new ArrayList<String>();
    private ArrayList<String> AllAreaUserIds = new ArrayList<String>();
    private ArrayList<HashMap<String , Integer>> SingleValCommentList = new ArrayList<HashMap<String, Integer>>();


    int likeCounter = 0 , dislikeCounter = 0 ;

    String  SingleAreaName , SingleAreaUserId , UserNameOfAllItsComment , commentsLilkeCount  , commentsDisLilkeCount  ;
    ArrayList<ArrayList<ArrayList<String>>> commentMap2 = new ArrayList<>();


    HashMap<String , Integer> userCalcLikes ;
    HashMap<String , Integer> userCalcLikesTop3;

    HashMap<String , HashMap< String , Integer>> userCalcArea = new  HashMap<String , HashMap< String , Integer>>();
    HashMap<String , HashMap< String , Integer>> userCalcAreaTop3 = new  HashMap<String , HashMap< String , Integer>>();



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_comments);

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_question);

        update_q_comment_toolbar = findViewById(R.id.update_question_comment_toolbar);
        Question_Comments_CategoryList = findViewById(R.id.all_users_category_questions_comments);
        Question_Comments_CategoryList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        Question_Comments_CategoryList.setLayoutManager(linearLayoutManager);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        QuestionRef =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("Questions");
        calcRef =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("CalcAbuEl3orif");

        AbuEl3orifRefFinal = FirebaseDatabase.getInstance().getReference().child("finalAbuEl3orifs");
        ReactsRef = FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("QuestionReacts");
        mAuth = FirebaseAuth.getInstance();
        CurrentuserId = mAuth.getCurrentUser().getUid();

        questionCommentContent = findViewById(R.id.add_question_comment_content);
        Question_comment_submit_btn = findViewById(R.id.add_question_comment_btn);
        loadingBar = new ProgressDialog(this);





//        //---------------------INTENT DATA-------------------------
//        Bundle bundle=getIntent().getExtras();
//        final String selectedArea = bundle.getString("SELAREA");
//        final String selectedCategory = bundle.getString("SELCATEGORY");

        //---------------------INTENT DATA-------------------------
        Bundle bundle=getIntent().getExtras();
        final String selectedArea = bundle.getString("SelectedArea");
        final String selectedCategory = bundle.getString("SelectedCategory");
        final String QuestionKey = bundle.getString("QuestionKey");



        //---------------------------------------------------------
        CategoryQuestionsCommentsRef = FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("Questions").child(selectedArea).child(selectedCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments");


        //----------get all area names list--------------------//


        calcRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                AllAreaNamesComments.add(dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

//        calcRef.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                for(DataSnapshot areaName : dataSnapshot.getChildren())
//                {
//                    SingleAreaName = areaName.getKey();
//                }
//
//                AllAreaNamesComments.add(SingleAreaName);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//
//            }
//        });
        //------------------------------------------------------

        //----------get all user ids list--------------------//



//            calcRef.addChildEventListener(new ChildEventListener()
//            {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//                {
//                    AllAreaUserIds.add(dataSnapshot.getKey());
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//                {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
//                {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//                {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError)
//                {
//
//                }
//            });
//            calcRef.child(AllAreaNamesComments.get(i)).child("AllAreaComments").addValueEventListener(new ValueEventListener()
//            {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//                {
//                    for(DataSnapshot singleUserId : dataSnapshot.getChildren())
//                    {
//                        SingleAreaUserId = singleUserId.getKey().toString();
//                        AllAreaUserIds.add(SingleAreaUserId);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError)
//                {
//
//                }
//           });



//--------------------------------------------------------------------------------


        Question_comment_submit_btn.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
                //SaveQuestionMethod();

                Calendar calendarForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                currentQuestionCommentDate = currentDate.format(calendarForDate.getTime());

                Calendar calendarForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                currentQuestionCommentTime = currentTime.format(calendarForTime.getTime());

                QuestionCommentStringContent =questionCommentContent.getText().toString();

                PostCommentRandomKey = currentQuestionCommentDate + currentQuestionCommentTime ;

                if(TextUtils.isEmpty(QuestionCommentStringContent))
                {
                    Toast.makeText(QuestionCommentsActivity.this, "comment Content is Empty..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HashMap QuestionMap = new HashMap();
                    QuestionMap.put("questionCommentUserId" , CurrentuserId);
                    QuestionMap.put("questionCommentUserName" , CommentuserUsername);
                    QuestionMap.put("questionCommentUserPP" , CommentuserPP);
                    QuestionMap.put("questionCommentDate" , currentQuestionCommentDate);
                    QuestionMap.put("questionCommentTime" , currentQuestionCommentTime);
                    QuestionMap.put("questionCommentContent" , QuestionCommentStringContent);
                    QuestionMap.put("questionCommentLikeCount" , likeCounter);
                    QuestionMap.put("questionCommentDislikeCount" , dislikeCounter);

                    HashMap CalcMap = new HashMap();
                    CalcMap.put("questionCommentUserId" , CurrentuserId);
                    CalcMap.put("questionCommentUserName" , CommentuserUsername);
                    CalcMap.put("questionCommentContent" , QuestionCommentStringContent);
                    CalcMap.put("questionCommentLikeCount" , likeCounter);
                    CalcMap.put("questionCommentDislikeCount" , dislikeCounter);


                    loadingBar.setTitle("Add Comment...");
                    loadingBar.setMessage("please wait until your comment saving complete...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    QuestionRef.child(selectedArea).child(selectedCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(CurrentuserId+"@"+PostCommentRandomKey).updateChildren(QuestionMap).addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                questionCommentContent.setText("");
                                //SendUserToQuestionsActivity();

                                Toast.makeText(QuestionCommentsActivity.this, "Comment is stored Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(QuestionCommentsActivity.this, "Error occuring during saving Comment", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });

                    FirebaseDatabase database = FirebaseDatabase.getInstance();



                    // AllAreaKey = CurrentuserId + "@" + currentQuestionCommentTime;

                    //.child("AllAreaComments").child(CurrentuserId)


                    calcRef.child(selectedArea).child(CurrentuserId+"@"+PostCommentRandomKey).updateChildren(CalcMap).addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {

                        }
                    });


                }



                //----------calling calc AbuEl3orif Method----------------------//


                //----------get all user ids list--------------------//



                calcRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        for(int i =0 ; i < AllAreaNamesComments.size() ; i++)
                        {
                            ArrayList<ArrayList<String>> SingleValCommentList1 = new ArrayList<ArrayList<String>>();

                            for(DataSnapshot usecom : dataSnapshot.child(AllAreaNamesComments.get(i)).getChildren())
                            {
                                ArrayList<String> commentMap = new ArrayList<>();
                                commentsLilkeCount = usecom.child("questionCommentLikeCount").getValue(Integer.class).toString();
                                UserNameOfAllItsComment = usecom.child("questionCommentContent").getValue(String.class).toString();
                                commentsDisLilkeCount = usecom.child("questionCommentDislikeCount").getValue(Integer.class).toString();
                                SingleAreaUserId = usecom.child("questionCommentUserId").getValue(String.class).toString();
                                SingleAreaName = AllAreaNamesComments.get(i);
                                commentMap.add(SingleAreaName);
                                commentMap.add(SingleAreaUserId);
                                commentMap.add(UserNameOfAllItsComment);
                                commentMap.add(commentsLilkeCount);


                                SingleValCommentList1.add(commentMap);

                            }
                            commentMap2.add(SingleValCommentList1);

                            System.out.println(commentMap2);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });



                for(int i =0 ; i < commentMap2.size() ; i++)
                {
                    int  m=0,k=1;
                    int userCommentLikes = 0 ;
                    ArrayList<Integer> Arr = new ArrayList<>();
                    ArrayList<Integer> ArrSort = new ArrayList<>();

                    userCalcLikes = new HashMap<String , Integer>();
                    for(int j =0 ; j < commentMap2.get(i).size() ; j++)
                    {
                        String id = commentMap2.get(i).get(m).get(k);
                        String AreaName = commentMap2.get(i).get(m).get(0);


                        String userCommentId = id;
                        if(commentMap2.get(i).get(j).contains(userCommentId) && commentMap2.get(i).get(j).contains(AreaName))
                        {

                            userCommentLikes = Integer.parseInt( commentMap2.get(i).get(j).get(3));
                        }

                        if(userCalcLikes.containsKey(userCommentId))
                        {

                            userCommentLikes+= userCalcLikes.get(userCommentId);
                            userCalcLikes.put(userCommentId , userCommentLikes);
                            userCalcArea.put(AreaName , sortByValue(userCalcLikes));

                        }
                        else
                        {
                            userCalcLikes.put(userCommentId , userCommentLikes);
                            userCalcArea.put(AreaName , sortByValue(userCalcLikes));
                            userCommentLikes = 0;
                        }
                        m++;
                    }
                }


                for (Map.Entry<String, HashMap<String, Integer>> entry : userCalcArea.entrySet())
                {
                    userCalcLikesTop3 = new HashMap<>();
                    HashMap<String, Integer> childMap = entry.getValue();
                    String AreaName = entry.getKey();


                    for (Map.Entry<String, Integer> entry2 : childMap.entrySet())
                    {
                        final String childKey = entry2.getKey();
                        int childValue = entry2.getValue();
                        userCalcLikesTop3.put(childKey , childValue);
                        userCalcAreaTop3.put(AreaName , userCalcLikesTop3);
                        if(userCalcLikesTop3.size() == 1)
                        {

                            HashMap AbuMap = new HashMap();
                            AbuMap.put("GoldenID" , childKey);

                            AbuEl3orifRefFinal.child(AreaName).updateChildren(AbuMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });



                        }
                        if(userCalcLikesTop3.size() == 2)
                        {

                            HashMap AbuMap = new HashMap();
                            AbuMap.put("SilverID" , childKey);

                            AbuEl3orifRefFinal.child(AreaName).updateChildren(AbuMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });


                        }
                        if(userCalcLikesTop3.size() == 3)
                        {
                            HashMap AbuMap = new HashMap();
                            AbuMap.put("BronzeID" , childKey);
                            AbuEl3orifRefFinal.child(AreaName).updateChildren(AbuMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });

                            break;
                        }
                    }
                }

                //--------------------------------------------------------------------------------------------------------



//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//
//
//
//            }
//        }, 30000);
//        //--------------------------------------------------------------







            }


//--------------------------------------------------------------------------------






        });

        //------------------------------------------calc abuEl3orif Method--------------------------------------------







        setSupportActionBar(update_q_comment_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Comment");


        //====ref to users node======//
        userRef.child(CurrentuserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    CommentuserPP = dataSnapshot.child("ImageUrl").getValue().toString();
                    CommentuserUsername = dataSnapshot.child("username").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        //==================================QUESTION LIST APPEAR HERE========================================//
//        FirebaseRecyclerAdapter<Questions , QuestionsViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Questions, QuestionsViewHolder>
//                        (
//                                Questions.class,
//                                R.layout.all_questions_layout,
//                                QuestionsViewHolder.class,
//                                CategoryQuestions
//                        )
//                {
//                    @Override
//                    protected void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position, @NonNull Questions model)
//                    {
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//                    {
//                        return null;
//                    }
//                };
        //=====================Dispaly All Users here====================================//

        //        FirebaseRecyclerOptions<Questions> options =
//                new FirebaseRecyclerOptions.Builder<Questions>()
//                        .setQuery(CategoryQuestionsRef, Questions.class)
//                        .build();

        //DisplayAllUserQuestions();

        FirebaseRecyclerOptions<QuestionComments> options = new FirebaseRecyclerOptions.Builder<QuestionComments>()
                .setQuery(CategoryQuestionsCommentsRef.orderByChild("questionCommentLikeCount"), new SnapshotParser<QuestionComments>()
                {
                    @NonNull
                    @Override
                    public QuestionComments parseSnapshot(@NonNull DataSnapshot snapshot)
                    {
                        QuestionComments questionComments = new QuestionComments(snapshot.child("questionCommentUserName").getValue().toString() ,snapshot.child("questionCommentTime").getValue().toString()  , snapshot.child("questionCommentDate").getValue().toString() ,snapshot.child("questionCommentContent").getValue().toString() ,  snapshot.child("questionCommentUserPP").getValue().toString());
                        return questionComments;

                    }
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<QuestionComments, QuestionsCommentViewHolder>(options)
        {


            @Override
            protected void onBindViewHolder(@NonNull QuestionsCommentViewHolder holder, int position, @NonNull QuestionComments model)
            {
                final String QuestionCommentKey = getRef(position).getKey();

                holder.setQuestion_comment_uFullName(model.question_comment_uFullName);
                holder.setQuestion_comment_time(model.question_comment_time);
                holder.setQuestion_comment_date(model.question_comment_date);
                holder.setQuestion_comments_description(model.question_comments_description);

                String imageUri = model.question_comment_uPP;
                holder.setQuestion_comment_uPP(imageUri);

                //======Reacts Status==================//
                holder.setQuestionReactsStatus(QuestionKey,QuestionCommentKey , selectedArea , selectedCategory);

                //================when click the quetion=================//
                holder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent ClickQuestionCommentIntent = new Intent(QuestionCommentsActivity.this , EditQuestionCommentActivity.class);
                        ClickQuestionCommentIntent.putExtra("QuestionCommentKey" , QuestionCommentKey);
                        ClickQuestionCommentIntent.putExtra("SelectedArea" , selectedArea);
                        ClickQuestionCommentIntent.putExtra("SelectedCategory", selectedCategory);
                        ClickQuestionCommentIntent.putExtra("QuestionKey", QuestionKey);


                        startActivity(ClickQuestionCommentIntent);

                    }
                });

                //====like btn=================
                holder.like_Question_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        likeChacker = true;

                        ReactsRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(likeChacker.equals(true))
                                {
                                    if(!dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && !dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                                    {
                                        ReactsRef.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).setValue(true);
                                        // like_Question_btn.setColorFilter(getResources().getColor(R.color.colorLike));
                                        likeChacker = false;

                                    }

                                    else if(dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && !dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                                    {
                                        ReactsRef.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).removeValue();
                                        likeChacker = false;

                                    }

                                    else if(!dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                                    {
                                        ReactsRef.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).removeValue();
                                        ReactsRef.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).setValue(true);

                                        likeChacker = false;
                                        dislikeChecker = false ;

                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });

                    }
                });

                //====Dislike btn=================
                holder.dislike_Question_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dislikeChecker = true;

                        ReactsRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dislikeChecker.equals(true))
                                {
                                    if(!dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && !dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                                    {
                                        ReactsRef.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).setValue(true);
                                        dislikeChecker = false;

                                    }

                                    else if(!dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                                    {
                                        ReactsRef.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).removeValue();
                                        dislikeChecker = false;

                                    }

                                    else if(dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && !dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                                    {
                                        ReactsRef.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).setValue(true);
                                        ReactsRef.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).child(CurrentuserId).removeValue();

                                        likeChacker = false;
                                        dislikeChecker = false ;

                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });

                    }
                });

            }

            @NonNull
            @Override
            public QuestionCommentsActivity.QuestionsCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_question_comments_layout, parent, false);
                context = parent.getContext();

                return new QuestionCommentsActivity.QuestionsCommentViewHolder(view);
            }



        };


        Question_Comments_CategoryList.setAdapter(adapter);

        //===================================================================================================
        //================================================================================

        CalcAbuEl2orifMethod();
    }


    public void calcAbuEl3orifMethod()
    {

    }

    public static HashMap<String, Integer> sortByValue(HashMap <String , Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void CalcAbuEl2orifMethod()
    {

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        adapter.startListening();

    }

    @Override
    protected void onStop()
    {
        super.onStop();

        adapter.stopListening();
    }

    public class QuestionsCommentViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        ImageButton like_Question_btn , dislike_Question_btn , comments_Question_btn;
        TextView likecount , dislikeCount , commentsCount;
        String current_uid;
        DatabaseReference ReactsRef2 ,QuestionRef2 ,calcRef2 ;

        public QuestionsCommentViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;

            like_Question_btn = (ImageButton) mView.findViewById(R.id.like_icon_btn);
            dislike_Question_btn =(ImageButton) mView.findViewById(R.id.dislike_icon_btn);
            comments_Question_btn = (ImageButton) mView.findViewById(R.id.comments_icon_btn);
            likecount = (TextView) mView.findViewById(R.id.likes_count);
            dislikeCount = (TextView) mView.findViewById(R.id.dislikes_count);
            commentsCount = (TextView) mView.findViewById(R.id.Question_comments_count);
            calcRef2 =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("CalcAbuEl3orif");
            QuestionRef2 =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("Questions");
            ReactsRef2 = FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("QuestionReacts");
            current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setQuestionReactsStatus(final String QuestionKey , final String QuestionCommentKey , final String SelArea , final String SelCategory)
        {
            ReactsRef2.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(!dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && !dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                    {
                        likeCounter = (int) dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).getChildrenCount();
                        dislikeCounter = (int) dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).getChildrenCount();
                        like_Question_btn.setColorFilter(getResources().getColor(R.color.DefaultColorLike));
                        dislike_Question_btn.setColorFilter(getResources().getColor(R.color.DefaultColorLike));
                        likecount.setText(Integer.toString(likeCounter));
                        dislikeCount.setText(Integer.toString(dislikeCounter));
                        QuestionRef2.child(SelArea).child(SelCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(QuestionCommentKey).child("questionCommentLikeCount").setValue(likeCounter);
                        QuestionRef2.child(SelArea).child(SelCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(QuestionCommentKey).child("questionCommentDislikeCount").setValue(dislikeCounter);

//                        calcRef2.child(SelArea).child("AllAreaComments").child(current_uid).child(AllAreaKey).child("questionCommentLikeCount").setValue(likeCounter);
//                        calcRef2.child(SelArea).child("AllAreaComments").child(current_uid).child(AllAreaKey).child("questionCommentDislikeCount").setValue(dislikeCounter);
                        calcRef2.child(SelArea).child(QuestionCommentKey).child("questionCommentLikeCount").setValue(likeCounter);
                        calcRef2.child(SelArea).child(QuestionCommentKey).child("questionCommentDislikeCount").setValue(dislikeCounter);


                    }

                    else if(!dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                    {
                        likeCounter = (int) dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).getChildrenCount();
                        dislikeCounter = (int) dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).getChildrenCount();
                        like_Question_btn.setColorFilter(getResources().getColor(R.color.DefaultColorLike));
                        dislike_Question_btn.setColorFilter(getResources().getColor(R.color.colorDislike));
                        likecount.setText(Integer.toString(likeCounter));
                        dislikeCount.setText(Integer.toString(dislikeCounter));
                        QuestionRef2.child(SelArea).child(SelCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(QuestionCommentKey).child("questionCommentLikeCount").setValue(likeCounter);
                        QuestionRef2.child(SelArea).child(SelCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(QuestionCommentKey).child("questionCommentDislikeCount").setValue(dislikeCounter);

//                        calcRef2.child(SelArea).child("AllAreaComments").child(current_uid).child(AllAreaKey).child("questionCommentLikeCount").setValue(likeCounter);
//                        calcRef2.child(SelArea).child("AllAreaComments").child(current_uid).child(AllAreaKey).child("questionCommentDislikeCount").setValue(dislikeCounter);
                        calcRef2.child(SelArea).child(QuestionCommentKey).child("questionCommentLikeCount").setValue(likeCounter);
                        calcRef2.child(SelArea).child(QuestionCommentKey).child("questionCommentDislikeCount").setValue(dislikeCounter);


                    }

                    else if(dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId) && !dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).hasChild(CurrentuserId))
                    {
                        likeCounter = (int) dataSnapshot.child("QuestionCommentLikes").child(QuestionKey).child(QuestionCommentKey).getChildrenCount();
                        dislikeCounter = (int) dataSnapshot.child("QuestionCommentDisLikes").child(QuestionKey).child(QuestionCommentKey).child(QuestionCommentKey).getChildrenCount();
                        like_Question_btn.setColorFilter(getResources().getColor(R.color.colorLike));
                        dislike_Question_btn.setColorFilter(getResources().getColor(R.color.DefaultColorLike));
                        likecount.setText(Integer.toString(likeCounter));
                        dislikeCount.setText(Integer.toString(dislikeCounter));
                        QuestionRef2.child(SelArea).child(SelCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(QuestionCommentKey).child("questionCommentLikeCount").setValue(likeCounter);
                        QuestionRef2.child(SelArea).child(SelCategory).child("categoryQuestions").child(QuestionKey).child("QuestionCategoryComments").child(QuestionCommentKey).child("questionCommentDislikeCount").setValue(dislikeCounter);

//                        calcRef2.child(SelArea).child("AllAreaComments").child(current_uid).child(AllAreaKey).child("questionCommentLikeCount").setValue(likeCounter);
//                        calcRef2.child(SelArea).child("AllAreaComments").child(current_uid).child(AllAreaKey).child("questionCommentDislikeCount").setValue(dislikeCounter);
                        calcRef2.child(SelArea).child(QuestionCommentKey).child("questionCommentLikeCount").setValue(likeCounter);
                        calcRef2.child(SelArea).child(QuestionCommentKey).child("questionCommentDislikeCount").setValue(dislikeCounter);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }

        public void setQuestion_comment_uFullName(String question_c_uFullName)
        {
            TextView q_c_UfullName = (TextView) mView.findViewById(R.id.Question_username);
            q_c_UfullName.setText(question_c_uFullName);
        }

        public void setQuestion_comment_time(String question_c_time)
        {
            TextView q_c_Time = (TextView) mView.findViewById(R.id.Question_time);
            q_c_Time.setText("  " + question_c_time);
        }

        public void setQuestion_comment_date(String question_c_date)
        {
            TextView q_c_Date = (TextView) mView.findViewById(R.id.Question_date);
            q_c_Date.setText("  " + question_c_date);
        }

        public void setQuestion_comments_description(String question_c_description)
        {
            TextView q_c_Description = (TextView) mView.findViewById(R.id.Question_description);
            q_c_Description.setText(question_c_description);
        }

        public void setQuestion_comment_uPP(String question_c_uPP)
        {
            q_Upp = mView.findViewById(R.id.Question_user_pp);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_icon);

            //Picasso.with(ctx).load(question_uPP).into(q_Upp);
            // Glide.with(context).applyDefaultRequestOptions(requestOptions).load(question_c_uPP).into(q_Upp);
            Glide.with(getApplicationContext()).load(question_c_uPP).into(q_Upp);


            // Picasso.with(ctx).load(question_uPP).placeholder(R.drawable.profile_icon).into(q_Upp);
        }


    }

    private void SaveQuestionMethod()
    {
//        Calendar calendarForDate = Calendar.getInstance();
//        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
//        currentQuestionDate = currentDate.format(calendarForDate.getTime());
//
//        Calendar calendarForTime = Calendar.getInstance();
//        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
//        currentQuestionTime = currentTime.format(calendarForTime.getTime());
//
//        QuestionStringContent =questionContent.getText().toString();
//
//        PostRandomKey = currentQuestionDate + currentQuestionTime ;
//
//
//
//        if(TextUtils.isEmpty(QuestionStringContent))
//        {
//            Toast.makeText(this, "Quetion Content is Empty..", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            HashMap QuestionMap = new HashMap();
//            QuestionMap.put("questionUserId" , CurrentuserId);
//            QuestionMap.put("questionUserName" , userUsername);
//            QuestionMap.put("questionUserPP" , userPP);
//            QuestionMap.put("questionDate" , currentQuestionDate);
//            QuestionMap.put("questionTime" , currentQuestionTime);
//            QuestionMap.put("questionContent" , QuestionStringContent);
//
//            loadingBar.setTitle("Asking Question...");
//            loadingBar.setMessage("please wait until your question saving complete...");
//            loadingBar.show();
//            loadingBar.setCanceledOnTouchOutside(true);
//
//            QuestionRef.child(selectedArea).child(selectedCategory).child("categoryQuestions").child(CurrentuserId + PostRandomKey).updateChildren(QuestionMap).addOnCompleteListener(new OnCompleteListener()
//            {
//                @Override
//                public void onComplete(@NonNull Task task)
//                {
//                    if(task.isSuccessful())
//                    {
//                        SendUserToQuestionsActivity();
//                        Toast.makeText(QuestionActivity.this, "Question is stored Successfully...", Toast.LENGTH_SHORT).show();
//                        loadingBar.dismiss();
//                    }
//                    else
//                    {
//                        Toast.makeText(QuestionActivity.this, "Error occuring during saving question", Toast.LENGTH_SHORT).show();
//                        loadingBar.dismiss();
//                    }
//
//                }
//            });
//
//
//        }
    }

    private void SendUserToQuestionCommentsActivity()
    {
        Intent QuestionIntent = new Intent(QuestionCommentsActivity.this , QuestionCommentsActivity.class);
        startActivity(QuestionIntent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToEditQuestionActivtity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToEditQuestionActivtity()
    {
        Intent EditQuetion_intent = new Intent(QuestionCommentsActivity.this , EditQuestionActivity.class);
        startActivity(EditQuetion_intent);

    }
}


