package com.q42;

import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;

public class Downloader implements Runnable {
	private final BlobKey blobKey;
	private final String outputPath;

	Downloader(BlobKey blobKey, String outputPath) {
		this.blobKey = blobKey;
		this.outputPath = outputPath;
	}

	public void run() {
		// Copy the blob to file
		try {
			InputStream in = new BlobstoreInputStream(this.blobKey);
			FileOutputStream out = new FileOutputStream(this.outputPath);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(out);
		} catch (Exception e) {
			System.out.println("Error while downloading to: " + this.outputPath + ": " + e);
		}
	}
}
