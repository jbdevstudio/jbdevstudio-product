package com.jboss.devstudio.core.installer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.izforge.izpack.util.Debug;

public class JavaVersionReader {
	private static final String SPACE = " ";
	private static final String JAVA_SECURITY_POLICY_SYSPROP = "java.security.policy";
	private static final String JAVA_SECURITY_MANAGER_SYSPROP = "java.security.manager";
	private static final String CMD_COMMAND = "cmd /K chcp 1252"; //$NON-NLS-1$
	private static final String SH_COMMAND = "sh"; //$NON-NLS-1$
	private static final String RESPONSE_STRING_WITH_NO_CODE = "REQUEST_FINISHED";
	private static final String RESPONSE_STRING_PART_WITH_CODE = "REQUEST_FINISHED_";
	private static final String ERROUT_FINISHED_STRING = "ERROUTPUT_FINISHED";

	private Process executable;
	private BufferedReader inputStream;
	private BufferedWriter outputStream;
	private BufferedReader errorStream;
	private StringBuffer fResponseText = new StringBuffer(0);
	private StringBuffer fErrorText = new StringBuffer(0);
	private ResponseListener responseListener;
	
	int errCode = 0;

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public JavaVersionReader() {
	}

	/*
	 * returns the command to execute depending on the OS version 
	 */
	private String getCommand() {
		// send request
		return (isUnixLikeSystem() ? SH_COMMAND : CMD_COMMAND);
	}

	/*
	 * If Unix like system return true
	 * 
	 * @return
	 */
	private boolean isUnixLikeSystem() {
		try {
			return (System.getProperty("os.name").toLowerCase().indexOf("win") == -1);
		} catch (Throwable x) {
			return false;
		}
	}


	/**
	 * @param monitor
	 */
	private void open() throws IOException {
		executable = Runtime.getRuntime().exec(getCommand());
		if (executable != null) {
			inputStream = new BufferedReader(new InputStreamReader(executable.getInputStream()));
			outputStream = new BufferedWriter(new OutputStreamWriter(executable.getOutputStream()));
			errorStream = new BufferedReader(new InputStreamReader(executable.getErrorStream()));
		}
	}

