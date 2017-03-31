package com.reversecoder.javamail.androidstudio.activity;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fsck.k9.mail.Address;
import com.fsck.k9.mail.FetchProfile;
import com.fsck.k9.mail.Flag;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.Message.RecipientType;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mail.Transport;
import com.fsck.k9.mail.TransportProvider;
import com.fsck.k9.mail.internet.MimeMessage;
import com.reversecoder.javamail.androidstudio.R;
import com.reversecoder.javamail.androidstudio.application.JavaMailApplication;
import com.reversecoder.javamail.androidstudio.compose.AttachmentPresenter;
import com.reversecoder.javamail.androidstudio.compose.QuotedMessageMvpView;
import com.reversecoder.javamail.androidstudio.compose.QuotedMessagePresenter;
import com.reversecoder.javamail.androidstudio.compose.RecipientMvpView;
import com.reversecoder.javamail.androidstudio.compose.RecipientPresenter;
import com.reversecoder.javamail.androidstudio.k9.Account;
import com.reversecoder.javamail.androidstudio.k9.Account.MessageFormat;
import com.reversecoder.javamail.androidstudio.k9.Action;
import com.reversecoder.javamail.androidstudio.k9.Identity;
import com.reversecoder.javamail.androidstudio.k9.K9;
import com.reversecoder.javamail.androidstudio.k9.Preferences;
import com.reversecoder.javamail.androidstudio.k9.activity.Accounts;
import com.reversecoder.javamail.androidstudio.k9.activity.K9Activity;
import com.reversecoder.javamail.androidstudio.k9.activity.MessageList;
import com.reversecoder.javamail.androidstudio.k9.activity.MessageLoaderHelper;
import com.reversecoder.javamail.androidstudio.k9.activity.MessageLoaderHelper.MessageLoaderCallbacks;
import com.reversecoder.javamail.androidstudio.k9.activity.MessageReference;
import com.reversecoder.javamail.androidstudio.k9.activity.UpgradeDatabases;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.AttachmentMvpView;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.AttachmentsChangedListener;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.ComposeCryptoStatus;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.ComposeCryptoStatus.SendErrorState;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.CryptoMode;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.CryptoSettingsDialog.OnCryptoModeChangedListener;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.IdentityAdapter;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.IdentityAdapter.IdentityContainer;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.PgpInlineDialog.OnOpenPgpInlineChangeListener;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.PgpSignOnlyDialog.OnOpenPgpSignOnlyChangeListener;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.RecipientsChangedListener;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.SaveMessageTask;
import com.reversecoder.javamail.androidstudio.k9.activity.compose.WaitingAction;
import com.reversecoder.javamail.androidstudio.k9.activity.misc.Attachment;
import com.reversecoder.javamail.androidstudio.k9.controller.MessagingController;
import com.reversecoder.javamail.androidstudio.k9.controller.MessagingListener;
import com.reversecoder.javamail.androidstudio.k9.controller.SimpleMessagingListener;
import com.reversecoder.javamail.androidstudio.k9.fragment.ProgressDialogFragment;
import com.reversecoder.javamail.androidstudio.k9.fragment.ProgressDialogFragment.CancelListener;
import com.reversecoder.javamail.androidstudio.k9.helper.Contacts;
import com.reversecoder.javamail.androidstudio.k9.helper.IdentityHelper;
import com.reversecoder.javamail.androidstudio.k9.helper.MailTo;
import com.reversecoder.javamail.androidstudio.k9.helper.ReplyToParser;
import com.reversecoder.javamail.androidstudio.k9.helper.SimpleTextWatcher;
import com.reversecoder.javamail.androidstudio.k9.helper.Utility;
import com.reversecoder.javamail.androidstudio.k9.mailstore.LocalMessage;
import com.reversecoder.javamail.androidstudio.k9.mailstore.MessageViewInfo;
import com.reversecoder.javamail.androidstudio.k9.message.ComposePgpInlineDecider;
import com.reversecoder.javamail.androidstudio.k9.message.IdentityField;
import com.reversecoder.javamail.androidstudio.k9.message.IdentityHeaderParser;
import com.reversecoder.javamail.androidstudio.k9.message.MessageBuilder;
import com.reversecoder.javamail.androidstudio.k9.message.PgpMessageBuilder;
import com.reversecoder.javamail.androidstudio.k9.message.QuotedTextMode;
import com.reversecoder.javamail.androidstudio.k9.message.SimpleMessageBuilder;
import com.reversecoder.javamail.androidstudio.k9.message.SimpleMessageFormat;
import com.reversecoder.javamail.androidstudio.k9.search.LocalSearch;
import com.reversecoder.javamail.androidstudio.k9.ui.EolConvertingEditText;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import timber.log.Timber;


