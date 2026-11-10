
package com.bgbking.telegramreport;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import android.content.ClipboardManager;
import android.content.ClipData;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(7, 14, 27);
    private final int PANEL = Color.rgb(15, 29, 49);
    private final int CYAN = Color.rgb(0, 220, 220);
    private final int GREEN = Color.rgb(20, 190, 130);

    private EditText groupLink, evidenceLink, details;
    private Spinner reasonSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 28);

        scroll.addView(root);

        TextView brand = new TextView(this);
        brand.setText("BGB KING");
        brand.setTextColor(CYAN);
        brand.setTextSize(32);
        brand.setTypeface(null, Typeface.BOLD);
        brand.setGravity(Gravity.CENTER);

        root.addView(brand);

        TextView subtitle = new TextView(this);
        subtitle.setText("CYBER SAFETY REPORT CENTER");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTextSize(12);
        subtitle.setPadding(0, 8, 0, 24);

        root.addView(subtitle);

        addLabel(root, "TELEGRAM GROUP LINK");

        groupLink = addEdit(
            root,
            "https://t.me/...",
            false
        );

        addLabel(root, "SELECT REPORT REASON");

        String[] reasons = {
            "ব্যক্তিগত ছবি নিয়ে অপব্যবহার",
            "অনুমতি ছাড়া ভিডিও প্রকাশ",
            "ভিডিওতে গালাগালি",
            "ভয়েস কলে গালাগালি",
            "অশ্লীল 18+ মেসেজ",
            "ব্যক্তিগত তথ্য প্রকাশ",
            "অন্যান্য হয়রানি"
        };

        reasonSpinner = new Spinner(this);

        ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                reasons
            );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        reasonSpinner.setAdapter(adapter);
        root.addView(reasonSpinner, matchWrap());

        addLabel(root, "MESSAGE / EVIDENCE LINK");

        evidenceLink = addEdit(
            root,
            "প্রমাণের লিংক (ঐচ্ছিক)",
            false
        );

        addLabel(root, "REPORT DETAILS");

        details = addEdit(
            root,
            "কী ঘটেছে বিস্তারিত লিখুন...",
            true
        );

        Button report = makeButton(
            "PREPARE OFFICIAL REPORT",
            GREEN
        );

        LinearLayout.LayoutParams p = matchWrap();
        p.topMargin = 22;
        root.addView(report, p);

        report.setOnClickListener(v -> prepareReport());

        Button copy = makeButton(
            "COPY REPORT TEXT",
            CYAN
        );

        LinearLayout.LayoutParams cp = matchWrap();
        cp.topMargin = 12;
        root.addView(copy, cp);

        copy.setOnClickListener(v -> copyReport());

        TextView footer = new TextView(this);

        footer.setText(
            "\nBGB KING\n" +
            "CYBER SAFETY ASSISTANT\n\n" +
            "এই অ্যাপ Telegram-এর অফিসিয়াল অ্যাপ নয়। " +
            "রিপোর্ট যাচাই করে নিজে Send করবেন। " +
            "Telegram ব্যবস্থা নেবে কি না, তা তাদের সিদ্ধান্ত।"
        );

        footer.setTextColor(Color.LTGRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setTextSize(12);

        root.addView(footer);

        setContentView(scroll);
    }

    private void prepareReport() {

        String link = groupLink.getText().toString().trim();

        if (!(link.startsWith("https://t.me/") ||
              link.startsWith("http://t.me/"))) {

            groupLink.setError("সঠিক Telegram লিংক দিন");
            return;
        }

        Intent email = new Intent(Intent.ACTION_SENDTO);

        email.setData(
            Uri.parse("mailto:abuse@telegram.org")
        );

        email.putExtra(
            Intent.EXTRA_SUBJECT,
            "Telegram Group Safety Report - BGB KING"
        );

        email.putExtra(
            Intent.EXTRA_TEXT,
            buildReport()
        );

        try {
            startActivity(
                Intent.createChooser(email, "Email App নির্বাচন করুন")
            );
        } catch (Exception e) {
            Toast.makeText(
                this,
                "ইমেইল অ্যাপ পাওয়া যায়নি",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    private String buildReport() {

        String group = groupLink.getText().toString().trim();
        String evidence = evidenceLink.getText().toString().trim();
        String reason = String.valueOf(
            reasonSpinner.getSelectedItem()
        );
        String description = details.getText().toString().trim();

        return "Dear Telegram Safety Team,\n\n" +

            "I would like to report a Telegram group " +
            "for possible privacy violations and harassment.\n\n" +

            "Group Link: " + group + "\n\n" +

            "Report Category: " + reason + "\n\n" +

            "Evidence Link: " +
            (evidence.isEmpty() ? "Not provided" : evidence) +
            "\n\n" +

            "Details:\n" +
            (description.isEmpty()
                ? "Please review the reported content."
                : description) +

            "\n\nPlease investigate the reported content " +
            "and take appropriate action if it violates " +
            "Telegram policies.\n\n" +

            "Thank you.";
    }

    private void copyReport() {

        ClipboardManager clipboard =
            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText(
            "Telegram Report",
            buildReport()
        );

        clipboard.setPrimaryClip(clip);

        Toast.makeText(
            this,
            "Report copied successfully",
            Toast.LENGTH_SHORT
        ).show();
    }

    private void addLabel(LinearLayout root, String text) {

        TextView label = new TextView(this);

        label.setText(text);
        label.setTextColor(CYAN);
        label.setTextSize(13);
        label.setTypeface(null, Typeface.BOLD);
        label.setPadding(2, 20, 2, 8);

        root.addView(label);
    }

    private EditText addEdit(
        LinearLayout root,
        String hint,
        boolean multiline
    ) {

        EditText edit = new EditText(this);

        edit.setHint(hint);
        edit.setTextColor(Color.WHITE);
        edit.setHintTextColor(Color.GRAY);
        edit.setTextSize(14);
        edit.setPadding(14, 12, 14, 12);
        edit.setBackground(panelBackground());

        if (multiline) {
            edit.setMinLines(4);
            edit.setGravity(Gravity.TOP);
        } else {
            edit.setSingleLine(true);
        }

        root.addView(edit, matchWrap());

        return edit;
    }

    private Button makeButton(String text, int color) {

        Button button = new Button(this);

        button.setText(text);
        button.setTextColor(Color.BLACK);
        button.setTextSize(13);
        button.setTypeface(null, Typeface.BOLD);
        button.setAllCaps(false);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(16);

        button.setBackground(bg);

        return button;
    }

    private GradientDrawable panelBackground() {

        GradientDrawable bg = new GradientDrawable();

        bg.setColor(PANEL);
        bg.setCornerRadius(14);
        bg.setStroke(1, CYAN);

        return bg;
    }

    private LinearLayout.LayoutParams matchWrap() {

        return new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }
}
