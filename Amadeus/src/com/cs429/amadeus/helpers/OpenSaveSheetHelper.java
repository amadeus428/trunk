package com.cs429.amadeus.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.HorizontalScrollView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.StaffLayout;

/**
 * This class contains static methods that take care of the opening/saving
 * sheets work.
 */
public class OpenSaveSheetHelper {
    private static final String DIR = "recordings";

    private OpenSaveSheetHelper() {
    }

    /**
     * Saves the given staff to a file with the given filename.
     * 
     * @param context
     * @param filename
     * @param staffLayout
     */
    public static void saveSheet(Context context, String filename, StaffLayout staffLayout) {
	try {
	    String xml = createSheetXML(staffLayout);
	    File root = context.getDir(DIR, Context.MODE_PRIVATE);
	    File file = new File(root, filename);
	    FileOutputStream outputStream = new FileOutputStream(file, false);
	    outputStream.write(xml.getBytes());
	    outputStream.close();
	} catch (Exception e) {
	    Log.e("TEST", e.toString());
	}
    }

    /**
     * First, clears the staff. Then, opens the sheet music file with the given
     * filename and updates the staff.
     * 
     * @param context
     * @param filename
     * @param staffLayout
     */
    public static void openSheet(Context context, String filename, StaffLayout staffLayout) {
	try {
	    staffLayout.clearAllNoteViews();

	    File rootDir = context.getDir(DIR, Context.MODE_PRIVATE);
	    File file = new File(rootDir, filename);

	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(file);
	    document.getDocumentElement().normalize();

	    Element root = (Element) document.getElementsByTagName("sheet").item(0);
	    NodeList noteList = root.getElementsByTagName("note");
	    for (int i = 0; i < noteList.getLength(); i++) {
		Element noteElem = (Element) noteList.item(i);
		char noteLetter = noteElem.getAttribute("noteLetter").charAt(0);
		int octave = Integer.valueOf(noteElem.getAttribute("octave"));
		boolean isSharp = Boolean.valueOf(noteElem.getAttribute("isSharp"));
		int type = Integer.valueOf(noteElem.getAttribute("type"));
		Note note = new Note(noteLetter, octave, isSharp, type);
		staffLayout.addNote(note);
	    }

	    ((HorizontalScrollView) staffLayout.getParent()).scrollTo(0, 0);
	} catch (Exception e) {
	    Log.e("TEST", "exception", e);
	}
    }

    private static String createSheetXML(StaffLayout staffLayout) {
	if(staffLayout == null) {
	    return null;
	}
	
	try {
	    StringWriter writer = new StringWriter();
	    XmlSerializer serializer = Xml.newSerializer();
	    serializer.setOutput(writer);
	    serializer.startDocument(null, true);
	    serializer.startTag(null, "sheet");
	    for (NoteView noteView : staffLayout.getAllNoteViews()) {
		Note note = noteView.getNote();
		serializer.startTag(null, "note");
		serializer.attribute(null, "noteLetter", "" + note.note);
		serializer.attribute(null, "octave", "" + note.octave);
		serializer.attribute(null, "isSharp", "" + note.isSharp);
		serializer.attribute(null, "type", "" + note.type);
		serializer.endTag(null, "note");
	    }
	    serializer.endTag(null, "sheet");
	    serializer.endDocument();
	    serializer.flush();

	    return writer.toString();
	} catch (Exception e) {
	    Log.e("TEST", e.toString());
	    return null;
	}
    }
}
