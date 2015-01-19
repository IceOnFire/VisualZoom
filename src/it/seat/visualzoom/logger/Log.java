package it.seat.visualzoom.logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;

import javax.swing.JOptionPane;

public class Log {

	private static Log log;

	private Logger logger;
	private MemoryHandler memoryHandler;
	private ConsoleHandler consoleHandler;
	private FileHandler fileHandler;

	public Log() throws SecurityException, IOException {
		logger = Logger.getLogger("zoom.logger");
		logger.setLevel(Level.ALL);
		fileHandler = new FileHandler("log.xml");
		fileHandler.setEncoding("UTF-8");
		memoryHandler = new MemoryHandler(fileHandler, 64, Level.SEVERE);
		memoryHandler.setLevel(Level.INFO);
		consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		logger.addHandler(memoryHandler);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
	}

	private static Log getInstance() {
		if (log == null) {
			try {
				log = new Log();
			} catch (Exception exc) {
				exc.printStackTrace();
				JOptionPane.showMessageDialog(null, exc.toString(),
						"Fatal error, application will exit...",
						JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		}
		return log;
	}

	public static Logger getLogger() {
		return getInstance().logger;
	}

	public static void main(String[] args) {
		getLogger().info("INFO_PROVA1");
		getLogger().info("INFO_PROVA2");
		getLogger().info("INFO_PROVA3");
		getLogger().info("INFO_PROVA4");
		getLogger().info("INFO_PROVA5");
		getLogger().info("INFO_PROVA6");
		getLogger().severe("ERROR_PROVA");
		getLogger().info("INFO_PROVA7");
		getLogger().info("INFO_PROVA8");
		getLogger().info("INFO_PROVA9");
	}

}
