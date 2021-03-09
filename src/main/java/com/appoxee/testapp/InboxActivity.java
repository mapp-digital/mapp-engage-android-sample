package com.appoxee.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appoxee.Appoxee;
import com.appoxee.internal.inapp.APXInboxWebViewClient;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Varun on 4/3/2018.
 */

public class InboxActivity extends Activity {

    RecyclerView recyclerView;
    InboxAdapter mAdapter;
    List<APXInboxMessage> inboxList;
    public static final String ABOUT_BLANK = "about:blank";
    AlertDialog modalDialog;

    private final String APX_LAUNCH_INBOX_ACTION = "com.appoxee.VIEW_INBOX";
    TextView tv;
    Uri uri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inbox_list_layout);

        recyclerView =  findViewById(R.id.recycler_view);
        tv = findViewById(R.id.textView);
        tv.setVisibility(View.GONE);

        Bundle bundle;
        Uri uri = null;


        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (APX_LAUNCH_INBOX_ACTION.equals(getIntent().getAction())) {
                uri = getIntent().getData();
                //
                getMessageId(uri);
            }
            /*if(uri != null) {
                String foo = uri.getQueryParameter("foo");
            }*/
            if (bundle != null) {
                inboxList = (List<APXInboxMessage>) bundle.getSerializable("inboxMessages");
            }

            InAppInboxCallback inAppInboxCallback = new InAppInboxCallback();
            inAppInboxCallback.addInAppInboxMessagesReceivedCallback(new InAppInboxCallback.onInAppInboxMessagesReceived() {
                @Override
                public void onInAppInboxMessages(List<APXInboxMessage> richMessages) {
                    Log.d("messages","messages = " +richMessages.get(0).getContent());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("inboxMessages", (ArrayList<APXInboxMessage>)richMessages);
                    Intent intent = new Intent(InboxActivity.this, InboxActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }

                @Override
                public void onInAppInboxMessage(final APXInboxMessage message) {
                    Log.d("messages","messages = " +message.getContent());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialogForInboxMessageContent(message, message.getContent());
                        }
                    });
                }
            });

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    APXInboxMessage inboxMessage = inboxList.get(position);
                    String status = inboxMessage.getStatus();
                    showDialogForInboxMessageContent(inboxMessage, inboxMessage.getContent());
                    inboxMessage.markAsRead(InboxActivity.this);
//                    inboxMessage.markAsDeleted(InboxActivity.this);
//                    inboxMessage.markAsUnRead(InboxActivity.this);
                }

                @Override
                public void onLongClick(View view, int position) {
                    APXInboxMessage inboxMessage = inboxList.get(position);
                    String status = inboxMessage.getStatus();
//                    inboxMessage.markAsUnRead(InboxActivity.this);
                    Log.d("Aleksandra",status);
                }
            }));


            if (inboxList != null && !inboxList.isEmpty()) {
                mAdapter = new InboxAdapter(inboxList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
                recyclerView.setAdapter(mAdapter);
                tv.setVisibility(View.GONE);
            } else {
                tv.setVisibility(View.VISIBLE);
                if(uri!=null && uri.toString() != null) {
                    tv.setText("MessageId = " + uri.toString());
                } else {
                    tv.setText("MessageId was null");
                }
            }
        }

        InAppCallback inAppCallback =  new InAppCallback();
        inAppCallback.addInAppMessageReceivedCallback(new InAppCallback.onInAppEventReceived() {
            @Override
            public void onInAppEvent(String eventName, String eventValue) {
                Log.d("Inbox eventName = ", eventName);
                Log.d("Inbox eventValue = ", eventValue);
                Toast.makeText(InboxActivity.this, "KEY = " +eventName + "VALUE = " + eventValue, Toast.LENGTH_LONG).show();

                if(modalDialog!=null && modalDialog.isShowing()) {
                    modalDialog.dismiss();
                }
            }
        });
    }

    private void getMessageId(Uri uri) {

        String protocol = uri.getScheme();
        String server = uri.getAuthority();
        String path = uri.getPath();
        String query = uri.getQuery();
        String messageId = uri.getQueryParameter("message_id");

        Log.d("InboxActivity","protocol = " +protocol);
        Log.d("InboxActivity","server = " +server);
        Log.d("InboxActivity","path = " +path);
        Log.d("InboxActivity","query = " +query);
        Log.d("InboxActivity","messageId = " +messageId);
        if(uri != null && uri.toString() != null) {
            tv.setText("\n MessageId = " + messageId );
        } else {
            tv.setText("DEEPLINK ACTIVITY URI is Null" );
        }

        Appoxee.instance().fetchInboxMessage(this, Integer.parseInt(messageId));

    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void showDialogForInboxMessageContent(APXInboxMessage richMessageObject, String htmlContent) {
        if(modalDialog != null && modalDialog.isShowing()) {
            modalDialog.dismiss();
        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, com.appoxee.sdk.R.style.ModalDialogTheme);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.pop_up, null);

        ImageView inAppImage = (ImageView) dialogView.findViewById(R.id.inAppImage);
        ImageView dismissDialogImageIcon = (ImageView) dialogView.findViewById(R.id.inAppCloseDialog);
        TextView inAppTitle = (TextView) dialogView.findViewById(R.id.inAppTitle);
        inAppTitle.setText(richMessageObject.getSubject());
        TextView inAppContent = (TextView) dialogView.findViewById(R.id.inAppContent);
        inAppContent.setText(richMessageObject.getSummary());
        Glide.with(this)
                .asDrawable()
                .load(richMessageObject.getIconUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(inAppImage);
        dismissDialogImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modalDialog != null) {
                    modalDialog.dismiss();
                }
            }
        });
        dialogBuilder.setView(dialogView);
        modalDialog = dialogBuilder.create();
        modalDialog.setCancelable(true);
        modalDialog.requestWindowFeature(modalDialog.getWindow().FEATURE_NO_TITLE);
        if(modalDialog!= null && modalDialog.isShowing()) {
            modalDialog.dismiss();
        }
        modalDialog.show();


    }


    public class MyDividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;
        private int mOrientation;
        private Context context;
        private int margin;

        public MyDividerItemDecoration(Context context, int orientation, int margin) {
            this.context = context;
            this.margin = margin;
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left + dpToPx(margin), top, right - dpToPx(margin), bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top + dpToPx(margin), right, bottom - dpToPx(margin));
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }

        private int dpToPx(int dp) {
            Resources r = context.getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }
    }

}

