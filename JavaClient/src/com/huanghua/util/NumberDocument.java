
package com.huanghua.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumberDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;

    @Override
    public void insertString(int offset, String s, AttributeSet as) throws BadLocationException {
        if (s.matches("\\d*")) {
            super.insertString(offset, s, as);
        } else {
            return;
        }
    }
}
