package com.example;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyActivity extends Activity {
    private static final String TAG = "MyActivity";

    private EditText editText;
    private Drawable iconDrawable;
    private TextView rawTextView;
    private TextView emotifiedTextView;
    private ImageButton emoticonButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        iconDrawable = getResources().getDrawable(R.drawable.ic_launcher);
        iconDrawable.setBounds(0, 0, iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight());

        editText = (EditText) findViewById(R.id.edit_text);
        rawTextView = (TextView) findViewById(R.id.raw_textview);
        emotifiedTextView = (TextView) findViewById(R.id.emotified_textview);
        emoticonButton = (ImageButton) findViewById(R.id.insert_button);

        emoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();

                String textToInsert = "[icon]";
                editText.getText().replace(Math.min(selectionStart, selectionEnd), Math.max(selectionStart, selectionEnd),
                        textToInsert, 0, textToInsert.length());
            }
        });

        editText.setText("Tap the image button to insert an 'emoticon'");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emotifySpannable(editable);

                // Sanity check. Convert to raw string, then re-emotify in TextView
                String rawContent = editable.toString();
                rawTextView.setText(rawContent);

                SpannableString spannable = new SpannableString(rawContent);
                emotifySpannable(spannable);
                emotifiedTextView.setText(spannable);
            }
        });
    }

    /**
     * Work through the contents of the string, and replace any occurrences of [icon] with the imageSpan
     * @param spannable
     */
    private void emotifySpannable(Spannable spannable) {

        int length = spannable.length();
        int position = 0;
        int tagStartPosition = 0;
        StringBuilder buffer = new StringBuilder();
        boolean inTag = false;

        do {

            String c = spannable.subSequence(position, position + 1).toString();

            if (!inTag && c.equals("[")) {
                buffer = new StringBuilder();
                tagStartPosition = position;
                Log.d(TAG, "   Entering tag at " + tagStartPosition);

                inTag = true;
            }

            if (inTag) {
                buffer.append(c);

                // Have we reached end of the tag?
                if (c.equals("]")) {
                    inTag = false;
                    String tag = buffer.toString();

                    Log.d(TAG, "Tag: " + tag + ", started at: " + tagStartPosition + ", finished at " + (tagStartPosition + position) + ", length: " + tag.length());

                    if (tag.equals("[icon]")) {
                        ImageSpan imageSpan = new ImageSpan(iconDrawable, ImageSpan.ALIGN_BASELINE);
                        spannable.setSpan(imageSpan, tagStartPosition, tagStartPosition + tag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);    // Spannable#setSpan applies the xxxxSpan to characters n1...n2
                    }
                }
            }

            position++;
        } while (position < length);
    }
}
