package com.q42;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class App {

	private static final String SETTINGS_FILE = "settings.properties";

	private static String getSetting(String key, Properties settings) {

		String value = (String) settings.get(key);

		if (value == null || value.trim().isEmpty()) {
			System.err.println("'" + key + "' not found in " + SETTINGS_FILE);
			System.exit(1);
		}

		return value;
	}

	public static void main(String[] args) throws IOException {

		final Properties settings = new Properties();
		settings.load(new FileInputStream("settings.properties"));

		// Set variables
		int totalNrOfBlobs = 0;
		int nonEmptyBlobCount = 0;
		int downloadedBlobCount = 0;

		// This is the URL of you GAE project, and can also be a subversion.
		final String serverString = String.format("%s.appspot.com", getSetting("appId", settings));
		final String outputFolder = getSetting("outputFolder", settings);
		StopWatch sw = new StopWatch();

		// Create the output directory
		File dir = new File(outputFolder);
		if (!dir.mkdir()) {
			System.err.println("could not create folder '" + dir.getAbsolutePath() + "'");
			System.exit(2);
		}

		// Connect to the remote API
		RemoteApiOptions options = new RemoteApiOptions().server(serverString, 443).useApplicationDefaultCredential();
		RemoteApiInstaller installer = new RemoteApiInstaller();
		installer.install(options);

		try {
			System.out.println("-----------STARTED-----------");
			sw.start();

			// When there are more than 1000 results, you'll get a 'logChunkSizeWarning'.
			// This warning is ignored in the demo.
			Iterator<BlobInfo> blobsIterator = new BlobInfoFactory().queryBlobInfos();
			while (blobsIterator.hasNext()) {
				totalNrOfBlobs++;

				// Get the next Blob, skip it if it's empty
				BlobInfo blobInfo = (BlobInfo) blobsIterator.next();
				if (blobInfo.getSize() == 0) {
					continue;
				};
				
				nonEmptyBlobCount++;

				// Create the new output path
				String outputPath = outputFolder + "/" + createFilenameFromBlobInfo(blobInfo);

				// Skip already existing files
				File file = new File(outputPath);
				if (file.exists()) {
					continue;
				}

				//Download the blob
				InputStream in = new BlobstoreInputStream(blobInfo.getBlobKey());
				FileOutputStream out = new FileOutputStream(outputPath);
				IOUtils.copy(in, out);
				IOUtils.closeQuietly(out);

				downloadedBlobCount++;

				// Output some information every 100 blobs
				if (downloadedBlobCount % 100 == 0) {
					System.out.println("- Downloaded: " + downloadedBlobCount + " blobs in: " + getDuration(sw.getTime()));
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e);
			e.printStackTrace();
		} finally {
			installer.uninstall();
		}

		sw.stop();
		System.out.println("- Summary: totalBlobs: " + totalNrOfBlobs + ", not empty blobs: " + nonEmptyBlobCount + ", downloaded this run: " + downloadedBlobCount
				+ ", time taken: " + getDuration(sw.getTime()));
		System.out.println("-----------FINISHED-----------");
	}

	public static String getDuration(long ms) {
		double seconds = Math.ceil(ms / 1000);
		return (int) Math.floor(seconds / 60) + " min, " + (int) seconds % 60 + " sec";
	}

	public static String createFilenameFromBlobInfo(BlobInfo blobInfo) {
		String filename = blobInfo.getFilename();
		return blobInfo.getBlobKey().getKeyString() + getExtensionFromFilename(filename);
	}

	public static String getExtensionFromFilename(String filename) {
		String extension = "";

		int index = filename.lastIndexOf('.');
		if (index >= 0) {
			extension = filename.substring(index);
		}

		return extension;
	}
}
