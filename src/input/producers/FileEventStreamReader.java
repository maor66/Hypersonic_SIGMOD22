package input.producers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class FileEventStreamReader {

	private final String[] inputFilesPaths;
	private final int eventsPerRead;
	
	private int currentFileIndex;
	private boolean hasMoreEvents;
	private BufferedReader reader;
	
	public FileEventStreamReader(String[] inputDirsPaths, String[] inputFilesPaths, int eventsPerRead) {
		if (inputDirsPaths.length == 0 && inputFilesPaths.length == 0) {
			throw new RuntimeException("No input files specified");
		}
		if (inputDirsPaths.length > 0 && inputFilesPaths.length > 0) {
			throw new RuntimeException("Must either specify files only or directories only");
		}
		if (inputDirsPaths.length > 0) {
			this.inputFilesPaths = enumerateDirectories(inputDirsPaths);
		}
		else {
			this.inputFilesPaths = inputFilesPaths;
		}
		this.eventsPerRead = eventsPerRead;
		currentFileIndex = 0;
		hasMoreEvents = true;
		reader = null;
	}
	
	private String[] enumerateDirectories(String[] inputDirsPaths) {
		List<String[]> listOfNameArrays = new ArrayList<String[]>(inputDirsPaths.length);
		int totalFilesCounter = 0;
		for (String directoryPath : inputDirsPaths) {
			String[] filesFromCurrentDirectory = enumerateDirectory(directoryPath);
			listOfNameArrays.add(filesFromCurrentDirectory);
			totalFilesCounter += filesFromCurrentDirectory.length;
		}
		String[] result = new String[totalFilesCounter];
		int i = 0;
		for (String[] arrayOfFileNames : listOfNameArrays) {
			for (String fileName : arrayOfFileNames) {
				result[i++] = fileName;
			}
		}
		return result;
	}
	
	private String[] enumerateDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();
		List<String> result = new ArrayList<String>();
		for (File file : files) {
			if (file.isDirectory()) {
				result.addAll(Arrays.asList(enumerateDirectory(file.getAbsolutePath())));
			}
			else {
				result.add(file.getAbsolutePath());
			}
		}
		Collections.sort(result);
		return result.toArray(new String[0]);
	}
	
	private void initializeReader() throws UnsupportedEncodingException, FileNotFoundException {
		String currentFilePath = inputFilesPaths[currentFileIndex];
		@SuppressWarnings("resource")
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(currentFilePath), "ASCII");
		reader = eventsPerRead > 0 ? new BufferedReader(inputStreamReader, eventsPerRead) : 
									 new BufferedReader(inputStreamReader);
	}
	
	private void switchInputFile() throws IOException {
		reader.close();
		currentFileIndex++;
		if (currentFileIndex >= inputFilesPaths.length) {
			hasMoreEvents = false;
			reader = null;
		}
		else {
			initializeReader();
		}
	}
	
	private String[] parseLine(String line) {
		return line.split(",");
	}
	
	public String[] getRawEvent() {
		try {
			if (reader == null) {
				initializeReader();
			}
			String line = reader.readLine();
			while (line == null) {
				switchInputFile();
				if (!hasMoreEvents) {
					return null;
				}
				line = reader.readLine();
			}
			return parseLine(line);
		}
		catch (Exception e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			hasMoreEvents = false;
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean hasMoreEvents() {
		return hasMoreEvents;
	}
	
	public void finish() {
		if (reader == null) {
			return;
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
