/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Panels.MailView;

// The contents of this file are subject to the Mozilla Public License Version
// 1.1
//(the "License"); you may not use this file except in compliance with the
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.apache.tools.ant.filters.StringInputStream;

import org.columba.core.gui.htmlviewer.api.IHTMLViewerPlugin;
import org.columba.core.io.DiskIO;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.simple.XHTMLPanel;

public class ColumbaFSRenderer extends JScrollPane implements
        IHTMLViewerPlugin
{

    private XHTMLPanel panel = new XHTMLPanel();
    URL baseUrl = DiskIO.getResourceURL("org/columba/core/icons/MISC/");

    public ColumbaFSRenderer()
    {
        super();

        setViewportView(panel);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        getVerticalScrollBar().setUnitIncrement(15);

    }

    public void view( String body )
    {
        if (body == null)
        {
            return;
        }
        try
        {

            Tidy tidy = new Tidy();
            StringReader rdr = new StringReader(body);
            StringWriter wrt = new StringWriter();

/*            BufferedInputStream sourceIn = new BufferedInputStream(
                    new ByteArrayInputStream(body.getBytes("ISO-8859-1")));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
*/
            // Set bean properties
            tidy.setQuiet(false);
            tidy.setShowWarnings(true);
            tidy.setIndentContent(true);
            tidy.setSmartIndent(true);
            tidy.setIndentAttributes(false);
            tidy.setWraplen(1024);
            tidy.setXHTML(true);
            tidy.setXmlOut(true);

            tidy.setMakeClean(true);

            tidy.setErrout(new PrintWriter(System.out));

            tidy.parse(rdr, wrt);

            panel.setDocument(new StringInputStream( wrt.toString()), baseUrl.toExternalForm());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getSelectedText()
    {
        return "";
    }

    public boolean initialized()
    {
        return true;
    }

    public JComponent getComponent()
    {
        return panel;
    }

    public JComponent getContainer()
    {
        return this;
    }

    public String getText()
    {
        return "";
    }

    /**
     * @see org.columba.core.gui.htmlviewer.api.IHTMLViewerPlugin#setCaretPosition(int)
     */
    public void setCaretPosition( int position )
    {
        // TODO
    }

    /**
     * @see org.columba.core.gui.htmlviewer.api.IHTMLViewerPlugin#moveCaretPosition(int)
     */
    public void moveCaretPosition( int position )
    {
        // TODO
    }
}
