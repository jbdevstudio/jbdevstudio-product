package com.jboss.devstudio.core.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.izforge.izpack.util.Debug;

public class JavaVersionReader {
	private static final String JAVA_SECURITY_POLICY_SYSPROP = "java.security.policy";
	private static final String JAVA_SECURITY_MANAGER_SYSPROP = "java.security.manager";

	private Process executable;
	private BufferedReader inputStream;
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
		if (errorStream != null) {
			try {
				errorStream.close();
			} catch (IOException e) {
				// Ignore I/O Exception on close
			}
		}
		if (executable != null) {
		  executable.destroy();
		}
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
	private int executeRequest(String request, List<String> arguments) throws IOException {
		fResponseText.setLength(0);
		fErrorText.setLength(0);

		// send request
		open(request, arguments);
		CountDownLatch latch = new CountDownLatch(2);
		new Thread(() -> readErrorLine(latch)).start();
		new Thread(() -> readLine(latch)).start();
		try {
      latch.await();
    } catch (InterruptedException e) {
      throw new IOException(e);
    }
		errCode = executable.exitValue();
		
		return errCode;
	}

  private void open(String request, List<String> arguments) throws IOException {
    List<String> args = new ArrayList<>();
    args.add(request);
    args.addAll(arguments);
    executable = new ProcessBuilder(args).start();
		inputStream = new BufferedReader(new InputStreamReader(executable.getInputStream()));
    errorStream = new BufferedReader(new InputStreamReader(executable.getErrorStream()));
  }

	/**
	 * Reads a line from the response stream.
	 */
	public void readLine(CountDownLatch latch) {
	  String str;
	  
	  try {
      while ((str = inputStream.readLine()) != null) {
        fResponseText.append(str);
        responseListener.stdout(str);
      }
    } catch (IOException e) {
      Debug.trace(e);
    }
		latch.countDown();
	}

	/**
	 * Reads a error line from the response error stream.
	 */
	public void readErrorLine(CountDownLatch latch) {
    String str;
    
    try {
      while ((str = errorStream.readLine()) != null) {
        fErrorText.append(str);
        responseListener.errout(str);
      }
    } catch (IOException e) {
      Debug.trace(e);
    }
    latch.countDown();
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

	public String executeJava(String path, List<String> args) {
		return executeJava(path, args, true);
	}

	public String executeJava(String path, List<String> args,boolean headless) {
		String result = null;
		List<String> arguments = new ArrayList<>();
		try {
		  path = path + File.separator + (isUnixLikeSystem()?"java":"javaw");
		  if (headless) {
		    arguments.add("-Djava.awt.headless=true");
		  }
		  String manager = getSecurityProperties(true);
		  if (manager.length() > 0) {
		    arguments.add(manager);
		  }
      String policy = getSecurityProperties(false);
      if (policy.length() > 0) {
        arguments.add(policy);
      }
      arguments.addAll(args);
			executeRequest(path, arguments);
			result = getErrorText();
		} catch (IOException e) {
			Debug.trace(e);
			result = "";
		} finally {
			close();
		}
		return result;
	}

	private String getSecurityProperties(boolean forManager) {
		StringBuilder sp = new StringBuilder();
    String policy = System.getProperty(JAVA_SECURITY_POLICY_SYSPROP);
		String manager = System.getProperty(JAVA_SECURITY_MANAGER_SYSPROP);
		if(manager!=null && !manager.isBlank() && policy != null && !policy.isBlank()) {
		  if (forManager) {
		    sp.append("-D").append(JAVA_SECURITY_MANAGER_SYSPROP).append('=').append(manager);
		  } else {
	      sp.append("-D").append(JAVA_SECURITY_POLICY_SYSPROP).append("=\"").append(policy).append("\"");
		  }
		}
		return sp.toString();
	}

  public String executeJava(String path, List<String> args, ResponseListener listener) {
		this.responseListener = listener;
		return executeJava(path, args);
	}
	
	public interface ResponseListener {
		void stdout(String line);
		void errout(String line);
	}
}