	/**
	 * closes the connection
	 */
	synchronized public void close() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// Ignore I/O Exception on close
			}
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				// Ignore I/O Exception on close
			}
		}
		if (errorStream != null) {
			try {
				errorStream.close();
			} catch (IOException e) {
				// Ignore I/O Exception on close
			}
		}
		executable.destroy();
	}

	public String getOutputText() {
		return fResponseText.toString();
	}

	public String getErrorText() {
		return fErrorText.toString();
	}
	
	/**
	 * Executes a request and processes the responses.
	 * 
	 * @param request the command to execute
	 * 
	 */
	private int executeRequest(String request) throws IOException {
		fResponseText.setLength(0);
		fErrorText.setLength(0);

		// send request
		if (isUnixLikeSystem()) {
			writeLine(request + ";echo REQUEST_FINISHED_$?;echo ERROUTPUT_FINISHED >&2");
		} else {
			File batch = createTempBatFile();
			writeLine(batch, "set ERRORLEVEL=\n" + request + "\necho REQUEST_FINISHED_%ERRORLEVEL%");
			writeLine("\"" + batch.getCanonicalPath() + "\"");
		}
		
		flush();
		
		boolean bErrOutputFinished = false;  
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Just awake.
		}
		for (;;) {
			String error = readErrorLine();
			if (error != null) {
				if (error.trim().equalsIgnoreCase(ERROUT_FINISHED_STRING)) { //$NON-NLS-1$
					bErrOutputFinished = true;
				} else {
					fErrorText.append(error);
					responseListener.errout(error);
					continue;
				}
			}

			// retrieve a response line
			String response = readLine();
			if (response != null && response.trim().length() > 0) {
				// handle completion responses
				if (response.trim().equalsIgnoreCase(RESPONSE_STRING_WITH_NO_CODE)) { //$NON-NLS-1$
					break;
				} else if (response.trim().toUpperCase().startsWith(RESPONSE_STRING_PART_WITH_CODE)) { //$NON-NLS-1$
					try {
						errCode = Integer.parseInt(response.trim().substring(RESPONSE_STRING_PART_WITH_CODE.length()));
					} catch (NumberFormatException x) {
						errCode = 0;
					}
					break;
				} 
				fResponseText.append(response);
				responseListener.stdout(response);
			}
		}

		for (int i = 0; i < 10 && !bErrOutputFinished; i++) {
			String error = readErrorLine();
			if (error != null) {
				if (error.trim().equalsIgnoreCase(ERROUT_FINISHED_STRING)) { //$NON-NLS-1$
					break;
				} else {
					fErrorText.append(error);
					responseListener.errout(error);
				}
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// Just awake.
				}
			}
		}
		return errCode;
	}

	private static String getEncoding() {
		return "Cp1252"; //$NON-NLS-1$
	}

	/**
	 * Flushes the request stream.
	 */
	 void flush() throws IOException {
		outputStream.flush();
	}

	/**
	 * Sends the given string and a newline to the server.
	 */
	public void writeLine(String s) throws IOException {
		Debug.trace("[DEBUG] [ Command ] " + s);
		outputStream.write(s);
		outputStream.newLine();;
	}

	/**
	 * Reads a line from the response stream.
	 */
	public String readLine() throws IOException {
		return inputStream.readLine();
	}

	/**
	 * Reads a error line from the response error stream.
	 */
	public String readErrorLine() throws IOException {
		String result = null;
		if (executable.getErrorStream().available() > 0) {
			result = errorStream.readLine();
		}
		return result;
	}

	/**
	 * Read line from InputStream
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	String readLine(InputStream in) throws IOException {
		return inputStream.readLine();
	}

	public String getJavaVersion(String path) {
		return executeJava(path, " -version");
	}

	public String executeJava(String path, String command) {
		return executeJava(path, command, true);
	}

	public String executeJava(String path, String command,boolean headless) {
		String result = null;
		try {
			open();
			StringBuilder request = new StringBuilder();
			request
				.append("\"")
				.append(path)
				.append(File.separator)
				.append("java\" ")
				.append(headless?"-Djava.awt.headless=true":"")
				.append(SPACE)
				.append(getSecurityProperties())
				.append(SPACE)
				.append(command);

			executeRequest(request.toString());
			result = getErrorText();
		} catch (IOException e) {
			Debug.trace(e);
			result = "";
		} finally {
			close();
		}
		return result;
	}

	
	private Object getSecurityProperties() {
		StringBuilder sp = new StringBuilder();
		String manager = System.getProperty(JAVA_SECURITY_MANAGER_SYSPROP);
		String policy = System.getProperty(JAVA_SECURITY_POLICY_SYSPROP);
		if(manager!=null && policy!=null) {
			sp.append("-D").append(JAVA_SECURITY_MANAGER_SYSPROP).append(manager).append(SPACE);
			sp.append("-D").append(JAVA_SECURITY_POLICY_SYSPROP).append("=\"").append(policy).append("\"");
		}
		return sp.toString();
	}

	public String executeJava(String path, String command, ResponseListener listener) {
		this.responseListener = listener;
		return executeJava(path, command);
	}
	
	public interface ResponseListener {
		void stdout(String line);
		void errout(String line);
	}
	
	public File createTempBatFile () {
		try {
			return File.createTempFile("izpack", ".bat");
		} catch (IOException e) {
			Debug.trace(e);
		}
		return null;
	}
	
	private void writeLine(File batch, String string) {
		try {
			FileWriter writer = new FileWriter(batch);
			writer.append(string);
			writer.close();
		} catch (IOException e) {
			Debug.trace(e);
		}
	}
}
