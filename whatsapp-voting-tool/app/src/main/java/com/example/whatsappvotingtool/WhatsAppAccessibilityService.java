
// WhatsAppAccessibilityService.java
package com.example.whatsappvotingtool;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class WhatsAppAccessibilityService extends AccessibilityService {

    private String messageToSend;
    private boolean isMessageSent = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        messageToSend = intent.getStringExtra("message");
        isMessageSent = false;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !isMessageSent) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                sendMessage(rootNode);
            }
        }
    }

    private void sendMessage(AccessibilityNodeInfo node) {
        if (node == null) return;

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);

            if (child != null && "android.widget.EditText".contentEquals(child.getClassName())) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, messageToSend);
                child.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            } else if (child != null && child.getText() != null && child.getText().toString().equalsIgnoreCase("Send")) {
                child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                isMessageSent = true;
                Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
                stopSelf();
                return;
            }

            sendMessage(child);
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "Service Interrupted", Toast.LENGTH_SHORT).show();
    }
}
