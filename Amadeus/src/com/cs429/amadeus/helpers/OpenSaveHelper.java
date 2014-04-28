package com.cs429.amadeus.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.Xml;
import android.widget.HorizontalScrollView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.helpers.SoundProfile.Range;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.StaffLayout;

/**
 * This class contains static methods that take care of the opening/saving work.
 */
public class OpenSaveHelper {
    public static final String RECORDINGS_DIR = "recordings";
    public static final String SOUND_PROFILES_DIR = "soundProfiles";

    private OpenSaveHelper() {
    }

    /**
     * Saves the given staff to a file with the given filename.
     * 
     * @param context
     * @param fileName
     * @param staffLayout
     */
    public static void saveSheet(Context context, String fileName,
	    StaffLayout staffLayout) {
	try {
	    String xml = createSheetXML(staffLayout);
	    File root = context.getDir(RECORDINGS_DIR, Context.MODE_PRIVATE);
	    File file = new File(root, fileName);
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
     * @param fileName
     * @param staffLayout
     */
    public static void openSheet(Context context, String fileName,
	    StaffLayout staffLayout) {
	try {
	    staffLayout.clearAllNoteViews();

	    File rootDir = context.getDir(RECORDINGS_DIR, Context.MODE_PRIVATE);
	    File file = new File(rootDir, fileName);

	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(file);
	    document.getDocumentElement().normalize();

	    Element root = (Element) document.getElementsByTagName("sheet")
		    .item(0);
	    NodeList noteList = root.getElementsByTagName("note");
	    for (int i = 0; i < noteList.getLength(); i++) {
		Element noteElem = (Element) noteList.item(i);
		char noteLetter = noteElem.getAttribute("noteLetter").charAt(0);
		int octave = Integer.valueOf(noteElem.getAttribute("octave"));
		boolean isSharp = Boolean.valueOf(noteElem
			.getAttribute("isSharp"));
		int type = Integer.valueOf(noteElem.getAttribute("type"));
		Note note = new Note(noteLetter, octave, isSharp, type);
		staffLayout.addNote(note);
	    }

	    ((HorizontalScrollView) staffLayout.getParent()).scrollTo(0, 0);
	} catch (Exception e) {
	    Log.e("TEST", "exception", e);
	}
    }

    public static SoundProfile openSoundProfile(Context context, String fileName) {
	try {
	    SoundProfile profile = new SoundProfile();

	    File rootDir = context.getDir(SOUND_PROFILES_DIR,
		    Context.MODE_PRIVATE);
	    File file = new File(rootDir, fileName);

	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(file);
	    document.getDocumentElement().normalize();

	    Element root = (Element) document.getElementsByTagName(
		    "soundProfile").item(0);
	    NodeList noteList = root.getElementsByTagName("mapping");
	    for (int i = 0; i < noteList.getLength(); i++) {
		Element noteElem = (Element) noteList.item(i);
		float low = Float.parseFloat(noteElem.getAttribute("low"));
		float high = Float.parseFloat(noteElem.getAttribute("high"));
		String path = noteElem.getAttribute("path");
		profile.addMapping(low, high, path);
	    }

	    return profile;
	} catch (Exception e) {
	    Log.e("TEST", e.toString());
	    return null;
	}
    }

    public static void saveSoundProfile(Context context, String fileName,
	    SoundProfile profile) {
	try {
	    String xml = createSoundProfileXML(profile);
	    File root = context
		    .getDir(SOUND_PROFILES_DIR, Context.MODE_PRIVATE);
	    File file = new File(root, fileName);
	    FileOutputStream outputStream = new FileOutputStream(file, false);
	    outputStream.write(xml.getBytes());
	    outputStream.close();
	} catch (Exception e) {
	    Log.e("TEST", e.toString());
	}
    }

    private static String createSheetXML(StaffLayout staffLayout) {
	if (staffLayout == null) {
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

    private static String createSoundProfileXML(SoundProfile profile) {
	if(profile == null) {
	    return null;
	}
	
	try {
	    StringWriter writer = new StringWriter();
	    XmlSerializer serializer = Xml.newSerializer();
	    serializer.setOutput(writer);
	    serializer.startDocument(null, true);
	    serializer.startTag(null, "soundProfile");
	    for (Entry<Range, String> entry : profile.getMap().entrySet()) {
		Range range = entry.getKey();
		String low = "" + range.low;
		String high = "" + range.high;
		String filePath = entry.getValue();

		serializer.startTag(null, "mapping");
		serializer.attribute(null, "low", low);
		serializer.attribute(null, "high", high);
		serializer.attribute(null, "path", filePath);
		serializer.endTag(null, "mapping");
	    }
	    serializer.endTag(null, "soundProfile");
	    serializer.endDocument();
	    serializer.flush();

	    return writer.toString();
	} catch (Exception e) {
	    Log.e("TEST", e.toString());
	    return null;
	}
    }
}
