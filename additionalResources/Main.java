package cmisTest;

import java.io.Console;
import java.util.Scanner;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class Main {
	
	private static String filePath = "/User Homes/SAAS/Queues/Incoming/Common Core.docx";
	private static String folderPath = "/User Homes/SAAS/Queues/Incoming";
	private static String folderId = "";
	private static String alfBox = "wakko";
	private static AlfrescoSession sess = new AlfrescoSession(alfBox);
	
	public static void main(String[] args) {
//		getTest();
		Document doc = uploadTest();
//		printAllTest();
		Scanner scan = new Scanner(System.in);
		String input = "";
		while(!input.equals("y")) {
			System.out.println("Delete? (y/n)");
			input = scan.nextLine();
		}
		deleteTest(doc);
		System.out.println("Deleted");
	}
	
	public static void getTest() {
		filePath="/User Homes/burninga/test1.jpg";
		String id = sess.getObjectIdByPath(filePath);
		System.out.println(id);
	}
	
	public static Document uploadTest() {
		folderId = sess.getObjectIdByPath("/User Homes/burninga");
		System.out.println(folderId);
		ContentStream contentStream = sess.createDocument("ThisIsATest", "panda.jpg");
		return sess.uploadDocument(folderId, "ThisIsATestTitle", contentStream, null, null);
	}
	
	public static void printAllTest() {
		int x = 0;
		int increment = 10;
		int totalTimes = 100;
		long startTime = 0;
		long elapsedTime = 0;
		
		folderId = sess.getObjectIdByPath(folderPath);
		startTime = System.nanoTime();
		for (x = 0; x < totalTimes; x += increment) {
			sess.folderFunction(folderId, increment, x, true, false);
		}
		elapsedTime = (System.nanoTime()-startTime)/1000000000;
		System.out.println("*\n*\n*\n*\n*\n"+x+" documents processed in "+elapsedTime+" seconds.");
		System.out.println("That amounts to "+((long)x/elapsedTime)+" a second, or "+(60*60*(long)x/elapsedTime)+" an hour");
	}
	
	public static void deleteTest(Document doc) {
		doc.delete(true);
	}
}
