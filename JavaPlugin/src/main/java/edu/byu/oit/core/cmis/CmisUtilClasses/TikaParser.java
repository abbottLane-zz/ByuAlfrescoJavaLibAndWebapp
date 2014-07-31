package edu.byu.oit.core.cmis.CmisUtilClasses;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

public class TikaParser extends AbstractParser {

    ////////////////////////////////////////////////////////////////////////////
    ///put these outside of the parser to make the object parser more dynamic
    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.application("jpeg"));
    public static final String MIME_TYPE = "image/jpeg";
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext parseContext) {
        return null;
    }

    @Override
    public void parse(InputStream inputStream, ContentHandler contentHandler, Metadata metadata, ParseContext parseContext) throws IOException, SAXException, TikaException {
        metadata.set(Metadata.CONTENT_TYPE, MIME_TYPE);
        metadata.set("Hello", "World");

        XHTMLContentHandler xhtml = new XHTMLContentHandler(contentHandler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
    }
}