@SuppressWarnings("deprecation") // TODO get rid of activity dialogs and indeterminate progress bars
public class MessageComposeActivity extends K9Activity implements OnClickListener,
        CancelListener, OnFocusChangeListener, OnCryptoModeChangedListener,
        OnOpenPgpInlineChangeListener, OnOpenPgpSignOnlyChangeListener, MessageBuilder.Callback,
        AttachmentsChangedListener, RecipientsChangedListener {

    private static final int DIALOG_SAVE_OR_DISCARD_DRAFT_MESSAGE = 1;
    private static final int DIALOG_CONFIRM_DISCARD_ON_BACK = 2;
    private static final int DIALOG_CHOOSE_IDENTITY = 3;
    private static final int DIALOG_CONFIRM_DISCARD = 4;

    private static final long INVALID_DRAFT_ID = MessagingController.INVALID_MESSAGE_ID;

    public static final String ACTION_COMPOSE = "com.fsck.k9.intent.action.COMPOSE";
    public static final String ACTION_REPLY = "com.fsck.k9.intent.action.REPLY";
    public static final String ACTION_REPLY_ALL = "com.fsck.k9.intent.action.REPLY_ALL";
    public static final String ACTION_FORWARD = "com.fsck.k9.intent.action.FORWARD";
    public static final String ACTION_EDIT_DRAFT = "com.fsck.k9.intent.action.EDIT_DRAFT";

    public static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_MESSAGE_REFERENCE = "message_reference";
    public static final String EXTRA_MESSAGE_DECRYPTION_RESULT = "message_decryption_result";

    private static final String STATE_KEY_SOURCE_MESSAGE_PROCED =
            "com.fsck.k9.activity.MessageCompose.stateKeySourceMessageProced";
    private static final String STATE_KEY_DRAFT_ID = "com.fsck.k9.activity.MessageCompose.draftId";
    private static final String STATE_IDENTITY_CHANGED =
            "com.fsck.k9.activity.MessageCompose.identityChanged";
    private static final String STATE_IDENTITY =
            "com.fsck.k9.activity.MessageCompose.identity";
    private static final String STATE_IN_REPLY_TO = "com.fsck.k9.activity.MessageCompose.inReplyTo";
    private static final String STATE_REFERENCES = "com.fsck.k9.activity.MessageCompose.references";
    private static final String STATE_KEY_READ_RECEIPT = "com.fsck.k9.activity.MessageCompose.messageReadReceipt";
    private static final String STATE_KEY_CHANGES_MADE_SINCE_LAST_SAVE = "com.fsck.k9.activity.MessageCompose.changesMadeSinceLastSave";
    private static final String STATE_ALREADY_NOTIFIED_USER_OF_EMPTY_SUBJECT = "alreadyNotifiedUserOfEmptySubject";

    private static final String FRAGMENT_WAITING_FOR_ATTACHMENT = "waitingForAttachment";

    private static final int MSG_PROGRESS_ON = 1;
    private static final int MSG_PROGRESS_OFF = 2;
    public static final int MSG_SAVED_DRAFT = 4;
    private static final int MSG_DISCARDED_DRAFT = 5;

    private static final int REQUEST_MASK_RECIPIENT_PRESENTER = (1 << 8);
    private static final int REQUEST_MASK_LOADER_HELPER = (1 << 9);
    private static final int REQUEST_MASK_ATTACHMENT_PRESENTER = (1 << 10);
    private static final int REQUEST_MASK_MESSAGE_BUILDER = (1 << 11);

    /**
     * Regular expression to remove the first localized "Re:" prefix in subjects.
     *
     * Currently:
     * - "Aw:" (german: abbreviation for "Antwort")
     */
    private static final Pattern PREFIX = Pattern.compile("^AW[:\\s]\\s*", Pattern.CASE_INSENSITIVE);

    private QuotedMessagePresenter quotedMessagePresenter;
    private MessageLoaderHelper messageLoaderHelper;
    private AttachmentPresenter attachmentPresenter;

    private Contacts contacts;

    /**
     * The account used for message composition.
     */
    private Account account;
    private Identity identity;
    private boolean identityChanged = false;
    private boolean signatureChanged = false;

    // relates to the message being replied to, forwarded, or edited TODO split up?
    private MessageReference relatedMessageReference;
    /**
     * Indicates that the source message has been processed at least once and should not
     * be processed on any subsequent loads. This protects us from adding attachments that
     * have already been added from the restore of the view state.
     */
    private boolean relatedMessageProcessed = false;

    private RecipientPresenter recipientPresenter;
    private MessageBuilder currentMessageBuilder;
    private boolean finishAfterDraftSaved;
    private boolean alreadyNotifiedUserOfEmptySubject = false;
    private boolean changesMadeSinceLastSave = false;

    /**
     * The database ID of this message's draft. This is used when saving drafts so the message in
     * the database is updated instead of being created anew. This property is INVALID_DRAFT_ID
     * until the first save.
     */
    private long draftId = INVALID_DRAFT_ID;

    private Action action;

    private boolean requestReadReceipt = false;

    private TextView chooseIdentityButton;
    private EditText subjectView;
    private EolConvertingEditText signatureView;
    private EolConvertingEditText messageContentView;
    private LinearLayout attachmentsView;

    private String referencedMessageIds;
    private String repliedToMessageId;

    // The currently used message format.
    private SimpleMessageFormat currentMessageFormat;

    private boolean isInSubActivity = false;

    private boolean navigateUp;

    /**
     * Compose a new message using the given account. If account is null the default account
     * will be used.
     */
    public static void actionCompose(Context context, Account account) {
//        String accountUuid = (account == null) ?
//                Preferences.getPreferences(context).getDefaultAccount().getUuid() :
//                account.getUuid();

        Intent i = new Intent(context, MessageComposeActivity.class);
        i.putExtra(MessageComposeActivity.EXTRA_ACCOUNT, account.getUuid());
        i.setAction(MessageComposeActivity.ACTION_COMPOSE);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (UpgradeDatabases.actionUpgradeDatabases(this, getIntent())) {
//            finish();
//            return;
//        }
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

//        if (K9.getK9ComposerThemeSetting() != K9.Theme.USE_GLOBAL) {
//            // theme the whole content according to the theme (except the action bar)
//            ContextThemeWrapper themeContext = new ContextThemeWrapper(this,
//                    K9.getK9ThemeResourceId(K9.getK9ComposerTheme()));
//            @SuppressLint("InflateParams") // this is the top level activity element, it has no root
//            View v = LayoutInflater.from(themeContext).inflate(R.layout.message_compose, null);
//            TypedValue outValue = new TypedValue();
//            // background color needs to be forced
//            themeContext.getTheme().resolveAttribute(R.attr.messageViewBackgroundColor, outValue, true);
//            v.setBackgroundColor(outValue.data);
//            setContentView(v);
//        } else {
//            setContentView(R.layout.message_compose);
//        }
        setContentView(R.layout.message_compose);

        initializeActionBar();

        // on api level 15, setContentView() shows the progress bar for some reason...
        setProgressBarIndeterminateVisibility(false);

        final Intent intent = getIntent();

        String messageReferenceString = intent.getStringExtra(EXTRA_MESSAGE_REFERENCE);
        relatedMessageReference = MessageReference.parse(messageReferenceString);

        final String accountUuid = (relatedMessageReference != null) ?
                relatedMessageReference.getAccountUuid() :
                intent.getStringExtra(EXTRA_ACCOUNT);

        if (account == null) {
            account = Preferences.getPreferences(this).getDefaultAccount();
        }

//        if (account == null) {
//            /*
//             * There are no accounts set up. This should not have happened. Prompt the
//             * user to set up an account as an acceptable bailout.
//             */
//            startActivity(new Intent(this, Accounts.class));
//            changesMadeSinceLastSave = false;
//            finish();
//            return;
//        }

        contacts = Contacts.getInstance(MessageComposeActivity.this);

        chooseIdentityButton = (TextView) findViewById(R.id.identity);
        chooseIdentityButton.setOnClickListener(this);

        RecipientMvpView recipientMvpView = new RecipientMvpView(this);
        ComposePgpInlineDecider composePgpInlineDecider = new ComposePgpInlineDecider();
        recipientPresenter = new RecipientPresenter(getApplicationContext(), getLoaderManager(), recipientMvpView,
                account, composePgpInlineDecider, new ReplyToParser(), this);
        recipientPresenter.updateCryptoStatus();


        subjectView = (EditText) findViewById(R.id.subject);
        subjectView.getInputExtras(true).putBoolean("allowEmoji", true);
        subjectView.setText("Android app email testing!!!");

        EolConvertingEditText upperSignature = (EolConvertingEditText) findViewById(R.id.upper_signature);
        EolConvertingEditText lowerSignature = (EolConvertingEditText) findViewById(R.id.lower_signature);

        QuotedMessageMvpView quotedMessageMvpView = new QuotedMessageMvpView(this);
        quotedMessagePresenter = new QuotedMessagePresenter(this, quotedMessageMvpView, account);
        attachmentPresenter = new AttachmentPresenter(getApplicationContext(), attachmentMvpView, getLoaderManager(), this);

        messageContentView = (EolConvertingEditText) findViewById(R.id.message_content);
        messageContentView.getInputExtras(true).putBoolean("allowEmoji", true);
        messageContentView.setText("This is a test mail from android app.");

        attachmentsView = (LinearLayout) findViewById(R.id.attachments);

        TextWatcher draftNeedsChangingTextWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changesMadeSinceLastSave = true;
            }
        };

        TextWatcher signTextWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changesMadeSinceLastSave = true;
                signatureChanged = true;
            }
        };

        recipientMvpView.addTextChangedListener(draftNeedsChangingTextWatcher);
        quotedMessageMvpView.addTextChangedListener(draftNeedsChangingTextWatcher);

        subjectView.addTextChangedListener(draftNeedsChangingTextWatcher);

        messageContentView.addTextChangedListener(draftNeedsChangingTextWatcher);

        /*
         * We set this to invisible by default. Other methods will turn it back on if it's
         * needed.
         */

        quotedMessagePresenter.showOrHideQuotedText(QuotedTextMode.NONE);

        subjectView.setOnFocusChangeListener(this);
        messageContentView.setOnFocusChangeListener(this);

        if (savedInstanceState != null) {
            /*
             * This data gets used in onCreate, so grab it here instead of onRestoreInstanceState
             */
            relatedMessageProcessed = savedInstanceState.getBoolean(STATE_KEY_SOURCE_MESSAGE_PROCED, false);
        }


        if (initFromIntent(intent)) {
            action = Action.COMPOSE;
            changesMadeSinceLastSave = true;

            Log.d("rc-k9javamail: ", this.action.name());
        } else {
            String action = intent.getAction();
            if (ACTION_COMPOSE.equals(action)) {
                this.action = Action.COMPOSE;

                Log.d("rc-k9javamail: ", "action name from compose: "+this.action.name());
            } else if (ACTION_REPLY.equals(action)) {
                this.action = Action.REPLY;
            } else if (ACTION_REPLY_ALL.equals(action)) {
                this.action = Action.REPLY_ALL;
            } else if (ACTION_FORWARD.equals(action)) {
                this.action = Action.FORWARD;
            } else if (ACTION_EDIT_DRAFT.equals(action)) {
                this.action = Action.EDIT_DRAFT;
            } else {

                // This shouldn't happen
                Timber.w("MessageCompose was started with an unsupported action");
                this.action = Action.COMPOSE;

                Log.d("rc-k9javamail: ", "action name from else default: "+this.action.name());
            }
        }

        if (identity == null) {
            identity = account.getIdentity(0);
            Log.d("rc-k9javamail: ", "got identity"+identity.getName());
        }

        if (account.isSignatureBeforeQuotedText()) {
            signatureView = upperSignature;
            lowerSignature.setVisibility(View.GONE);
        } else {
            signatureView = lowerSignature;
            upperSignature.setVisibility(View.GONE);
        }
        updateSignature();
        signatureView.addTextChangedListener(signTextWatcher);

        if (!identity.getSignatureUse()) {
            signatureView.setVisibility(View.GONE);
        }

        requestReadReceipt = account.isMessageReadReceiptAlways();

        updateFrom();

        if (!relatedMessageProcessed) {
            if (action == Action.REPLY || action == Action.REPLY_ALL ||
                    action == Action.FORWARD || action == Action.EDIT_DRAFT) {
                messageLoaderHelper = new MessageLoaderHelper(this, getLoaderManager(), getFragmentManager(),
                        messageLoaderCallbacks);
                internalMessageHandler.sendEmptyMessage(MSG_PROGRESS_ON);

                Parcelable cachedDecryptionResult = intent.getParcelableExtra(EXTRA_MESSAGE_DECRYPTION_RESULT);
                messageLoaderHelper.asyncStartOrResumeLoadingMessage(relatedMessageReference, cachedDecryptionResult);
            }

            if (action != Action.EDIT_DRAFT) {
                String alwaysBccString = account.getAlwaysBcc();
                if (!TextUtils.isEmpty(alwaysBccString)) {
                    recipientPresenter.addBccAddresses(Address.parse(alwaysBccString));
                }
            }
        }

        if (action == Action.REPLY || action == Action.REPLY_ALL) {
            relatedMessageReference = relatedMessageReference.withModifiedFlag(Flag.ANSWERED);
        }

        if (action == Action.REPLY || action == Action.REPLY_ALL ||
                action == Action.EDIT_DRAFT) {
            //change focus to message body.
            messageContentView.requestFocus();
        } else {
            // Explicitly set focus to "To:" input field (see issue 2998)
            recipientMvpView.requestFocusOnToField();
        }

        if (action == Action.FORWARD) {
            relatedMessageReference = relatedMessageReference.withModifiedFlag(Flag.FORWARDED);
        }

        updateMessageFormat();

        // Set font size of input controls
        int fontSize = K9.getFontSizes().getMessageComposeInput();
        recipientMvpView.setFontSizes(K9.getFontSizes(), fontSize);
        quotedMessageMvpView.setFontSizes(K9.getFontSizes(), fontSize);
        K9.getFontSizes().setViewTextSize(subjectView, fontSize);
        K9.getFontSizes().setViewTextSize(messageContentView, fontSize);
        K9.getFontSizes().setViewTextSize(signatureView, fontSize);


        updateMessageFormat();

        setTitle();

        currentMessageBuilder = (MessageBuilder) getLastNonConfigurationInstance();
        if (currentMessageBuilder != null) {
            setProgressBarIndeterminateVisibility(true);
            currentMessageBuilder.reattachCallback(this);
        }

        if(currentMessageBuilder==null){
            Log.d("rc-k9javamail: ", "currentMessageBuilder is null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recipientPresenter != null) {
            recipientPresenter.onActivityDestroy();
        }
    }

    /**
     * Handle external intents that trigger the message compose activity.
     *
     * <p>
     * Supported external intents:
     * <ul>
     *   <li>{@link Intent#ACTION_VIEW}</li>
     *   <li>{@link Intent#ACTION_SENDTO}</li>
     *   <li>{@link Intent#ACTION_SEND}</li>
     *   <li>{@link Intent#ACTION_SEND_MULTIPLE}</li>
     * </ul>
     * </p>
     *
     * @param intent
     *         The (external) intent that started the activity.
     *
     * @return {@code true}, if this activity was started by an external intent. {@code false},
     *         otherwise.
     */
    private boolean initFromIntent(final Intent intent) {
        boolean startedByExternalIntent = false;
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SENDTO.equals(action)) {
            /*
             * Someone has clicked a mailto: link. The address is in the URI.
             */
            if (intent.getData() != null) {
                Uri uri = intent.getData();
                if (MailTo.isMailTo(uri)) {
                    MailTo mailTo = MailTo.parse(uri);
                    initializeFromMailto(mailTo);
                }
            }

            /*
             * Note: According to the documentation ACTION_VIEW and ACTION_SENDTO don't accept
             * EXTRA_* parameters.
             * And previously we didn't process these EXTRAs. But it looks like nobody bothers to
             * read the official documentation and just copies wrong sample code that happens to
             * work with the AOSP Email application. And because even big players get this wrong,
             * we're now finally giving in and read the EXTRAs for those actions (below).
             */
        }

        if (Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action) ||
                Intent.ACTION_SENDTO.equals(action) || Intent.ACTION_VIEW.equals(action)) {
            startedByExternalIntent = true;

            /*
             * Note: Here we allow a slight deviation from the documented behavior.
             * EXTRA_TEXT is used as message body (if available) regardless of the MIME
             * type of the intent. In addition one or multiple attachments can be added
             * using EXTRA_STREAM.
             */
            CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
            // Only use EXTRA_TEXT if the body hasn't already been set by the mailto URI
            if (text != null && messageContentView.getText().length() == 0) {
                messageContentView.setCharacters(text);
            }

            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action)) {
                Uri stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (stream != null) {
                    attachmentPresenter.addAttachment(stream, type);
                }
            } else {
                List<Parcelable> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (list != null) {
                    for (Parcelable parcelable : list) {
                        Uri stream = (Uri) parcelable;
                        if (stream != null) {
                            attachmentPresenter.addAttachment(stream, type);
                        }
                    }
                }
            }

            String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            // Only use EXTRA_SUBJECT if the subject hasn't already been set by the mailto URI
            if (subject != null && subjectView.getText().length() == 0) {
                subjectView.setText(subject);
            }

            recipientPresenter.initFromSendOrViewIntent(intent);

        }

        return startedByExternalIntent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagingController.getInstance(this).addListener(messagingListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        MessagingController.getInstance(this).removeListener(messagingListener);

        boolean isPausingOnConfigurationChange = (getChangingConfigurations() & ActivityInfo.CONFIG_ORIENTATION)
                == ActivityInfo.CONFIG_ORIENTATION;
        boolean isCurrentlyBuildingMessage = currentMessageBuilder != null;

        if (isPausingOnConfigurationChange || isCurrentlyBuildingMessage || isInSubActivity) {
            return;
        }

        checkToSaveDraftImplicitly();
    }

    /**
     * The framework handles most of the fields, but we need to handle stuff that we
     * dynamically show and hide:
     * Attachment list,
     * Cc field,
     * Bcc field,
     * Quoted text,
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_KEY_SOURCE_MESSAGE_PROCED, relatedMessageProcessed);
        outState.putLong(STATE_KEY_DRAFT_ID, draftId);
        outState.putSerializable(STATE_IDENTITY, identity);
        outState.putBoolean(STATE_IDENTITY_CHANGED, identityChanged);
        outState.putString(STATE_IN_REPLY_TO, repliedToMessageId);
        outState.putString(STATE_REFERENCES, referencedMessageIds);
        outState.putBoolean(STATE_KEY_READ_RECEIPT, requestReadReceipt);
        outState.putBoolean(STATE_KEY_CHANGES_MADE_SINCE_LAST_SAVE, changesMadeSinceLastSave);
        outState.putBoolean(STATE_ALREADY_NOTIFIED_USER_OF_EMPTY_SUBJECT, alreadyNotifiedUserOfEmptySubject);

        recipientPresenter.onSaveInstanceState(outState);
        quotedMessagePresenter.onSaveInstanceState(outState);
        attachmentPresenter.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (currentMessageBuilder != null) {
            currentMessageBuilder.detachCallback();
        }
        return currentMessageBuilder;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        attachmentsView.removeAllViews();

        requestReadReceipt = savedInstanceState.getBoolean(STATE_KEY_READ_RECEIPT);

        recipientPresenter.onRestoreInstanceState(savedInstanceState);
        quotedMessagePresenter.onRestoreInstanceState(savedInstanceState);
        attachmentPresenter.onRestoreInstanceState(savedInstanceState);

        draftId = savedInstanceState.getLong(STATE_KEY_DRAFT_ID);
        identity = (Identity) savedInstanceState.getSerializable(STATE_IDENTITY);
        identityChanged = savedInstanceState.getBoolean(STATE_IDENTITY_CHANGED);
        repliedToMessageId = savedInstanceState.getString(STATE_IN_REPLY_TO);
        referencedMessageIds = savedInstanceState.getString(STATE_REFERENCES);
        changesMadeSinceLastSave = savedInstanceState.getBoolean(STATE_KEY_CHANGES_MADE_SINCE_LAST_SAVE);
        alreadyNotifiedUserOfEmptySubject = savedInstanceState.getBoolean(STATE_ALREADY_NOTIFIED_USER_OF_EMPTY_SUBJECT);

        updateFrom();

        updateMessageFormat();
    }

    private void setTitle() {
        setTitle(action.getTitleResource());
    }

    @Nullable
    private MessageBuilder createMessageBuilder(boolean isDraft) {
        MessageBuilder builder;

        recipientPresenter.updateCryptoStatus();
        ComposeCryptoStatus cryptoStatus = recipientPresenter.getCurrentCryptoStatus();
        // TODO encrypt drafts for storage
        if (!isDraft && cryptoStatus.shouldUsePgpMessageBuilder()) {
            SendErrorState maybeSendErrorState = cryptoStatus.getSendErrorStateOrNull();
            if (maybeSendErrorState != null) {
                recipientPresenter.showPgpSendError(maybeSendErrorState);
                return null;
            }

            PgpMessageBuilder pgpBuilder = PgpMessageBuilder.newInstance();
            recipientPresenter.builderSetProperties(pgpBuilder);
            builder = pgpBuilder;
        } else {
            builder = SimpleMessageBuilder.newInstance();
        }

        builder.setSubject(Utility.stripNewLines(subjectView.getText().toString()))
                .setSentDate(new Date())
                .setHideTimeZone(K9.hideTimeZone())
                .setTo(recipientPresenter.getToAddresses())
                .setCc(recipientPresenter.getCcAddresses())
                .setBcc(recipientPresenter.getBccAddresses())
                .setInReplyTo(repliedToMessageId)
                .setReferences(referencedMessageIds)
                .setRequestReadReceipt(requestReadReceipt)
                .setIdentity(identity)
                .setMessageFormat(currentMessageFormat)
                .setText(messageContentView.getCharacters())
                .setAttachments(attachmentPresenter.createAttachmentList())
                .setSignature(signatureView.getCharacters())
                .setSignatureBeforeQuotedText(account.isSignatureBeforeQuotedText())
                .setIdentityChanged(identityChanged)
                .setSignatureChanged(signatureChanged)
                .setCursorPosition(messageContentView.getSelectionStart())
                .setMessageReference(relatedMessageReference)
                .setDraft(isDraft)
                .setIsPgpInlineEnabled(cryptoStatus.isPgpInlineModeEnabled());

        quotedMessagePresenter.builderSetProperties(builder);

        Log.d("rc-k9javamail:","message build successfully");

        return builder;
    }

    private void checkToSendMessage() {
        if (subjectView.getText().length() == 0 && !alreadyNotifiedUserOfEmptySubject) {
            Toast.makeText(this, R.string.empty_subject, Toast.LENGTH_LONG).show();
            alreadyNotifiedUserOfEmptySubject = true;
            return;
        }

        if (recipientPresenter.checkRecipientsOkForSending()) {
            return;
        }

        if (attachmentPresenter.checkOkForSendingOrDraftSaving()) {
            return;
        }

        performSendAfterChecks();
    }

    private void checkToSaveDraftAndSave() {
        if (!account.hasDraftsFolder()) {
            Toast.makeText(this, R.string.compose_error_no_draft_folder, Toast.LENGTH_SHORT).show();
            return;
        }

        if (attachmentPresenter.checkOkForSendingOrDraftSaving()) {
            return;
        }

        finishAfterDraftSaved = true;
        performSaveAfterChecks();
    }

    private void checkToSaveDraftImplicitly() {
        if (!account.hasDraftsFolder()) {
            return;
        }

        if (!changesMadeSinceLastSave) {
            return;
        }

        finishAfterDraftSaved = false;
        performSaveAfterChecks();
    }

    private void performSaveAfterChecks() {
        currentMessageBuilder = createMessageBuilder(true);
        if (currentMessageBuilder != null) {
            setProgressBarIndeterminateVisibility(true);
            currentMessageBuilder.buildAsync(this);
        }
    }

    public void performSendAfterChecks() {
        currentMessageBuilder = createMessageBuilder(false);
        if (currentMessageBuilder != null) {
            changesMadeSinceLastSave = false;
            setProgressBarIndeterminateVisibility(true);
            Log.d("rc-k9javamail:","Preparing for buildAsync");
            currentMessageBuilder.buildAsync(this);
        }
    }

    private void onDiscard() {
        if (draftId != INVALID_DRAFT_ID) {
            MessagingController.getInstance(getApplication()).deleteDraft(account, draftId);
            draftId = INVALID_DRAFT_ID;
        }
        internalMessageHandler.sendEmptyMessage(MSG_DISCARDED_DRAFT);
        changesMadeSinceLastSave = false;
        if (navigateUp) {
            openAutoExpandFolder();
        } else {
            finish();
        }
    }

    private void onReadReceipt() {
        CharSequence txt;
        if (!requestReadReceipt) {
            txt = getString(R.string.read_receipt_enabled);
            requestReadReceipt = true;
        } else {
            txt = getString(R.string.read_receipt_disabled);
            requestReadReceipt = false;
        }
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showContactPicker(int requestCode) {
        requestCode |= REQUEST_MASK_RECIPIENT_PRESENTER;
        isInSubActivity = true;
        startActivityForResult(contacts.contactPickerIntent(), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isInSubActivity = false;

        if ((requestCode & REQUEST_MASK_MESSAGE_BUILDER) == REQUEST_MASK_MESSAGE_BUILDER) {
            requestCode ^= REQUEST_MASK_MESSAGE_BUILDER;
            if (currentMessageBuilder == null) {
                Timber.e("Got a message builder activity result for no message builder, " +
                        "this is an illegal state!");
                return;
            }
            currentMessageBuilder.onActivityResult(requestCode, resultCode, data, this);
            return;
        }

        if ((requestCode & REQUEST_MASK_RECIPIENT_PRESENTER) == REQUEST_MASK_RECIPIENT_PRESENTER) {
            requestCode ^= REQUEST_MASK_RECIPIENT_PRESENTER;
            recipientPresenter.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if ((requestCode & REQUEST_MASK_LOADER_HELPER) == REQUEST_MASK_LOADER_HELPER) {
            requestCode ^= REQUEST_MASK_LOADER_HELPER;
            messageLoaderHelper.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if ((requestCode & REQUEST_MASK_ATTACHMENT_PRESENTER) == REQUEST_MASK_ATTACHMENT_PRESENTER) {
            requestCode ^= REQUEST_MASK_ATTACHMENT_PRESENTER;
            attachmentPresenter.onActivityResult(resultCode, requestCode, data);
        }
    }

    private void onAccountChosen(Account account, Identity identity) {
        if (!this.account.equals(account)) {
            Timber.v("Switching account from %s to %s", this.account, account);

            // on draft edit, make sure we don't keep previous message UID
            if (action == Action.EDIT_DRAFT) {
                relatedMessageReference = null;
            }

            // test whether there is something to save
            if (changesMadeSinceLastSave || (draftId != INVALID_DRAFT_ID)) {
                final long previousDraftId = draftId;
                final Account previousAccount = this.account;

                // make current message appear as new
                draftId = INVALID_DRAFT_ID;

                // actual account switch
                this.account = account;

                Timber.v("Account switch, saving new draft in new account");
                checkToSaveDraftImplicitly();

                if (previousDraftId != INVALID_DRAFT_ID) {
                    Timber.v("Account switch, deleting draft from previous account: %d", previousDraftId);

                    MessagingController.getInstance(getApplication()).deleteDraft(previousAccount,
                            previousDraftId);
                }
            } else {
                this.account = account;
            }

            // Show CC/BCC text input field when switching to an account that always wants them
            // displayed.
            // Please note that we're not hiding the fields if the user switches back to an account
            // that doesn't have this setting checked.
            recipientPresenter.onSwitchAccount(this.account);
            quotedMessagePresenter.onSwitchAccount(this.account);

            // not sure how to handle mFolder, mSourceMessage?
        }

        switchToIdentity(identity);
    }

    private void switchToIdentity(Identity identity) {
        this.identity = identity;
        identityChanged = true;
        changesMadeSinceLastSave = true;
        updateFrom();
        updateSignature();
        updateMessageFormat();
        recipientPresenter.onSwitchIdentity(identity);
    }

    private void updateFrom() {
        chooseIdentityButton.setText(identity.getEmail());
    }

    private void updateSignature() {
        if (identity.getSignatureUse()) {
            signatureView.setCharacters(identity.getSignature());
            signatureView.setVisibility(View.VISIBLE);
        } else {
            signatureView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.message_content:
            case R.id.subject:
                if (hasFocus) {
                    recipientPresenter.onNonRecipientFieldFocused();
                }
                break;
        }
    }

    @Override
    public void onCryptoModeChanged(CryptoMode cryptoMode) {
        recipientPresenter.onCryptoModeChanged(cryptoMode);
    }

    @Override
    public void onOpenPgpInlineChange(boolean enabled) {
        recipientPresenter.onCryptoPgpInlineChanged(enabled);
    }

    @Override
    public void onOpenPgpSignOnlyChange(boolean enabled) {
        recipientPresenter.onCryptoPgpSignOnlyDisabled();
    }
    @Override
    public void onAttachmentAdded() {
        changesMadeSinceLastSave = true;
    }

    @Override
    public void onAttachmentRemoved() {
        changesMadeSinceLastSave = true;
    }

    @Override
    public void onRecipientsChanged() {
        changesMadeSinceLastSave = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.identity:
                showDialog(DIALOG_CHOOSE_IDENTITY);
                break;
        }
    }

    private void askBeforeDiscard() {
        if (K9.confirmDiscardMessage()) {
            showDialog(DIALOG_CONFIRM_DISCARD);
        } else {
            onDiscard();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                prepareToFinish(true);
                break;
            case R.id.send:
                checkToSendMessage();
                break;
            case R.id.save:
                checkToSaveDraftAndSave();
                break;
            case R.id.discard:
                askBeforeDiscard();
                break;
            case R.id.add_from_contacts:
                recipientPresenter.onMenuAddFromContacts();
                break;
            case R.id.openpgp_inline_enable:
                recipientPresenter.onMenuSetPgpInline(true);
                updateMessageFormat();
                break;
            case R.id.openpgp_inline_disable:
                recipientPresenter.onMenuSetPgpInline(false);
                updateMessageFormat();
                break;
            case R.id.openpgp_sign_only:
                recipientPresenter.onMenuSetSignOnly(true);
                break;
            case R.id.openpgp_sign_only_disable:
                recipientPresenter.onMenuSetSignOnly(false);
                break;
            case R.id.add_attachment:
                attachmentPresenter.onClickAddAttachment(recipientPresenter);
                break;
            case R.id.read_receipt:
                onReadReceipt();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (isFinishing()) {
            return false;
        }

        getMenuInflater().inflate(R.menu.message_compose_option, menu);

        // Disable the 'Save' menu option if Drafts folder is set to -NONE-
        if (!account.hasDraftsFolder()) {
            menu.findItem(R.id.save).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        recipientPresenter.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        prepareToFinish(false);
    }

    private void prepareToFinish(boolean shouldNavigateUp) {
        navigateUp = shouldNavigateUp;

        if (changesMadeSinceLastSave && draftIsNotEmpty()) {
            if (!account.hasDraftsFolder()) {
                showDialog(DIALOG_CONFIRM_DISCARD_ON_BACK);
            } else {
                showDialog(DIALOG_SAVE_OR_DISCARD_DRAFT_MESSAGE);
            }
        } else {
            // Check if editing an existing draft.
            if (draftId == INVALID_DRAFT_ID) {
                onDiscard();
            } else {
                if (navigateUp) {
                    openAutoExpandFolder();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void openAutoExpandFolder() {
        String folder = account.getAutoExpandFolderName();
        LocalSearch search = new LocalSearch(folder);
        search.addAccountUuid(account.getUuid());
        search.addAllowedFolder(folder);
        MessageList.actionDisplaySearch(this, search, false, true);
        finish();
    }

    private boolean draftIsNotEmpty() {
        if (messageContentView.getText().length() != 0) {
            return true;
        }
        if (!attachmentPresenter.createAttachmentList().isEmpty()) {
            return true;
        }
        if (subjectView.getText().length() != 0) {
            return true;
        }
        if (!recipientPresenter.getToAddresses().isEmpty() ||
                !recipientPresenter.getCcAddresses().isEmpty() ||
                !recipientPresenter.getBccAddresses().isEmpty()) {
            return true;
        }
        return false;
    }


    public void onProgressCancel(ProgressDialogFragment fragment) {
        attachmentPresenter.attachmentProgressDialogCancelled();
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SAVE_OR_DISCARD_DRAFT_MESSAGE:
                return new Builder(this)
                        .setTitle(R.string.save_or_discard_draft_message_dlg_title)
                        .setMessage(R.string.save_or_discard_draft_message_instructions_fmt)
                        .setPositiveButton(R.string.save_draft_action, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismissDialog(DIALOG_SAVE_OR_DISCARD_DRAFT_MESSAGE);
                                checkToSaveDraftAndSave();
                            }
                        })
                        .setNegativeButton(R.string.discard_action, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismissDialog(DIALOG_SAVE_OR_DISCARD_DRAFT_MESSAGE);
                                onDiscard();
                            }
                        })
                        .create();
            case DIALOG_CONFIRM_DISCARD_ON_BACK:
                return new Builder(this)
                        .setTitle(R.string.confirm_discard_draft_message_title)
                        .setMessage(R.string.confirm_discard_draft_message)
                        .setPositiveButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismissDialog(DIALOG_CONFIRM_DISCARD_ON_BACK);
                            }
                        })
                        .setNegativeButton(R.string.discard_action, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismissDialog(DIALOG_CONFIRM_DISCARD_ON_BACK);
                                Toast.makeText(MessageComposeActivity.this,
                                        getString(R.string.message_discarded_toast),
                                        Toast.LENGTH_LONG).show();
                                onDiscard();
                            }
                        })
                        .create();
            case DIALOG_CHOOSE_IDENTITY:
                Context context = new ContextThemeWrapper(this,
                        (K9.getK9Theme() == K9.Theme.LIGHT) ?
                                R.style.Theme_K9_Dialog_Light :
                                R.style.Theme_K9_Dialog_Dark);
                Builder builder = new Builder(context);
                builder.setTitle(R.string.send_as);
                final IdentityAdapter adapter = new IdentityAdapter(context);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IdentityContainer container = (IdentityContainer) adapter.getItem(which);
                        onAccountChosen(container.account, container.identity);
                    }
                });

                return builder.create();
            case DIALOG_CONFIRM_DISCARD: {
                return new Builder(this)
                        .setTitle(R.string.dialog_confirm_delete_title)
                        .setMessage(R.string.dialog_confirm_delete_message)
                        .setPositiveButton(R.string.dialog_confirm_delete_confirm_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        onDiscard();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_confirm_delete_cancel_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .create();
            }
        }
        return super.onCreateDialog(id);
    }

    public void saveDraftEventually() {
        changesMadeSinceLastSave = true;
    }

    public void loadQuotedTextForEdit() {
        if (relatedMessageReference == null) { // shouldn't happen...
            throw new IllegalStateException("tried to edit quoted message with no referenced message");
        }

        messageLoaderHelper.asyncStartOrResumeLoadingMessage(relatedMessageReference, null);
    }

    /**
     * Pull out the parts of the now loaded source message and apply them to the new message
     * depending on the type of message being composed.
     *
     * @param messageViewInfo
     *         The source message used to populate the various text fields.
     */
    private void processSourceMessage(MessageViewInfo messageViewInfo) {
        try {
            switch (action) {
                case REPLY:
                case REPLY_ALL: {
                    processMessageToReplyTo(messageViewInfo);
                    break;
                }
                case FORWARD: {
                    processMessageToForward(messageViewInfo);
                    break;
                }
                case EDIT_DRAFT: {
                    processDraftMessage(messageViewInfo);
                    break;
                }
                default: {
                    Timber.w("processSourceMessage() called with unsupported action");
                    break;
                }
            }
        } catch (MessagingException me) {
            /*
             * Let the user continue composing their message even if we have a problem processing
             * the source message. Log it as an error, though.
             */
            Timber.e(me, "Error while processing source message: ");
        } finally {
            relatedMessageProcessed = true;
            changesMadeSinceLastSave = false;
        }

        updateMessageFormat();
    }

    private void processMessageToReplyTo(MessageViewInfo messageViewInfo) throws MessagingException {
        Message message = messageViewInfo.message;

        if (message.getSubject() != null) {
            final String subject = PREFIX.matcher(message.getSubject()).replaceFirst("");

            if (!subject.toLowerCase(Locale.US).startsWith("re:")) {
                subjectView.setText("Re: " + subject);
            } else {
                subjectView.setText(subject);
            }
        } else {
            subjectView.setText("");
        }

        /*
         * If a reply-to was included with the message use that, otherwise use the from
         * or sender address.
         */
        boolean isReplyAll = action == Action.REPLY_ALL;
        recipientPresenter.initFromReplyToMessage(message, isReplyAll);

        if (message.getMessageId() != null && message.getMessageId().length() > 0) {
            repliedToMessageId = message.getMessageId();

            String[] refs = message.getReferences();
            if (refs != null && refs.length > 0) {
                referencedMessageIds = TextUtils.join("", refs) + " " + repliedToMessageId;
            } else {
                referencedMessageIds = repliedToMessageId;
            }

        } else {
            Timber.d("could not get Message-ID.");
        }

        // Quote the message and setup the UI.
        quotedMessagePresenter.initFromReplyToMessage(messageViewInfo, action);

        if (action == Action.REPLY || action == Action.REPLY_ALL) {
            Identity useIdentity = IdentityHelper.getRecipientIdentityFromMessage(account, message);
            Identity defaultIdentity = account.getIdentity(0);
            if (useIdentity != defaultIdentity) {
                switchToIdentity(useIdentity);
            }
        }

    }

    private void processMessageToForward(MessageViewInfo messageViewInfo) throws MessagingException {
        Message message = messageViewInfo.message;

        String subject = message.getSubject();
        if (subject != null && !subject.toLowerCase(Locale.US).startsWith("fwd:")) {
            subjectView.setText("Fwd: " + subject);
        } else {
            subjectView.setText(subject);
        }

        // "Be Like Thunderbird" - on forwarded messages, set the message ID
        // of the forwarded message in the references and the reply to.  TB
        // only includes ID of the message being forwarded in the reference,
        // even if there are multiple references.
        if (!TextUtils.isEmpty(message.getMessageId())) {
            repliedToMessageId = message.getMessageId();
            referencedMessageIds = repliedToMessageId;
        } else {
            Timber.d("could not get Message-ID.");
        }

        // Quote the message and setup the UI.
        quotedMessagePresenter.processMessageToForward(messageViewInfo);
        attachmentPresenter.processMessageToForward(messageViewInfo);
    }

    private void processDraftMessage(MessageViewInfo messageViewInfo) {
        Message message = messageViewInfo.message;
        draftId = MessagingController.getInstance(getApplication()).getId(message);
        subjectView.setText(message.getSubject());

        recipientPresenter.initFromDraftMessage(message);

        // Read In-Reply-To header from draft
        final String[] inReplyTo = message.getHeader("In-Reply-To");
        if (inReplyTo.length >= 1) {
            repliedToMessageId = inReplyTo[0];
        }

        // Read References header from draft
        final String[] references = message.getHeader("References");
        if (references.length >= 1) {
            referencedMessageIds = references[0];
        }

        if (!relatedMessageProcessed) {
            attachmentPresenter.loadNonInlineAttachments(messageViewInfo);
        }

        // Decode the identity header when loading a draft.
        // See buildIdentityHeader(TextBody) for a detailed description of the composition of this blob.
        Map<IdentityField, String> k9identity = new HashMap<>();
        String[] identityHeaders = message.getHeader(K9.IDENTITY_HEADER);

        if (identityHeaders.length > 0 && identityHeaders[0] != null) {
            k9identity = IdentityHeaderParser.parse(identityHeaders[0]);
        }

        Identity newIdentity = new Identity();
        if (k9identity.containsKey(IdentityField.SIGNATURE)) {
            newIdentity.setSignatureUse(true);
            newIdentity.setSignature(k9identity.get(IdentityField.SIGNATURE));
            signatureChanged = true;
        } else {
            if (message instanceof LocalMessage) {
                newIdentity.setSignatureUse(((LocalMessage) message).getFolder().getSignatureUse());
            }
            newIdentity.setSignature(identity.getSignature());
        }

        if (k9identity.containsKey(IdentityField.NAME)) {
            newIdentity.setName(k9identity.get(IdentityField.NAME));
            identityChanged = true;
        } else {
            newIdentity.setName(identity.getName());
        }

        if (k9identity.containsKey(IdentityField.EMAIL)) {
            newIdentity.setEmail(k9identity.get(IdentityField.EMAIL));
            identityChanged = true;
        } else {
            newIdentity.setEmail(identity.getEmail());
        }

        if (k9identity.containsKey(IdentityField.ORIGINAL_MESSAGE)) {
            relatedMessageReference = null;
            String originalMessage = k9identity.get(IdentityField.ORIGINAL_MESSAGE);
            MessageReference messageReference = MessageReference.parse(originalMessage);

            if (messageReference != null) {
                // Check if this is a valid account in our database
                Preferences prefs = Preferences.getPreferences(getApplicationContext());
                Account account = prefs.getAccount(messageReference.getAccountUuid());
                if (account != null) {
                    relatedMessageReference = messageReference;
                }
            }
        }

        identity = newIdentity;

        updateSignature();
        updateFrom();

        quotedMessagePresenter.processDraftMessage(messageViewInfo, k9identity);
    }

    static class SendMessageTask extends AsyncTask<Void, Void, Void> {
        final Context context;
        final Account account;
        final Contacts contacts;
        final Message message;
        final Long draftId;
        final MessageReference messageReference;

        SendMessageTask(Context context, Account account, Contacts contacts, Message message,
                Long draftId, MessageReference messageReference) {
            this.context = context;
            this.account = account;
            this.contacts = contacts;
            this.message = message;
            this.draftId = draftId;
            this.messageReference = messageReference;
            Log.d("rc-k9javamail: ","In SendMessageTask");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Log.d("rc-k9javamail: ","In SendMessageTask doInBackground");
                contacts.markAsContacted(message.getRecipients(RecipientType.TO));
                contacts.markAsContacted(message.getRecipients(RecipientType.CC));
                contacts.markAsContacted(message.getRecipients(RecipientType.BCC));
                updateReferencedMessage();

                Log.d("rc-k9javamail: ","Passed updateReferencedMessage()");
                Log.d("rc-k9javamail: ","Starting sendMessage()");
                sendMessage(account);
                Log.d("rc-k9javamail: ","Passed sendMessage()");
            } catch (Exception e) {
                Timber.e(e, "Failed to mark contact as contacted.");
            }

//            Log.d("rc-k9javamail: ","Starting sendMessage()");
//            MessagingController.getInstance(context).sendMessage(account, message, null);

//            Log.d("rc-k9javamail: ","Passed sendMessage()");
//            if (draftId != null) {
//                Log.d("rc-k9javamail: ","Draft is found");
//                // TODO set draft id to invalid in MessageCompose!
//                MessagingController.getInstance(context).deleteDraft(account, draftId);
//
//            }

            return null;
        }


        private void sendMessage(Account account) throws Exception{
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.BODY);
            TransportProvider transportProvider = TransportProvider.getInstance();
            Transport transport = transportProvider.getTransport(JavaMailApplication.getGlobalContext(), account);

            message.setFlag(Flag.X_SEND_IN_PROGRESS, true);

            Timber.i("Sending message with UID %s", message.getUid());
            transport.sendMessage(message);

            message.setFlag(Flag.X_SEND_IN_PROGRESS, false);
            message.setFlag(Flag.SEEN, true);
        }


        /**
         * Set the flag on the referenced message(indicated we replied / forwarded the message)
         **/
        private void updateReferencedMessage() {
            if (messageReference != null && messageReference.getFlag() != null) {
                Log.d("rc-k9javamail: ","In updateReferencedMessage()");
                Timber.d("Setting referenced message (%s, %s) flag to %s",
                        messageReference.getFolderName(),
                        messageReference.getUid(),
                        messageReference.getFlag());

                final Account account = Preferences.getPreferences(context)
                        .getAccount(messageReference.getAccountUuid());
                final String folderName = messageReference.getFolderName();
                final String sourceMessageUid = messageReference.getUid();
                MessagingController.getInstance(context).setFlag(account, folderName,
                        sourceMessageUid, messageReference.getFlag(), true);
            }
        }
    }

    /**
     * When we are launched with an intent that includes a mailto: URI, we can actually
     * gather quite a few of our message fields from it.
     *
     * @param mailTo
     *         The MailTo object we use to initialize message field
     */
    private void initializeFromMailto(MailTo mailTo) {
        recipientPresenter.initFromMailto(mailTo);

        String subject = mailTo.getSubject();
        if (subject != null && !subject.isEmpty()) {
            subjectView.setText(subject);
        }

        String body = mailTo.getBody();
        if (body != null && !body.isEmpty()) {
            messageContentView.setCharacters(body);
        }
    }

    private void setCurrentMessageFormat(SimpleMessageFormat format) {
        // This method will later be used to enable/disable the rich text editing mode.

        currentMessageFormat = format;
    }

    public void updateMessageFormat() {
        MessageFormat origMessageFormat = account.getMessageFormat();
        SimpleMessageFormat messageFormat;
        if (origMessageFormat == MessageFormat.TEXT) {
            // The user wants to send text/plain messages. We don't override that choice under
            // any circumstances.
            messageFormat = SimpleMessageFormat.TEXT;
        } else if (quotedMessagePresenter.isForcePlainText()
                && quotedMessagePresenter.includeQuotedText()) {
            // Right now we send a text/plain-only message when the quoted text was edited, no
            // matter what the user selected for the message format.
            messageFormat = SimpleMessageFormat.TEXT;
        } else if (recipientPresenter.isForceTextMessageFormat()) {
            // Right now we only support PGP inline which doesn't play well with HTML. So force
            // plain text in those cases.
            messageFormat = SimpleMessageFormat.TEXT;
        } else if (origMessageFormat == MessageFormat.AUTO) {
            if (action == Action.COMPOSE || quotedMessagePresenter.isQuotedTextText() ||
                    !quotedMessagePresenter.includeQuotedText()) {
                // If the message format is set to "AUTO" we use text/plain whenever possible. That
                // is, when composing new messages and replying to or forwarding text/plain
                // messages.
                messageFormat = SimpleMessageFormat.TEXT;
            } else {
                messageFormat = SimpleMessageFormat.HTML;
            }
        } else {
            // In all other cases use HTML
            messageFormat = SimpleMessageFormat.HTML;
        }

        setCurrentMessageFormat(messageFormat);
    }

    @Override
    public void onMessageBuildSuccess(MimeMessage message, boolean isDraft) {
        if (isDraft) {
            changesMadeSinceLastSave = false;
            currentMessageBuilder = null;

            if (action == Action.EDIT_DRAFT && relatedMessageReference != null) {
                message.setUid(relatedMessageReference.getUid());
            }

            // TODO more appropriate logic here? not sure
            boolean saveRemotely = !recipientPresenter.getCurrentCryptoStatus().shouldUsePgpMessageBuilder();
            new SaveMessageTask(getApplicationContext(), account, contacts, internalMessageHandler,
                    message, draftId, saveRemotely).execute();
            if (finishAfterDraftSaved) {
//                finish();
            } else {
                setProgressBarIndeterminateVisibility(false);
            }
        } else {
            currentMessageBuilder = null;
            if(account==null){
                Log.d("rc-k9javamail: ","account is null");
            }
            if(contacts==null){
                Log.d("rc-k9javamail: ","contacts is null");
            }
            if(message==null){
                Log.d("rc-k9javamail: ","message is null");
            }
            if(draftId==0){
                Log.d("rc-k9javamail: ","draftId is null");
            }
            if(relatedMessageReference==null){
                Log.d("rc-k9javamail: ","relatedMessageReference is null");
            }
            new SendMessageTask(JavaMailApplication.getGlobalContext(), account, contacts, message,
                    draftId != INVALID_DRAFT_ID ? draftId : null, relatedMessageReference).execute();
//            finish();
        }
    }

    @Override
    public void onMessageBuildCancel() {
        currentMessageBuilder = null;
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onMessageBuildException(MessagingException me) {
        Timber.e(me, "Error sending message");
        Toast.makeText(MessageComposeActivity.this,
                getString(R.string.send_failed_reason, me.getLocalizedMessage()), Toast.LENGTH_LONG).show();
        currentMessageBuilder = null;
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onMessageBuildReturnPendingIntent(PendingIntent pendingIntent, int requestCode) {
        requestCode |= REQUEST_MASK_MESSAGE_BUILDER;
        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0);
        } catch (SendIntentException e) {
            Timber.e(e, "Error starting pending intent from builder!");
        }
    }

    public void launchUserInteractionPendingIntent(PendingIntent pendingIntent, int requestCode) {
        requestCode |= REQUEST_MASK_RECIPIENT_PRESENTER;
        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0);
        } catch (SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void loadLocalMessageForDisplay(MessageViewInfo messageViewInfo, Action action) {
        // We check to see if we've previously processed the source message since this
        // could be called when switching from HTML to text replies. If that happens, we
        // only want to update the UI with quoted text (which picks the appropriate
        // part).
        if (relatedMessageProcessed) {
            try {
                quotedMessagePresenter.populateUIWithQuotedMessage(messageViewInfo, true, action);
            } catch (MessagingException e) {
                // Hm, if we couldn't populate the UI after source reprocessing, let's just delete it?
                quotedMessagePresenter.showOrHideQuotedText(QuotedTextMode.HIDE);
                Timber.e(e, "Could not re-process source message; deleting quoted text to be safe.");
            }
            updateMessageFormat();
        } else {
            processSourceMessage(messageViewInfo);
            relatedMessageProcessed = true;
        }
    }

    private MessageLoaderCallbacks messageLoaderCallbacks = new MessageLoaderCallbacks() {
        @Override
        public void onMessageDataLoadFinished(LocalMessage message) {
            // nothing to do here, we don't care about message headers
        }

        @Override
        public void onMessageDataLoadFailed() {
            internalMessageHandler.sendEmptyMessage(MSG_PROGRESS_OFF);
            Toast.makeText(MessageComposeActivity.this, R.string.status_invalid_id_error, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageViewInfoLoadFinished(MessageViewInfo messageViewInfo) {
            internalMessageHandler.sendEmptyMessage(MSG_PROGRESS_OFF);
            loadLocalMessageForDisplay(messageViewInfo, action);
        }

        @Override
        public void onMessageViewInfoLoadFailed(MessageViewInfo messageViewInfo) {
            internalMessageHandler.sendEmptyMessage(MSG_PROGRESS_OFF);
            Toast.makeText(MessageComposeActivity.this, R.string.status_invalid_id_error, Toast.LENGTH_LONG).show();
        }

        @Override
        public void setLoadingProgress(int current, int max) {
            // nvm - we don't have a progress bar
        }

        @Override
        public void startIntentSenderForMessageLoaderHelper(IntentSender si, int requestCode, Intent fillIntent,
                int flagsMask, int flagValues, int extraFlags) {
            try {
                requestCode |= REQUEST_MASK_LOADER_HELPER;
                startIntentSenderForResult(si, requestCode, fillIntent, flagsMask, flagValues, extraFlags);
            } catch (SendIntentException e) {
                Timber.e(e, "Irrecoverable error calling PendingIntent!");
            }
        }

        @Override
        public void onDownloadErrorMessageNotFound() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MessageComposeActivity.this, R.string.status_invalid_id_error, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDownloadErrorNetworkError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MessageComposeActivity.this, R.string.status_network_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void initializeActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // TODO We miss callbacks for this listener if they happens while we are paused!
    public MessagingListener messagingListener = new SimpleMessagingListener() {

        @Override
        public void messageUidChanged(Account account, String folder, String oldUid, String newUid) {
            if (relatedMessageReference == null) {
                return;
            }

            Account sourceAccount = Preferences.getPreferences(MessageComposeActivity.this)
                    .getAccount(relatedMessageReference.getAccountUuid());
            String sourceFolder = relatedMessageReference.getFolderName();
            String sourceMessageUid = relatedMessageReference.getUid();

            boolean changedMessageIsCurrent =
                    account.equals(sourceAccount) && folder.equals(sourceFolder) && oldUid.equals(sourceMessageUid);
            if (changedMessageIsCurrent) {
                relatedMessageReference = relatedMessageReference.withModifiedUid(newUid);
            }
        }

    };

    AttachmentMvpView attachmentMvpView = new AttachmentMvpView() {
        private HashMap<Uri, View> attachmentViews = new HashMap<>();

        @Override
        public void showWaitingForAttachmentDialog(WaitingAction waitingAction) {
            String title;

            switch (waitingAction) {
                case SEND: {
                    title = getString(R.string.fetching_attachment_dialog_title_send);
                    break;
                }
                case SAVE: {
                    title = getString(R.string.fetching_attachment_dialog_title_save);
                    break;
                }
                default: {
                    return;
                }
            }

            ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(title,
                    getString(R.string.fetching_attachment_dialog_message));
            fragment.show(getFragmentManager(), FRAGMENT_WAITING_FOR_ATTACHMENT);
        }

        @Override
        public void dismissWaitingForAttachmentDialog() {
            ProgressDialogFragment fragment = (ProgressDialogFragment)
                    getFragmentManager().findFragmentByTag(FRAGMENT_WAITING_FOR_ATTACHMENT);

            if (fragment != null) {
                fragment.dismiss();
            }
        }

        @Override
        @SuppressLint("InlinedApi")
        public void showPickAttachmentDialog(int requestCode) {
            requestCode |= REQUEST_MASK_ATTACHMENT_PRESENTER;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            isInSubActivity = true;

            startActivityForResult(Intent.createChooser(i, null), requestCode);
        }

        @Override
        public void addAttachmentView(final Attachment attachment) {
            View view = getLayoutInflater().inflate(R.layout.message_compose_attachment, attachmentsView, false);
            attachmentViews.put(attachment.uri, view);

            View deleteButton = view.findViewById(R.id.attachment_delete);
            deleteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attachmentPresenter.onClickRemoveAttachment(attachment.uri);
                }
            });

            updateAttachmentView(attachment);
            attachmentsView.addView(view);
        }

        @Override
        public void updateAttachmentView(Attachment attachment) {
            View view = attachmentViews.get(attachment.uri);
            if (view == null) {
                throw new IllegalArgumentException();
            }

            TextView nameView = (TextView) view.findViewById(R.id.attachment_name);
            boolean hasMetadata = (attachment.state != Attachment.LoadingState.URI_ONLY);
            if (hasMetadata) {
                nameView.setText(attachment.name);
            } else {
                nameView.setText(R.string.loading_attachment);
            }

            View progressBar = view.findViewById(R.id.progressBar);
            boolean isLoadingComplete = (attachment.state == Attachment.LoadingState.COMPLETE);
            progressBar.setVisibility(isLoadingComplete ? View.GONE : View.VISIBLE);
        }

        @Override
        public void removeAttachmentView(Attachment attachment) {
            View view = attachmentViews.get(attachment.uri);
            attachmentsView.removeView(view);
            attachmentViews.remove(attachment.uri);
        }

        @Override
        public void performSendAfterChecks() {
            MessageComposeActivity.this.performSendAfterChecks();
        }

        @Override
        public void performSaveAfterChecks() {
            MessageComposeActivity.this.performSaveAfterChecks();
        }

        @Override
        public void showMissingAttachmentsPartialMessageWarning() {
            Toast.makeText(MessageComposeActivity.this,
                    getString(R.string.message_compose_attachments_skipped_toast), Toast.LENGTH_LONG).show();
        }
    };

    private Handler internalMessageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_ON:
                    setProgressBarIndeterminateVisibility(true);
                    break;
                case MSG_PROGRESS_OFF:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case MSG_SAVED_DRAFT:
                    draftId = (Long) msg.obj;
                    Toast.makeText(
                            MessageComposeActivity.this,
                            getString(R.string.message_saved_toast),
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_DISCARDED_DRAFT:
                    Toast.makeText(
                            MessageComposeActivity.this,
                            getString(R.string.message_discarded_toast),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}