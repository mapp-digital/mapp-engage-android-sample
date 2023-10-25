package com.appoxee.testapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appoxee.Appoxee;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Varun on 4/3/2018.
 */

@SuppressWarnings({"FieldCanBeLocal", "unchecked"})
public class InboxActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InboxAdapter mAdapter;
    List<APXInboxMessage> inboxList;
    public static final String ABOUT_BLANK = "about:blank";
    AlertDialog modalDialog;

    private final String APX_LAUNCH_INBOX_ACTION = "com.appoxee.VIEW_INBOX";
    TextView tv;
    Uri uri;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inbox_list_layout);

        recyclerView = findViewById(R.id.recycler_view);
        tv = findViewById(R.id.textView);

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
                //noinspection unchecked
                inboxList = (List<APXInboxMessage>) bundle.getSerializable("inboxMessages");
            }

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    APXInboxMessage inboxMessage = inboxList.get(position);
                    showDialogForInboxMessageContent(inboxMessage, inboxMessage.getContent());
                    inboxMessage.markAsRead(InboxActivity.this);
                    mAdapter.notifyItemChanged(position);
                }

                @Override
                public void onLongClick(View view, int position) {
                    APXInboxMessage inboxMessage = inboxList.get(position);
                    longClickPopupMenu(inboxMessage, position);
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
                if (uri != null) {
                    tv.setText("MessageId = " + uri);
                } else {
                    tv.setText("MessageId was null");
                }
            }
        }
    }

    private void longClickPopupMenu(APXInboxMessage message, int itemPosition) {
        CharSequence[] menuItems = new String[3];
        menuItems[0] = "Read";
        menuItems[1] = "Unread";
        menuItems[2] = "Delete";

        AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
        AlertDialog dialog = builder.setTitle("Menu")
                .setItems(menuItems, (dg, menuPosition) -> {
                    switch (menuPosition) {
                        case 0:
                            message.markAsRead(InboxActivity.this);
                            break;
                        case 1:
                            message.markAsUnRead(InboxActivity.this);
                            break;
                        default:
                            message.markAsDeleted(InboxActivity.this);
                            break;
                    }
                    mAdapter.notifyItemChanged(itemPosition);
                })
                .create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void getMessageId(Uri uri) {
        if (uri != null) {
            String protocol = uri.getScheme();
            String server = uri.getAuthority();
            String path = uri.getPath();
            String query = uri.getQuery();
            String messageId = uri.getQueryParameter("message_id");

            Log.d("InboxActivity", "protocol = " + protocol);
            Log.d("InboxActivity", "server = " + server);
            Log.d("InboxActivity", "path = " + path);
            Log.d("InboxActivity", "query = " + query);
            Log.d("InboxActivity", "messageId = " + messageId);
            String formattedText="\n MessageId = " + messageId;
            tv.setText(formattedText);
            int message_id=Integer.parseInt(messageId);
            Appoxee.instance().fetchInboxMessage(message_id);
        } else {
            tv.setText("DEEPLINK ACTIVITY URI is Null");
        }
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetector gestureDetector;
        private final ClickListener clickListener;

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
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
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
        if (modalDialog != null && modalDialog.isShowing()) {
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
        Button btnWebPage = dialogView.findViewById(R.id.btnWebpage);
        Button btnPlayStore = dialogView.findViewById(R.id.btnPlayStore);
        Button btnDeepLink = dialogView.findViewById(R.id.btnDeepLink);
        inAppContent.setText(richMessageObject.getSummary());

        Glide.with(this)
                .asDrawable()
                .load(richMessageObject.getIconUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(inAppImage);

        btnWebPage.setOnClickListener(v -> {
            richMessageObject.handleAction("apxAction://landingPage?openInApp=0&link=https://www.google.com", InboxActivity.this);
            modalDialog.dismiss();
        });

        btnPlayStore.setOnClickListener(v -> {
            richMessageObject.handleAction("apxAction://appStore?openInApp=0&link=com.kiloo.subwaysurf&hl=en&gl=US", InboxActivity.this);
            modalDialog.dismiss();
        });

        btnDeepLink.setOnClickListener(v -> {
            modalDialog.dismiss();
            richMessageObject.handleAction("apxAction://deeplink?link=https://www.test.com?a=b", InboxActivity.this);
        });

        dismissDialogImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modalDialog != null) {
                    modalDialog.dismiss();
                }
            }
        });

        if (modalDialog != null && modalDialog.isShowing()) {
            modalDialog.dismiss();
        }

        dialogBuilder.setView(dialogView);
        modalDialog = dialogBuilder.create();
        modalDialog.setCancelable(true);
        modalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modalDialog.show();
    }


    public static class MyDividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private final Drawable mDivider;
        private int mOrientation;
        private final Context context;
        private final int margin;

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

