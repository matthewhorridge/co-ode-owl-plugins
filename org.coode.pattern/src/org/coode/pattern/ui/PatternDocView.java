package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.browser.MiniBrowser;

import java.awt.*;
import java.net.URL;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class PatternDocView extends AbstractPatternView {

    private MiniBrowser browser;

    protected void initialisePatternView() {
        setLayout(new BorderLayout(6, 6));
        browser = new MiniBrowser();
        browser.showToolBar(true);
        add(browser, BorderLayout.CENTER);
    }

    protected void disposePatternView() {
        browser = null;
    }

    public void selectionChanged(Pattern pattern) {
        if (pattern != null){
            selectionChanged(pattern.getDescriptor());
        }
    }

    public void selectionChanged(PatternDescriptor patternDescr) {
        if (patternDescr != null){
            setHeaderText(patternDescr.toString());
            URL refURL = patternDescr.getReferenceURL();
            //browser.clearHistory();
            browser.setURL(refURL);
        }
    }
}
