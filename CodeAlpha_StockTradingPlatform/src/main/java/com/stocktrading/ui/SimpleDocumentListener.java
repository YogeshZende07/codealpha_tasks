package com.stocktrading.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Convenience DocumentListener that runs the same action for insert, remove
 * and changed-update events, so callers can react to any text field edit
 * with a single lambda instead of implementing three methods.
 */
public class SimpleDocumentListener implements DocumentListener {

    private final Runnable action;

    public SimpleDocumentListener(Runnable action) {
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        action.run();
    }
}
