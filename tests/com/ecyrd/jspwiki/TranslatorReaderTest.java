
package com.ecyrd.jspwiki;

import com.ecyrd.jspwiki.attachment.Attachment;
import com.ecyrd.jspwiki.providers.*;
import junit.framework.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;

public class TranslatorReaderTest extends TestCase
{
    Properties props = new Properties();
    Vector     created = new Vector();

    static final String PAGE_NAME = "testpage";

    TestEngine testEngine;


    public TranslatorReaderTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        props.load( TestEngine.findTestProperties() );

        props.setProperty( "jspwiki.translatorReader.matchEnglishPlurals", "true" );
        testEngine = new TestEngine( props );
    }

    public void tearDown()
    {
        deleteCreatedPages();
    }

    private void newPage( String name )
    {
        testEngine.saveText( name, "<test>" );

        created.addElement( name );
    }

    private void deleteCreatedPages()
    {
        for( Iterator i = created.iterator(); i.hasNext(); )
        {
            String name = (String) i.next();

            testEngine.deletePage(name);
        }

        created.clear();
    }

    private String translate( String src )
        throws IOException,
               NoRequiredPropertyException,
               ServletException
    {
        WikiContext context = new WikiContext( testEngine,
                                               PAGE_NAME );
        Reader r = new TranslatorReader( context, 
                                         new BufferedReader( new StringReader(src)) );
        StringWriter out = new StringWriter();
        int c;

        while( ( c=r.read()) != -1 )
        {
            out.write( c );
        }

        return out.toString();
    }

    public void testHyperlinks2()
        throws Exception
    {
        newPage("Hyperlink");

        String src = "This should be a [hyperlink]";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=Hyperlink\">hyperlink</a>",
                      translate(src) );
    }

    public void testHyperlinks3()
        throws Exception
    {
        newPage("HyperlinkToo");

        String src = "This should be a [hyperlink too]";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperlinkToo\">hyperlink too</a>",
                      translate(src) );
    }

    public void testHyperlinks4()
        throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [HyperLink]";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>",
                      translate(src) );
    }

    public void testHyperlinks5()
        throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [here|HyperLink]";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">here</a>",
                      translate(src) );
    }

    //
    //  Testing CamelCase hyperlinks
    //

    public void testHyperLinks6()
        throws Exception
    {
        newPage("DiscussionAboutWiki");
        newPage("WikiMarkupDevelopment");

        String src = "[DiscussionAboutWiki] [WikiMarkupDevelopment].";

        assertEquals( "<a class=\"wikipage\" href=\"Wiki.jsp?page=DiscussionAboutWiki\">DiscussionAboutWiki</a> <a class=\"wikipage\" href=\"Wiki.jsp?page=WikiMarkupDevelopment\">WikiMarkupDevelopment</a>.",
                      translate(src) );       
    }

    public void testHyperlinksCC()
        throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a HyperLink.";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>.",
                      translate(src) );
    }

    public void testHyperlinksCCNonExistant()
        throws Exception
    {
        String src = "This should be a HyperLink.";

        assertEquals( "This should be a <u>HyperLink</u><a href=\"Edit.jsp?page=HyperLink\">?</a>.",
                      translate(src) );
    }

    /**
     *  Check if the CC hyperlink translator gets confused with
     *  unorthodox bracketed links.
     */

    public void testHyperlinksCC2()
        throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [  HyperLink  ].";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">  HyperLink  </a>.",
                      translate(src) );
    }

    public void testHyperlinksCC3()
        throws Exception
    {
        String src = "This should be a nonHyperLink.";

        assertEquals( "This should be a nonHyperLink.",
                      translate(src) );
    }

    /** Two links on same line. */


    public void testHyperlinksCC4()
        throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "This should be a HyperLink, and ThisToo.";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>, and <a class=\"wikipage\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
                      translate(src) );
    }


    /** Two mixed links on same line. */

    public void testHyperlinksCC5()
        throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "This should be a [HyperLink], and ThisToo.";

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>, and <a class=\"wikipage\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
                      translate(src) );
    }

    /** Closing tags only. */

    public void testHyperlinksCC6()
        throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "] This ] should be a HyperLink], and ThisToo.";

        assertEquals( "] This ] should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>], and <a class=\"wikipage\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
                      translate(src) );
    }

    /** First and last words on line. */
    public void testHyperlinksCCFirstAndLast()
        throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "HyperLink, and ThisToo";

        assertEquals( "<a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>, and <a class=\"wikipage\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>",
                      translate(src) );
    }

    /** Hyperlinks inside URIs. */

    public void testHyperlinksCCURLs()
        throws Exception
    {
        String src = "http://www.foo.bar/ANewHope/";

        assertEquals( "http://www.foo.bar/ANewHope/",
                      translate(src) );
    }

    public void testHyperlinksCCNegated()
        throws Exception
    {
        String src = "This should not be a ~HyperLink.";

        assertEquals( "This should not be a HyperLink.",
                      translate(src) );
    }

    public void testHyperlinksCCNegated2()
        throws Exception
    {
        String src = "~HyperLinks should not be matched.";

        assertEquals( "HyperLinks should not be matched.",
                      translate(src) );
    }

    public void testCCLinkInList()
        throws Exception
    {
        newPage("HyperLink");

        String src = "*HyperLink";

        assertEquals( "<ul>\n<li><a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a></li>\n</ul>\n",
                      translate(src) );
    }

    public void testCCLinkBold()
        throws Exception
    {
        newPage("BoldHyperLink");

        String src = "__BoldHyperLink__";

        assertEquals( "<b><a class=\"wikipage\" href=\"Wiki.jsp?page=BoldHyperLink\">BoldHyperLink</a></b>",
                      translate(src) );
    }

    public void testCCLinkBold2()
        throws Exception
    {
        newPage("HyperLink");

        String src = "Let's see, if a bold __HyperLink__ is correct?";

        assertEquals( "Let's see, if a bold <b><a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a></b> is correct?",
                      translate(src) );
    }

    public void testCCLinkItalic()
        throws Exception
    {
        newPage("ItalicHyperLink");

        String src = "''ItalicHyperLink''";

        assertEquals( "<i><a class=\"wikipage\" href=\"Wiki.jsp?page=ItalicHyperLink\">ItalicHyperLink</a></i>",
                      translate(src) );
    }

    public void testCCLinkWithPunctuation()
        throws Exception
    {
        newPage("HyperLink");

        String src = "Test. Punctuation. HyperLink.";

        assertEquals( "Test. Punctuation. <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>.",
                      translate(src) );
    }

    public void testCCLinkWithPunctuation2()
        throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "Punctuations: HyperLink,ThisToo.";

        assertEquals( "Punctuations: <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>,<a class=\"wikipage\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
                      translate(src) );
    }

    public void testCCLinkWithScandics()
        throws Exception
    {
        newPage("�itiSy��ljy�");

        String src = "Onko t�m� hyperlinkki: �itiSy��ljy�?";

        assertEquals( "Onko t�m� hyperlinkki: <a class=\"wikipage\" href=\"Wiki.jsp?page=%C4itiSy%F6%D6ljy%E4\">�itiSy��ljy�</a>?",
                      translate(src) );
    }


    public void testHyperlinksExt()
        throws Exception
    {
        String src = "This should be a [http://www.regex.fi/]";

        assertEquals( "This should be a <a class=\"external\" href=\"http://www.regex.fi/\">http://www.regex.fi/</a>",
                      translate(src) );
    }

    public void testHyperlinksExt2()
        throws Exception
    {
        String src = "This should be a [link|http://www.regex.fi/]";

        assertEquals( "This should be a <a class=\"external\" href=\"http://www.regex.fi/\">link</a>",
                      translate(src) );
    }

    //
    //  Testing various odds and ends about hyperlink matching.
    //

    public void testHyperlinksPluralMatch()
        throws Exception
    {
        String src = "This should be a [HyperLinks]";

        newPage("HyperLink");

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLink\">HyperLinks</a>",
                      translate(src) );
    }

    public void testHyperlinksPluralMatch2()
        throws Exception
    {
        String src = "This should be a [HyperLinks]";

        assertEquals( "This should be a <u>HyperLinks</u><a href=\"Edit.jsp?page=HyperLinks\">?</a>",
                      translate(src) );
    }

    public void testHyperlinksPluralMatch3()
        throws Exception
    {
        String src = "This should be a [HyperLink]";

        newPage("HyperLinks");

        assertEquals( "This should be a <a class=\"wikipage\" href=\"Wiki.jsp?page=HyperLinks\">HyperLink</a>",
                      translate(src) );
    }


    public void testHyperlinkJS1()
        throws Exception
    {
        String src = "This should be a [link|http://www.haxored.com/\" onMouseOver=\"alert('Hahhaa');\"]";

        assertEquals( "This should be a <a class=\"external\" href=\"http://www.haxored.com/&quot; onMouseOver=&quot;alert('Hahhaa');&quot;\">link</a>",
                      translate(src) );
    }

    public void testHyperlinksInterWiki1()
        throws Exception
    {
        String src = "This should be a [link|JSPWiki:HyperLink]";

        assertEquals( "This should be a <a class=\"interwiki\" href=\"http://www.ecyrd.com/JSPWiki/Wiki.jsp?page=HyperLink\">link</a>",
                      translate(src) );
    }

    public void testNoHyperlink()
        throws Exception
    {
        newPage("HyperLink");

        String src = "This should not be a [[HyperLink]";

        assertEquals( "This should not be a [HyperLink]",
                      translate(src) );
    }

    public void testNoHyperlink2()
        throws Exception
    {
        String src = "This should not be a [[[[HyperLink]";

        assertEquals( "This should not be a [[[HyperLink]",
                      translate(src) );
    }

    public void testNoHyperlink3()
        throws Exception
    {
        String src = "[[HyperLink], and this [[Neither].";

        assertEquals( "[HyperLink], and this [Neither].",
                      translate(src) );
    }

    public void testNoPlugin()
        throws Exception
    {
        String src = "There is [[{NoPlugin}] here.";

        assertEquals( "There is [{NoPlugin}] here.",
                      translate(src) );
    }

    public void testErroneousHyperlink()
        throws Exception
    {
        String src = "What if this is the last char [";

        assertEquals( "What if this is the last char ",
                      translate(src) );
    }

    public void testErroneousHyperlink2()
        throws Exception
    {
        String src = "What if this is the last char [[";

        assertEquals( "What if this is the last char [",
                      translate(src) );
    }

    public void testExtraPagename1()
        throws Exception
    {
        String src = "Link [test_page]";

        newPage("Test_page");

        assertEquals("Link <a class=\"wikipage\" href=\"Wiki.jsp?page=Test_page\">test_page</a>",
                     translate(src) );
    }

    public void testExtraPagename2()
        throws Exception
    {
        String src = "Link [test.page]";

        newPage("Test.page");

        assertEquals("Link <a class=\"wikipage\" href=\"Wiki.jsp?page=Test.page\">test.page</a>",
                     translate(src) );
    }

    public void testExtraPagename3()
        throws Exception
    {
        String src = "Link [.testpage_]";

        newPage(".testpage_");

        assertEquals("Link <a class=\"wikipage\" href=\"Wiki.jsp?page=.testpage_\">.testpage_</a>",
                     translate(src) );
    }

    public void testInlineImages()
        throws Exception
    {
        String src = "Link [test|http://www.ecyrd.com/test.png]";

        assertEquals("Link <img class=\"inline\" src=\"http://www.ecyrd.com/test.png\" alt=\"test\" />",
                     translate(src) );
    }

    public void testInlineImages2()
        throws Exception
    {
        String src = "Link [test|http://www.ecyrd.com/test.ppm]";

        assertEquals("Link <a class=\"external\" href=\"http://www.ecyrd.com/test.ppm\">test</a>",
                     translate(src) );
    }

    public void testInlineImages3()
        throws Exception
    {
        String src = "Link [test|http://images.com/testi]";

        assertEquals("Link <img class=\"inline\" src=\"http://images.com/testi\" alt=\"test\" />",
                     translate(src) );
    }

    public void testInlineImages4()
        throws Exception
    {
        String src = "Link [test|http://foobar.jpg]";

        assertEquals("Link <img class=\"inline\" src=\"http://foobar.jpg\" alt=\"test\" />",
                     translate(src) );
    }

    // No link text should be just embedded link.
    public void testInlineImagesLink2()
        throws Exception
    {
        String src = "Link [http://foobar.jpg]";

        assertEquals("Link <img class=\"inline\" src=\"http://foobar.jpg\" alt=\"http://foobar.jpg\" />",
                     translate(src) );
    }

    public void testInlineImagesLink()
        throws Exception
    {
        String src = "Link [http://link.to/|http://foobar.jpg]";

        assertEquals("Link <a href=\"http://link.to/\"><img class=\"inline\" src=\"http://foobar.jpg\" /></a>",
                     translate(src) );
    }

    public void testInlineImagesLink3()
        throws Exception
    {
        String src = "Link [SandBox|http://foobar.jpg]";

        newPage("SandBox");

        assertEquals("Link <a class=\"wikipage\" href=\"Wiki.jsp?page=SandBox\"><img class=\"inline\" src=\"http://foobar.jpg\" alt=\"SandBox\" /></a>",
                     translate(src) );
    }

    public void testScandicPagename1()
        throws Exception
    {
        String src = "Link [��Test]";

        newPage("��Test"); // FIXME: Should be capital �

        assertEquals("Link <a class=\"wikipage\" href=\"Wiki.jsp?page=%C5%E4Test\">��Test</a>",
                     translate(src));
    }

    public void testParagraph()
        throws Exception
    {
        String src = "1\n\n2\n\n3";

        assertEquals( "1\n<p>\n2\n<p>\n3", translate(src) );
    }

    public void testParagraph2()
        throws Exception
    {
        String src = "[WikiEtiquette]\r\n\r\n[Find page]";

        newPage( "WikiEtiquette" );        

        assertEquals( "<a class=\"wikipage\" href=\"Wiki.jsp?page=WikiEtiquette\">WikiEtiquette</a>\n"+
                      "<p>\n<a class=\"wikipage\" href=\"Wiki.jsp?page=FindPage\">Find page</a>", translate(src) );
    }

    public void testLinebreak()
        throws Exception
    {
        String src = "1\\\\2";

        assertEquals( "1<br />2", translate(src) );
    }

    public void testLinebreakClear()
        throws Exception
    {
        String src = "1\\\\\\2";

        assertEquals( "1<br clear=\"all\" />2", translate(src) );
    }

    public void testTT()
        throws Exception
    {
        String src = "1{{2345}}6";

        assertEquals( "1<tt>2345</tt>6", translate(src) );
    }

    public void testTTAcrossLines()
        throws Exception
    {
        String src = "1{{\n2345\n}}6";

        assertEquals( "1<tt>\n2345\n</tt>6", translate(src) );
    }

    public void testTTLinks()
        throws Exception
    {
        String src = "1{{\n2345\n[a link]\n}}6";

        newPage("ALink");

        assertEquals( "1<tt>\n2345\n<a class=\"wikipage\" href=\"Wiki.jsp?page=ALink\">a link</a>\n</tt>6", translate(src) );
    }

    public void testPre()
        throws Exception
    {
        String src = "1{{{2345}}}6";

        assertEquals( "1<pre>2345</pre>6", translate(src) );
    }

    public void testPre2()
        throws Exception
    {
        String src = "1 {{{ {{{ 2345 }}} }}} 6";

        assertEquals( "1 <pre> {{{ 2345 </pre> }}} 6", translate(src) );
    }

    public void testHTMLInPre()
        throws Exception
    {
        String src = "1 {{{ <b> }}}";

        assertEquals( "1 <pre> &lt;b&gt; </pre>", translate(src) );
    }

    public void testCamelCaseInPre()
        throws Exception
    {
        String src = "1 {{{ CamelCase }}}";

        assertEquals( "1 <pre> CamelCase </pre>", translate(src) );
    }

    public void testList1()
        throws Exception
    {
        String src = "A list:\n* One\n* Two\n* Three\n";

        assertEquals( "A list:\n<ul>\n<li> One\n</li>\n<li> Two\n</li>\n<li> Three\n</li>\n</ul>\n", 
                      translate(src) );
    }

    /** Plain multi line testing:
        <pre>
        * One
          continuing
        * Two
        * Three
        </pre>
     */
    public void testMultilineList1()
        throws Exception
    {
        String src = "A list:\n* One\n continuing.\n* Two\n* Three\n";

        assertEquals( "A list:\n<ul>\n<li> One\n continuing.\n</li>\n<li> Two\n</li>\n<li> Three\n</li>\n</ul>\n", 
                      translate(src) );
    }

    public void testMultilineList2()
        throws Exception
    {
        String src = "A list:\n* One\n continuing.\n* Two\n* Three\nShould be normal.";

        assertEquals( "A list:\n<ul>\n<li> One\n continuing.\n</li>\n<li> Two\n</li>\n<li> Three\n</li>\n</ul>\nShould be normal.", 
                      translate(src) );
    }

    public void testHTML()
        throws Exception
    {
        String src = "<b>Test</b>";

        assertEquals( "&lt;b&gt;Test&lt;/b&gt;", translate(src) );
    }

    public void testHTML2()
        throws Exception
    {
        String src = "<p>";

        assertEquals( "&lt;p&gt;", translate(src) );
    }

    public void testHTMLWhenAllowed()
        throws Exception
    {
        String src = "<p>";

        props.setProperty( "jspwiki.translatorReader.allowHTML", "true" );
        testEngine = new TestEngine( props );

        WikiContext context = new WikiContext( testEngine,
                                               PAGE_NAME );

        Reader r = new TranslatorReader( context, 
                                         new BufferedReader( new StringReader(src)) );
        StringWriter out = new StringWriter();
        int c;

        while( ( c=r.read()) != -1 )
        {
            out.write( c );
        }

        assertEquals( "<p>", out.toString() );
    }

    public void testQuotes()
        throws Exception
    {
        String src = "\"Test\"\"";

        assertEquals( "&quot;Test&quot;&quot;", translate(src) );
    }

    public void testItalicAcrossLinebreak()
        throws Exception
    {
        String src="''This is a\ntest.''";

        assertEquals( "<i>This is a\ntest.</i>", translate(src) );
    }

    public void testBoldAcrossLinebreak()
        throws Exception
    {
        String src="__This is a\ntest.__";

        assertEquals( "<b>This is a\ntest.</b>", translate(src) );
    }

    public void testBoldItalic()
        throws Exception
    {
        String src="__This ''is'' a test.__";

        assertEquals( "<b>This <i>is</i> a test.</b>", translate(src) );
    }

    public void testFootnote1()
        throws Exception
    {
        String src="Footnote[1]";

        assertEquals( "Footnote<a class=\"footnoteref\" href=\"#ref-testpage-1\">[1]</a>", 
                      translate(src) );
    }

    public void testFootnote2()
        throws Exception
    {
        String src="[#2356] Footnote.";

        assertEquals( "<a class=\"footnote\" name=\"ref-testpage-2356\">[#2356]</a> Footnote.", 
                      translate(src) );
    }

    /** Check an reported error condition where empty list items could cause crashes */

    public void testEmptySecondLevelList()
        throws Exception
    {
        String src="A\n\n**\n\nB";

        assertEquals( "A\n<p>\n<ul>\n<ul>\n<li>\n</li>\n</ul>\n</ul>\n<p>\nB", 
                      translate(src) );
    }

    public void testEmptySecondLevelList2()
        throws Exception
    {
        String src="A\n\n##\n\nB";

        assertEquals( "A\n<p>\n<ol>\n<ol>\n<li>\n</li>\n</ol>\n</ol>\n<p>\nB", 
                      translate(src) );
    }

    /**
     * <pre>
     *   *Item A
     *   ##Numbered 1
     *   ##Numbered 2
     *   *Item B
     * </pre>
     *
     * would come out as:
     *<ul>
     * <li>Item A
     * </ul>
     * <ol>
     * <ol>
     * <li>Numbered 1
     * <li>Numbered 2
     * <ul>
     * <li></ol>
     * </ol>
     * Item B
     * </ul>
     *
     *  (by Mahlen Morris).
     */

    // FIXME: does not run - code base is too screwed for that.

    /*
    public void testMixedList()
        throws Exception
    {
        String src="*Item A\n##Numbered 1\n##Numbered 2\n*Item B\n";

        String result = translate(src);

        // Remove newlines for easier parsing.
        result = TextUtil.replaceString( result, "\n", "" );

        assertEquals( "<ul><li>Item A"+
                      "<ol><ol><li>Numbered 1"+
                      "<li>Numbered 2"+
                      "</ol></ol>"+
                      "<li>Item B"+
                      "</ul>",
                      result );
    }
    */
    /**
     *  Like testMixedList() but the list types have been reversed.
     */
    // FIXME: does not run - code base is too screwed for that.
    /*
    public void testMixedList2()
        throws Exception
    {
        String src="#Item A\n**Numbered 1\n**Numbered 2\n#Item B\n";

        String result = translate(src);

        // Remove newlines for easier parsing.
        result = TextUtil.replaceString( result, "\n", "" );

        assertEquals( "<ol><li>Item A"+
                      "<ul><ul><li>Numbered 1"+
                      "<li>Numbered 2"+
                      "</ul></ul>"+
                      "<li>Item B"+
                      "</ol>",
                      result );
    }
    */

    public void testPluginInsert()
        throws Exception
    {
        String src="[{INSERT com.ecyrd.jspwiki.plugin.SamplePlugin WHERE text=test}]";

        assertEquals( "test", translate(src) );
    }

    public void testPluginNoInsert()
        throws Exception
    {
        String src="[{SamplePlugin text=test}]";

        assertEquals( "test", translate(src) );
    }

    public void testPluginInsertJS()
        throws Exception
    {
        String src="Today: [{INSERT JavaScriptPlugin}] ''day''.";

        assertEquals( "Today: <script language=\"JavaScript\"><!--\nfoo='';\n--></script>\n <i>day</i>.", translate(src) );
    }

    public void testShortPluginInsert()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text=test}]";

        assertEquals( "test", translate(src) );
    }

    /**
     *  Test two plugins on same row.
     */
    public void testShortPluginInsert2()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text=test}] [{INSERT SamplePlugin WHERE text=test2}]";

        assertEquals( "test test2", translate(src) );
    }

    public void testPluginQuotedArgs()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text='test me now'}]";

        assertEquals( "test me now", translate(src) );
    }

    public void testPluginDoublyQuotedArgs()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text='test \\'me too\\' now'}]";

        assertEquals( "test 'me too' now", translate(src) );
    }

    public void testPluginQuotedArgs2()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text=foo}] [{INSERT SamplePlugin WHERE text='test \\'me too\\' now'}]";

        assertEquals( "foo test 'me too' now", translate(src) );
    }

    /**
     *  Plugin output must not be parsed as Wiki text.
     */
    public void testPluginWikiText()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text=PageContent}]";

        assertEquals( "PageContent", translate(src) );
    }

    /**
     *  Nor should plugin input be interpreted as wiki text.
     */
    public void testPluginWikiText2()
        throws Exception
    {
        String src="[{INSERT SamplePlugin WHERE text='----'}]";

        assertEquals( "----", translate(src) );
    }

    public void testMultilinePlugin1()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin WHERE\n text=PageContent}]";

        assertEquals( "Test PageContent", translate(src) );
    }

    public void testMultilinePluginBodyContent()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\ntext=PageContent\n\n123\n456\n}]";

        assertEquals( "Test PageContent (123+456+)", translate(src) );
    }

    public void testMultilinePluginBodyContent2()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\ntext=PageContent\n\n\n123\n456\n}]";

        assertEquals( "Test PageContent (+123+456+)", translate(src) );
    }

    public void testMultilinePluginBodyContent3()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\n\n123\n456\n}]";

        assertEquals( "Test  (123+456+)", translate(src) );
    }

    /**
     *  Has an extra space after plugin name.
     */
    public void testMultilinePluginBodyContent4()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin \n\n123\n456\n}]";

        assertEquals( "Test  (123+456+)", translate(src) );
    }

    /**
     *  Check that plugin end is correctly recognized.
     */
    public void testPluginEnd()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin text=']'}]";

        assertEquals( "Test ]", translate(src) );
    }

    public void testPluginEnd2()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin text='a[]+b'}]";

        assertEquals( "Test a[]+b", translate(src) );
    }

    public void testPluginEnd3()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\n\na[]+b\n}]";

        assertEquals( "Test  (a[]+b+)", translate(src) );
    }

    public void testPluginEnd4()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin text='}'}]";

        assertEquals( "Test }", translate(src) );
    }

    public void testPluginEnd5()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\n\na[]+b{}\nGlob.\n}]";

        assertEquals( "Test  (a[]+b{}+Glob.+)", translate(src) );
    }

    public void testPluginEnd6()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\n\na[]+b{}\nGlob.\n}}]";

        assertEquals( "Test  (a[]+b{}+Glob.+})", translate(src) );
    }

    //  FIXME: I am not entirely certain if this is the right result
    //  Perhaps some sort of an error should be checked?
    public void testPluginNoEnd()
        throws Exception
    {
        String src="Test [{INSERT SamplePlugin\n\na+b{}\nGlob.\n}";

        assertEquals( "Test {INSERT SamplePlugin\n\na+b{}\nGlob.\n}", translate(src) );
    }

    public void testVariableInsert()
        throws Exception
    {
        String src="[{$pagename}]";

        assertEquals( PAGE_NAME+"", translate(src) );
    }

    public void testTable1()
        throws Exception
    {
        String src="|| heading || heading2 \n| Cell 1 | Cell 2 \n| Cell 3 | Cell 4\n\n";

        assertEquals( "<table class=\"wikitable\" border=\"1\">\n"+
                      "<tr><th> heading </th><th> heading2 </th></tr>\n"+
                      "<tr><td> Cell 1 </td><td> Cell 2 </td></tr>\n"+
                      "<tr><td> Cell 3 </td><td> Cell 4</td></tr>\n"+
                      "</table>\n<p>\n",
                      translate(src) );
    }

    public void testTable2()
        throws Exception
    {
        String src="||heading||heading2\n|Cell 1| Cell 2\n| Cell 3 |Cell 4\n\n";

        assertEquals( "<table class=\"wikitable\" border=\"1\">\n"+
                      "<tr><th>heading</th><th>heading2</th></tr>\n"+
                      "<tr><td>Cell 1</td><td> Cell 2</td></tr>\n"+
                      "<tr><td> Cell 3 </td><td>Cell 4</td></tr>\n"+
                      "</table>\n<p>\n",
                      translate(src) );
    }

    public void testTable3()
        throws Exception
    {
        String src="|Cell 1| Cell 2\n| Cell 3 |Cell 4\n\n";

        assertEquals( "<table class=\"wikitable\" border=\"1\">\n"+
                      "<tr><td>Cell 1</td><td> Cell 2</td></tr>\n"+
                      "<tr><td> Cell 3 </td><td>Cell 4</td></tr>\n"+
                      "</table>\n<p>\n",
                      translate(src) );
    }

    public void testTableLink()
        throws Exception
    {
        String src="|Cell 1| Cell 2\n|[Cell 3|ReallyALink]|Cell 4\n\n";

        newPage("ReallyALink");

        assertEquals( "<table class=\"wikitable\" border=\"1\">\n"+
                      "<tr><td>Cell 1</td><td> Cell 2</td></tr>\n"+
                      "<tr><td><a class=\"wikipage\" href=\"Wiki.jsp?page=ReallyALink\">Cell 3</a></td><td>Cell 4</td></tr>\n"+
                      "</table>\n<p>\n",
                      translate(src) );
    }

    public void testTableLinkEscapedBar()
        throws Exception
    {
        String src="|Cell 1| Cell~| 2\n|[Cell 3|ReallyALink]|Cell 4\n\n";

        newPage("ReallyALink");

        assertEquals( "<table class=\"wikitable\" border=\"1\">\n"+
                      "<tr><td>Cell 1</td><td> Cell| 2</td></tr>\n"+
                      "<tr><td><a class=\"wikipage\" href=\"Wiki.jsp?page=ReallyALink\">Cell 3</a></td><td>Cell 4</td></tr>\n"+
                      "</table>\n<p>\n",
                      translate(src) );
    }

    public void testDescription()
        throws Exception
    {
        String src=";:Foo";

        assertEquals( "<dl>\n<dt></dt><dd>Foo</dd>\n</dl>",
                      translate(src) );
    }

    public void testDescription2()
        throws Exception
    {
        String src=";Bar:Foo";

        assertEquals( "<dl>\n<dt>Bar</dt><dd>Foo</dd>\n</dl>",
                      translate(src) );
    }

    public void testDescription3()
        throws Exception
    {
        String src=";:";

        assertEquals( "<dl>\n<dt></dt><dd></dd>\n</dl>",
                      translate(src) );
    }

    public void testDescription4()
        throws Exception
    {
        String src=";Bar:Foo :-)";

        assertEquals( "<dl>\n<dt>Bar</dt><dd>Foo :-)</dd>\n</dl>",
                      translate(src) );
    }


    public void testRuler()
        throws Exception
    {
        String src="----";

        assertEquals( "<hr />",
                      translate(src) );
    }

    public void testRulerCombo()
        throws Exception
    {
        String src="----Foo";

        assertEquals( "<hr />Foo",
                      translate(src) );
    }

    public void testShortRuler1()
        throws Exception
    {
        String src="-";

        assertEquals( "-",
                      translate(src) );
    }

    public void testShortRuler2()
        throws Exception
    {
        String src="--";

        assertEquals( "--",
                      translate(src) );
    }

    public void testShortRuler3()
        throws Exception
    {
        String src="---";

        assertEquals( "---",
                      translate(src) );
    }

    public void testLongRuler()
        throws Exception
    {
        String src="------";

        assertEquals( "<hr />",
                      translate(src) );
    }

    public void testHeading1()
        throws Exception
    {
        String src="!Hello";

        assertEquals( "<h4>Hello</h4>",
                      translate(src) );
    }

    public void testHeading2()
        throws Exception
    {
        String src="!!Hello";

        assertEquals( "<h3>Hello</h3>",
                      translate(src) );
    }

    public void testHeading3()
        throws Exception
    {
        String src="!!!Hello";

        assertEquals( "<h2>Hello</h2>",
                      translate(src) );
    }

    public void testHeadingHyperlinks()
        throws Exception
    {
        String src="!!![Hello]";

        assertEquals( "<h2><u>Hello</u><a href=\"Edit.jsp?page=Hello\">?</a></h2>",
                      translate(src) );
    }

    /**
     *  in 2.0.0, this one throws OutofMemoryError.
     */
    public void testBrokenPageText()
        throws Exception
    {
        String translation = translate( brokenPageText );

        assertNotNull( translation );
    }

    /**
     *  Shortened version of the previous one.
     */
    public void testBrokenPageTextShort()
        throws Exception
    {
        String src = "{{{\ncode.}}\n";

        assertEquals( "<pre>\ncode.}}\n</pre>\n", translate(src) );
    }

    /**
     *  Shortened version of the previous one.
     */
    public void testBrokenPageTextShort2()
        throws Exception
    {
        String src = "{{{\ncode.}\n";

        assertEquals( "<pre>\ncode.}\n</pre>\n", translate(src) );
    }


    /**
     *  Test collection of links.
     */

    public void testCollectingLinks()
        throws Exception
    {
        LinkCollector coll = new LinkCollector();
        String src = "[Test]";
        WikiContext context = new WikiContext( testEngine,
                                               PAGE_NAME );

        TranslatorReader r = new TranslatorReader( context, 
                                                   new BufferedReader( new StringReader(src)) );
        r.addLocalLinkHook( coll );
        r.addExternalLinkHook( coll );
        r.addAttachmentLinkHook( coll );

        StringWriter out = new StringWriter();
        
        FileUtil.copyContents( r, out );

        Collection links = coll.getLinks();

        assertEquals( "no links found", 1, links.size() );
        assertEquals( "wrong link", "Test", links.iterator().next() );
    }

    public void testCollectingLinks2()
        throws Exception
    {
        LinkCollector coll = new LinkCollector();
        String src = "["+PAGE_NAME+"/Test.txt]";
        WikiContext context = new WikiContext( testEngine,
                                               PAGE_NAME );

        TranslatorReader r = new TranslatorReader( context, 
                                                   new BufferedReader( new StringReader(src)) );
        r.addLocalLinkHook( coll );
        r.addExternalLinkHook( coll );
        r.addAttachmentLinkHook( coll );

        StringWriter out = new StringWriter();
        
        FileUtil.copyContents( r, out );

        Collection links = coll.getLinks();

        assertEquals( "no links found", 1, links.size() );
        assertEquals( "wrong link", PAGE_NAME+"/Test.txt", 
                      links.iterator().next() );
    }

    public void testCollectingLinksAttachment()
        throws Exception
    {
        // First, make an attachment.

        try
        {
            Attachment att = new Attachment( PAGE_NAME, "TestAtt.txt" );
            att.setAuthor( "FirstPost" );
            testEngine.getAttachmentManager().storeAttachment( att, testEngine.makeAttachmentFile() );

            LinkCollector coll = new LinkCollector();
            String src = "[TestAtt.txt]";
            WikiContext context = new WikiContext( testEngine,
                                                   PAGE_NAME );

            TranslatorReader r = new TranslatorReader( context, 
                                                       new BufferedReader( new StringReader(src)) );
            r.addLocalLinkHook( coll );
            r.addExternalLinkHook( coll );
            r.addAttachmentLinkHook( coll );

            StringWriter out = new StringWriter();
        
            FileUtil.copyContents( r, out );

            Collection links = coll.getLinks();

            assertEquals( "no links found", 1, links.size() );
            assertEquals( "wrong link", PAGE_NAME+"/TestAtt.txt", 
                          links.iterator().next() );
        }
        finally
        {
            String files = testEngine.getWikiProperties().getProperty( BasicAttachmentProvider.PROP_STORAGEDIR );
            File storagedir = new File( files, PAGE_NAME+BasicAttachmentProvider.DIR_EXTENSION );

            if( storagedir.exists() && storagedir.isDirectory() )
                testEngine.deleteAll( storagedir );
        }
    }

    
    // This is a random find: the following page text caused an eternal loop in V2.0.x.
    private static final String brokenPageText = 
                "Please ''check [RecentChanges].\n" + 
        "\n" + 
        "Testing. fewfwefe\n" + 
        "\n" + 
        "CHeck [testpage]\n" + 
        "\n" + 
        "More testing.\n" + 
        "dsadsadsa''\n" + 
        "Is this {{truetype}} or not?\n" + 
        "What about {{{This}}}?\n" + 
        "How about {{this?\n" + 
        "\n" + 
        "{{{\n" + 
        "{{text}}\n" + 
        "}}}\n" + 
        "goo\n" + 
        "\n" + 
        "<b>Not bold</b>\n" + 
        "\n" + 
        "motto\n" + 
        "\n" + 
        "* This is a list which we\n" + 
        "shall continue on a other line.\n" + 
        "* There is a list item here.\n" + 
        "*  Another item.\n" + 
        "* More stuff, which continues\n" + 
        "on a second line.  And on\n" + 
        "a third line as well.\n" + 
        "And a fourth line.\n" + 
        "* Third item.\n" + 
        "\n" + 
        "Foobar.\n" + 
        "\n" + 
        "----\n" + 
        "\n" + 
        "!!!Really big heading\n" + 
        "Text.\n" + 
        "!! Just a normal heading [with a hyperlink|Main]\n" + 
        "More text.\n" + 
        "!Just a small heading.\n" + 
        "\n" + 
        "This should be __bold__ text.\n" + 
        "\n" + 
        "__more bold text continuing\n" + 
        "on the next line.__\n" + 
        "\n" + 
        "__more bold text continuing\n" + 
        "\n" + 
        "on the next paragraph.__\n" + 
        "\n" + 
        "\n" + 
        "This should be normal.\n" +
        "\n" + 
        "Now, let's try ''italic text''.\n" + 
        "\n" + 
        "Bulleted lists:\n" + 
        "* One\n" + 
        "Or more.\n" + 
        "* Two\n" + 
        "\n" + 
        "** Two.One\n" + 
        "\n" + 
        "*** Two.One.One\n" + 
        "\n" + 
        "* Three\n" + 
        "\n" + 
        "Numbered lists.\n" + 
        "# One\n" + 
        "# Two\n" + 
        "# Three\n" + 
        "## Three.One\n" + 
        "## Three.Two\n" + 
        "## Three.Three\n" + 
        "### Three.Three.One\n" + 
        "# Four\n" + 
        "\n" +
        "End?\n" + 
        "\n" + 
        "No, let's {{break}} things.\\ {{{ {{{ {{text}} }}} }}}\n" +
        "\n" + 
        "More breaking.\n" + 
        "\n" +
        "{{{\n" + 
        "code.}}\n" + 
        "----\n" +
        "author: [Asser], [Ebu], [JanneJalkanen], [Jarmo|mailto:jarmo@regex.com.au]\n";
    

    public static Test suite()
    {
        return new TestSuite( TranslatorReaderTest.class );
    }
}
