package beerfandango.android101.hw2.icelandic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends ActionBarActivity {

    final Context activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

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
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_credits:
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
                builder.setTitle(R.string.action_credits);
                builder.setMessage(R.string.creditsDetails)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog creditsDialog = builder.create();
                creditsDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private ToggleButton toggleAnswerBtn;
        private int currentPosition;
        private TextView promptText;
        private TextView answerText;
        private String[] answers = {"Takk", "Hállo", "Velkomin", "Velkominn", "Hvað heitir þú?",
                "Ég heiti...", "Ég elska þig.", "Hvar er klósettið?", "Hjálp!", "Það var ekkert."};
        private String[] questions = {"Thanks", "Hello", "Welcome (to a male)",
                "Welcome (to a female)", "What's your name?", "My name is...", "I love you.",
                "Where is the toilet?", "Help!", "You're welcome."};
        private int[] sounds = {R.raw.thanks1_is, R.raw.hello1_is, R.raw.welcome1_is,
                R.raw.welcome2_is, R.raw.name_is, R.raw.mynameis_is, R.raw.iloveyou_is,
                R.raw.toilet_is, R.raw.help_is, R.raw.yourewelcome_is};
        private int[] sequence;
        private Button previousButton;
        private Button nextButton;
        private MediaPlayer mediaP;
        private Button replayButton;
        private ToggleButton toggleSoundBtn;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Log.d("ActivityControl", "Main fragment creation started.");

            assert rootView != null;
            // assign views to variables
            promptText = (TextView) rootView.findViewById(R.id.promptTextView);
            answerText = (TextView) rootView.findViewById(R.id.answerTextView);
            toggleAnswerBtn = (ToggleButton) rootView.findViewById(R.id.toggleAnswer);
            previousButton = (Button) rootView.findViewById(R.id.previousButton);
            nextButton = (Button) rootView.findViewById(R.id.nextButton);
            replayButton = (Button) rootView.findViewById(R.id.replayButton);
            toggleSoundBtn = (ToggleButton) rootView.findViewById(R.id.toggleSound);

            // add listeners to buttons
            toggleAnswerBtn.setOnClickListener(this);
            previousButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);
            replayButton.setOnClickListener(this);
            rootView.findViewById(R.id.shuffleDeck).setOnClickListener(this);

            // randomize cards
            randomizeQuestions();
            rewindDeck();

            Log.d("ActivityControl", "Main fragment creation completed.");

            return rootView;
        }

        private void rewindDeck() {
            Log.d("FlashCards", "Deck rewound.");
            // put first questions
            currentPosition = 0;
            showQuestion(currentPosition);
            hideAnswer();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.previousButton:
                    Log.d("ButtonControl", "Previous question button pressed.");
                    if (currentPosition > 0) {
                        currentPosition -= 1;
                        showQuestion(currentPosition);
                        hideAnswer();
                    } else {
                        Log.d("FlashCards", "Reached beginning of deck.");
                    }
                    break;

                case R.id.nextButton:
                    Log.d("ButtonControl", "Next question button pressed.");
                    if (currentPosition < questions.length - 1) {
                        currentPosition += 1;
                        showQuestion(currentPosition);
                        hideAnswer();
                    } else {
                        Log.d("FlashCards", "Reached end of deck.");
                    }
                    break;

                case R.id.toggleAnswer:
                    Log.d("ButtonControl", "Answer button pressed.");
                    if (toggleAnswerBtn.isChecked()) {
                        showAnswer();
                    } else {
                        hideAnswer();
                    }
                    break;
                case R.id.shuffleDeck:
                    Log.d("ButtonControl", "Shuffle button pressed.");
                    randomizeQuestions();
                    rewindDeck();
                    break;
                case R.id.replayButton:
                    Log.d("ButtonControl", "Replay button pressed.");
                    playMedia();
                    break;
            }
        }

        private void hideAnswer() {
            Log.d("ActivityControl", "Hiding answer.");
            // if called programatically, make sure UI is synced up
            if (toggleAnswerBtn.isChecked()) {
                toggleAnswerBtn.setChecked(false);
            }
            // clear out answer
            answerText.setText("");
            // hide replay button
            replayButton.setVisibility(View.INVISIBLE);
        }

        private void playMedia() {
            Log.d("ActivityControl", "Playing media.");
            mediaP.start();
        }

        private void showAnswer() {
            Log.d("ActivityControl", "Showing answer.");
            // if called programatically, make sure UI is synced up
            if (!toggleAnswerBtn.isChecked()) {
                toggleAnswerBtn.setChecked(true);
            }
            // pull answer from list
            answerText.setText(answers[sequence[currentPosition]]);
            // play media
            if (toggleSoundBtn.isChecked()) {
                playMedia();
            }
            // show replay button
            replayButton.setVisibility(View.VISIBLE);
        }

        private void showQuestion(int questionNo) {
            // pull question from list
            Log.i("FlashCards", "Question " + questionNo + " is next.");
            promptText.setText(questions[sequence[questionNo]]);

            // check if previous  button needs to be disabled
            if (currentPosition == 0) {
                previousButton.setEnabled(false);
            } else {
                previousButton.setEnabled(true);
            }
            // check if next button needs to be disabled
            if (currentPosition == questions.length - 1) {
                nextButton.setEnabled(false);
            } else {
                nextButton.setEnabled(true);
            }

            // Prep Media Player
            mediaP = MediaPlayer.create(getActivity(), sounds[sequence[questionNo]]);

        }

        private void randomizeQuestions() {
            Log.d("FlashCards", "Deck randomized.");
            //initialize array and pre-populate with numbers in order
            sequence = new int[questions.length];
            for (int questionNo = 0; questionNo < questions.length; questionNo++) {
                sequence[questionNo] = questionNo;
            }

            // Fisher-Yates shuffle
            Random rnd = new Random();
            for (int currentSeq = sequence.length - 1; currentSeq > 0; currentSeq--) {
                int target = rnd.nextInt(currentSeq + 1);

                // 3-way swap
                int temp = sequence[target];
                sequence[target] = sequence[currentSeq];
                sequence[currentSeq] = temp;
            }
        }

    } // end placeholder fragment
} // end activity
