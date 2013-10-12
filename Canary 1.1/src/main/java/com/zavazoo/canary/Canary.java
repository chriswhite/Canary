/*
 * Zavazoo Canary 1.1 - Java API for extended non-intrusive trace logging 
 * Copyright (C) 2011-2013 Chris White <chriswhitelondon@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zavazoo.canary;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.zavazoo.canary.output.VariableDecorator;
import com.zavazoo.canary.output.VariableDecoratorStrategy;

/**
 * Defines a Canary that enables flexible trace logging with extended but
 * non-intrusive syntax and automatic human-readable representation of any
 * variables to the application logs and/or standard output.<br/>
 * <br/>
 * Canary is abstract and thread-safe but is also mutable simultaneously for all
 * threads in order to switch Canary on and off programmatically for periods of
 * high load or activities related to timing or profiling.<br/>
 * <br/>
 * Canary will represent the following variables, and will similarly represent
 * any variable elements or entries to infinite regress, using the following
 * syntax:<br/>
 * <br/>
 * Array - [element1, element2, element3]<br/>
 * Collection - (element1, element2, element3)<br/>
 * Map - {entry1key => entry1value, entry2key => entry2value, entry3key =>
 * entry3value}<br/>
 * null - null<br/>
 * char=0 - null<br/>
 * default - variable.toString()<br/>
 * <br/>
 * Canary reads configuration data from a properties file named
 * canary.properties in the root of the class hierarchy alongside
 * log4j.properties. An average Canary properties file would contain the
 * following text: <br/>
 * <br/>
 * # Initialises Canary that will write to the application logs if the<br/>
 * # canary.writeToApplicationLogs property is true and the application log
 * level<br/>
 * # is less severe or equal to the log level specified using the
 * canary.logLevel<br/>
 * # property and that will write to standard output, if the application log
 * level<br/>
 * # is less severe or equal to the specified log level, if the<br/>
 * # canary.writeToStandardOutput property is true<br/>
 * <br/>
 * # Canary log level e.g. all, trace, debug, info, warn, error, fatal, off<br/>
 * canary.logLevel=trace<br/>
 * <br/>
 * # Write to the application logs e.g. true, false<br/>
 * canary.writeToApplicationLogs=false<br/>
 * <br/>
 * # Write to standard output e.g. true, false<br/>
 * canary.writeToStandardOutput=false<br/>
 * <br/>
 * # The maximum number of characters that this Canary will represent in any
 * entry<br/>
 * # written to the application logs or standard output. Defined in order to<br/>
 * # obviate the prospect of Canary acting as a CPU hog during the output of<br/>
 * # extremely long lines<br/>
 * canary.maximumRepresentationCharacters=200
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public abstract class Canary {

	/**
	 * The logger used when calling components do not specify their own logger.
	 */
	protected static final Logger logger = Logger.getLogger(Canary.class);

	/** The date format used to represent variables to standard output. */
	private static DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss.SSS");

	/**
	 * Switch used to indicate that this Canary is muted and therefore will
	 * never write to the application logs or standard output. If the muted
	 * switch is null then this Canary will write to the application logs or
	 * standard output.
	 */
	private static volatile Boolean muted = null;

	/** The log level of this Canary. */
	private static Level logLevel;

	/**
	 * Switch used to indicate that this Canary should write to the application
	 * logs.
	 */
	private static boolean writeToApplicationLogs;

	/**
	 * Switch used to indicate that this Canary should write to standard output.
	 */
	private static boolean writeToStandardOutput;

	/**
	 * The maximum number of characters that this Canary will represent in any
	 * entry written to the application logs or standard output. Defined in
	 * order to obviate the prospect of Canary acting as a CPU hog during the
	 * output of extremely long lines.
	 */
	private static int maximumRepresentationCharacters;

	static {

		InputStream input = null;

		try {

			Properties properties = new Properties();
			input = Canary.class.getResourceAsStream("/canary.properties");
			properties.load(input);

			String logLevel = properties.getProperty("canary.logLevel");
			String writeToApplicationLogs = properties
					.getProperty("canary.writeToApplicationLogs");
			String writeToStandardOutput = properties
					.getProperty("canary.writeToStandardOutput");
			String maximumRepresentationCharacters = properties
					.getProperty("canary.maximumRepresentationCharacters");

			Level parsedLogLevel = null;

			if (logLevel == null) {

				System.out
						.println("canary.logLevel property must be one of [all, trace, debug, info, warn, error, fatal, off] - defaulted to trace");

				parsedLogLevel = Level.TRACE;

			} else if (logLevel.equals("all")) {

				parsedLogLevel = Level.ALL;

			} else if (logLevel.equals("trace")) {

				parsedLogLevel = Level.TRACE;

			} else if (logLevel.equals("debug")) {

				parsedLogLevel = Level.DEBUG;

			} else if (logLevel.equals("info")) {

				parsedLogLevel = Level.INFO;

			} else if (logLevel.equals("warn")) {

				parsedLogLevel = Level.WARN;

			} else if (logLevel.equals("error")) {

				parsedLogLevel = Level.ERROR;

			} else if (logLevel.equals("fatal")) {

				parsedLogLevel = Level.FATAL;

			} else if (logLevel.equals("off")) {

				parsedLogLevel = Level.OFF;

			} else {

				System.out
						.println("canary.logLevel property must be one of [all, trace, debug, info, warn, error, fatal, off] - defaulted to trace");

				parsedLogLevel = Level.TRACE;

			}

			boolean parsedWriteToApplicationLogs = true;

			if (writeToApplicationLogs == null) {

				System.out
						.println("canary.writeToApplicationLogs property must be one of [true, false] - defaulted to true");

			} else if (writeToApplicationLogs.equals("true")) {

			} else if (writeToApplicationLogs.equals("false")) {

				parsedWriteToApplicationLogs = false;

			} else {

				System.out
						.println("canary.writeToApplicationLogs property must be one of [true, false] - defaulted to true");

			}

			boolean parsedWriteToStandardOutput = true;

			if (writeToStandardOutput == null) {

				System.out
						.println("canary.writeToStandardOutput property must be one of [true, false] - defaulted to true");

			} else if (writeToStandardOutput.equals("true")) {

			} else if (writeToStandardOutput.equals("false")) {

				parsedWriteToStandardOutput = false;

			} else {

				System.out
						.println("canary.writeToStandardOutput property must be one of [true, false] - defaulted to true");

			}

			int parsedMaximumRepresentationCharacters = 200;

			if (maximumRepresentationCharacters == null) {

				System.out
						.println("canary.maximumRepresentationCharacters property must be a positive integer such as 0, 100 or 1000 - defaulted to 200");

			} else {

				try {

					parsedMaximumRepresentationCharacters = Integer
							.parseInt(maximumRepresentationCharacters);

				} catch (NumberFormatException error) {

					System.out
							.println("canary.maximumRepresentationCharacters property must be a positive integer such as 0, 100 or 1000 - defaulted to 200");

				}

			}

			Canary.maximumRepresentationCharacters = parsedMaximumRepresentationCharacters;

			initialise(parsedLogLevel, parsedWriteToApplicationLogs,
					parsedWriteToStandardOutput);

		} catch (Exception expected) {

			// mute this canary if there is no canary properties file
			initialise(Level.OFF, false, false);

		} finally {

			try {

				if (input != null) {

					input.close();

				}

			} catch (Exception error) {

				error.printStackTrace();

			}

		}

	}

	/**
	 * Initialises this Canary that will write to the application logs if the
	 * specified switch is true and the application log level is less severe or
	 * equal to the specified log level and that will write to standard output,
	 * if the application log level is less severe or equal to the specified log
	 * level, if the specified switch is true.
	 * 
	 * @param logLevel
	 *            the log level of this Canary.
	 * @param writeToApplicationLogs
	 *            true if this Canary should write to the application logs,
	 *            false otherwise.
	 * @param writeToStandardOutput
	 *            true if this Canary should write to standard output, false
	 *            otherwise.
	 */
	private static void initialise(Level logLevel,
			boolean writeToApplicationLogs, boolean writeToStandardOutput) {

		if (writeToApplicationLogs) {

			Level applicationLogLevel = Logger.getRootLogger().getLevel();

			// do not write to the logs by default
			writeToApplicationLogs = false;

			if (applicationLogLevel == null) {

				// never write to the logs if the log level is not configured

			} else if (applicationLogLevel == Level.OFF
					|| logLevel == Level.OFF) {

				// never write to the logs if the log level is 'off'

			} else if (applicationLogLevel == Level.ALL
					|| logLevel == Level.ALL) {

				// always write to the logs if the log level is 'all'
				writeToApplicationLogs = true;

			} else if (applicationLogLevel == Level.TRACE) {

				if (logLevel == Level.TRACE || logLevel == Level.DEBUG
						|| logLevel == Level.INFO || logLevel == Level.WARN
						|| logLevel == Level.ERROR || logLevel == Level.FATAL) {

					writeToApplicationLogs = true;

				}

			} else if (applicationLogLevel == Level.DEBUG) {

				if (logLevel == Level.DEBUG || logLevel == Level.INFO
						|| logLevel == Level.WARN || logLevel == Level.ERROR
						|| logLevel == Level.FATAL) {

					writeToApplicationLogs = true;

				}

			} else if (applicationLogLevel == Level.INFO) {

				if (logLevel == Level.INFO || logLevel == Level.WARN
						|| logLevel == Level.ERROR || logLevel == Level.FATAL) {

					writeToApplicationLogs = true;

				}

			} else if (applicationLogLevel == Level.WARN) {

				if (logLevel == Level.WARN || logLevel == Level.ERROR
						|| logLevel == Level.FATAL) {

					writeToApplicationLogs = true;

				}

			} else if (applicationLogLevel == Level.ERROR) {

				if (logLevel == Level.ERROR || logLevel == Level.FATAL) {

					writeToApplicationLogs = true;

				}

			} else if (applicationLogLevel == Level.FATAL) {

				if (logLevel == Level.FATAL) {

					writeToApplicationLogs = true;

				}

			}

		}

		Canary.logLevel = logLevel;
		Canary.writeToApplicationLogs = writeToApplicationLogs;
		Canary.writeToStandardOutput = writeToStandardOutput;

		if (writeToApplicationLogs || writeToStandardOutput) {

			muted = null;

		} else {

			muted = true;

		}

	}

	/**
	 * Writes a representation of the specified variable with the specified
	 * variable identifier to the application logs using the logger of this
	 * Canary, in the absence of a specified logger, and the log level of this
	 * Canary, and also optionally writes the representation to standard output,
	 * if the application log level is less severe or equal to the log level of
	 * this Canary.
	 * 
	 * @param identifier
	 *            the identifier.
	 * @param variable
	 *            the variable.
	 */
	public static void output(String identifier, Object variable) {

		output(identifier, variable, null);

	}

	/**
	 * Writes a representation of the specified variable with the specified
	 * variable identifier to the application logs using the specified logger
	 * and the log level of this Canary, and also optionally writes the
	 * representation to standard output, if the application log level is less
	 * severe or equal to the log level of this Canary.
	 * 
	 * @param identifier
	 *            the identifier.
	 * @param variable
	 *            the variable.
	 * @param logger
	 *            the logger.
	 */
	public static void output(String identifier, Object variable, Logger logger) {

		if (muted == null) {

			try {

				VariableDecorator variableDecorator = VariableDecoratorStrategy
						.createDecoratorForVariableType(variable,
								maximumRepresentationCharacters);

				String variableRepresentation = variableDecorator
						.representVariable();

				StringBuilder representation = new StringBuilder();

				representation.append(identifier);
				representation.append(": ");
				representation.append(variableRepresentation);

				outputWithoutNotMutedAssertion(representation.toString(),
						logger);

			} catch (Exception error) {

				error.printStackTrace();

			}

		}

	}

	/**
	 * Writes the specified text to the application logs using the logger of
	 * this Canary, in the absence of a specified logger, and the log level of
	 * this Canary, and also optionally writes the text to standard output, if
	 * the application log level is less severe or equal to the log level of
	 * this Canary.
	 * 
	 * @param text
	 *            the text.
	 */
	public static void output(String text) {

		output(text, null);

	}

	/**
	 * Writes the specified text to the application logs using the specified
	 * logger and the log level of this Canary, and also optionally writes the
	 * text to standard output, if the application log level is less severe or
	 * equal to the log level of this Canary.
	 * 
	 * @param text
	 *            the text.
	 * @param logger
	 *            the logger.
	 */
	public static void output(String text, Logger logger) {

		if (muted == null) {

			try {

				outputWithoutNotMutedAssertion(text, logger);

			} catch (Exception error) {

				error.printStackTrace();

			}

		}

	}

	/**
	 * Asserts that this Canary may write to the application logs and also
	 * optionally write to standard output. Used to avoid performing additional
	 * work, such as building strings, in the calling component before invoking
	 * an output method on this Canary similar to the usage of isTraceEnabled()
	 * in log4j applications.
	 * 
	 * @return true if this Canary may write output, false otherwise.
	 */
	public static boolean isOutputEnabled() {

		if (muted == null) {

			return true;

		}

		return false;

	}

	/**
	 * Mutes this Canary such that it will never write to the application logs
	 * or standard output.
	 */
	public static void mute() {

		muted = true;

	}

	/**
	 * Unmutes this Canary such that it will write to the application logs or
	 * standard output depending on the initial configuration.
	 */
	public static void unmute() {

		muted = null;

	}

	/**
	 * Writes the specified text to the application logs using the specified
	 * logger, or the logger of this Canary if the logger is null, and the log
	 * level of this Canary and also optionally writes the text to standard
	 * output without asserting that this Canary is not muted.
	 * 
	 * @param text
	 *            the text.
	 * @param logger
	 *            the logger.
	 */
	private static void outputWithoutNotMutedAssertion(String text,
			Logger logger) {

		if (text.length() > maximumRepresentationCharacters) {

			text = text.substring(0, maximumRepresentationCharacters);
			text = text + "...";

		}

		if (writeToStandardOutput) {

			String time = DATE_FORMAT.format(new GregorianCalendar().getTime());

			int stackIndex = 3;

			if (logger == null) {

				stackIndex = 4;

			}

			StackTraceElement element = Thread.currentThread().getStackTrace()[stackIndex];

			String clazz = element.getClassName();
			String method = element.getMethodName();
			int line = element.getLineNumber();

			System.out.print(time);
			System.out.print(" ");
			System.out.print(clazz);
			System.out.print(".");
			System.out.print(method);
			System.out.print(".");
			System.out.print(line);
			System.out.print(": ");
			System.out.print(text);

			System.out.println();

		}

		if (logger == null) {

			logger = Canary.logger;

		}

		if (writeToApplicationLogs) {

			if (logLevel == Level.DEBUG) {

				logger.debug(text);

			} else if (logLevel == Level.INFO) {

				logger.info(text);

			} else if (logLevel == Level.WARN) {

				logger.warn(text);

			} else if (logLevel == Level.ERROR) {

				logger.error(text);

			} else if (logLevel == Level.FATAL) {

				logger.fatal(text);

			} else {

				// use the 'trace' log level by default
				logger.trace(text);

			}

		}

	}

}