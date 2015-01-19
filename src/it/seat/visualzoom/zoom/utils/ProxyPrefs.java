package it.seat.visualzoom.zoom.utils;

import it.seat.visualzoom.logger.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ProxyPrefs {

	static ProxyPrefs my_instance;

	public static final String PROXY_SET = "zoom.settings.proxy.set";
	public static final String PROXY_HOST = "zoom.settings.proxy.host";
	public static final String PROXY_PORT = "zoom.settings.proxy.port";
	public static final String PROXY_AUTH_REQUIRED = "zoom.settings.proxy.auth";
	public static final String PROXY_USER = "zoom.settings.proxy.user";
	public static final String PROXY_PASS = "zoom.settings.proxy.pass";

	Properties prefs;
	Proxy proxy;
	String proxyAuthString = null;
	boolean isAuthRequired = false;

	private ProxyPrefs() {
		prefs = new Properties();
		try {
			prefs.load(new FileInputStream("settings/proxy.prefs"));
			if (Boolean.parseBoolean(prefs.getProperty(ProxyPrefs.PROXY_SET))) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(prefs
						.getProperty(PROXY_HOST), Integer.parseInt(prefs
						.getProperty(PROXY_PORT))));
			} else {
				proxy = Proxy.NO_PROXY;
			}
			buildAuthString();
			isAuthRequired = Boolean.parseBoolean(prefs
					.getProperty(ProxyPrefs.PROXY_AUTH_REQUIRED));
		} catch (IOException e) {
			Log.getLogger().warning(
					"File di preferenze non trovato ! [" + e.toString() + "]");
		} catch (NumberFormatException e) {
			Log.getLogger().warning(
					"La porta HTTP deve essere un intero tra 0 e 65535 ! ["
							+ e.toString() + "]");
			proxy = Proxy.NO_PROXY;
		}
	}

	private void buildAuthString() {
		try {
			String s = prefs.getProperty(ProxyPrefs.PROXY_USER)
					+ ":"
					+ PBECipher.decrypt(prefs
							.getProperty(ProxyPrefs.PROXY_PASS));
			proxyAuthString = "Basic " + Base64.encode(s.getBytes());
		} catch (Exception e) {
			Log.getLogger().severe(
					"Impossibile decifrare la password per il proxy ! ["
							+ e.toString() + "]");
			Log.getLogger().info(
					"Password: " + prefs.getProperty(ProxyPrefs.PROXY_PASS));
			proxyAuthString = "";
		}
	}

	public String get(String key) {
		return prefs.getProperty(key);
	}

	public boolean isAuthRequired() {
		return isAuthRequired;
	}

	public String getProxyAuthString() {
		return proxyAuthString;
	}

	public void set(String key, String value) {
		prefs.setProperty(key, value);
	}

	public Proxy getSelectedProxy() {
		return proxy;
	}

	public void save() {
		try {
			prefs.store(new FileOutputStream("settings/proxy.prefs"),
					"Zoom Properties");
			if (Boolean.parseBoolean(prefs.getProperty(ProxyPrefs.PROXY_SET))) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(prefs
						.getProperty(PROXY_HOST), Integer.parseInt(prefs
						.getProperty(PROXY_PORT))));
			} else {
				proxy = Proxy.NO_PROXY;
			}
			isAuthRequired = Boolean.getBoolean(prefs
					.getProperty(ProxyPrefs.PROXY_AUTH_REQUIRED));
			buildAuthString();
		} catch (IOException e) {
			Log.getLogger().warning(
					"Impossibile trovare il file di preferenze: creo un file vuoto ["
							+ e.toString() + "]");
			File prefsFile = new File("settings/zoom.prefs");
			try {
				if (!prefsFile.exists())
					prefsFile.createNewFile();
			} catch (IOException exc) {
				Log.getLogger().severe(
						"Impossibile creare il file di preferenze ["
								+ exc.toString() + "]");
			}
		} catch (NumberFormatException e) {
			Log.getLogger().warning(
					"La porta HTTP deve essere un intero tra 0 e 65535 ! ["
							+ e.toString() + "]");
			proxy = Proxy.NO_PROXY;
		}
	}

	public static ProxyPrefs getInstance() {
		if (my_instance == null) {
			my_instance = new ProxyPrefs();
		}
		return my_instance;
	}

	public static void main(String[] args) throws IllegalBlockSizeException,
			BadPaddingException, IOException, Base64DecodingException {
		ProxyPrefs prefs = new ProxyPrefs();
		System.out.println(PBECipher.decrypt(prefs.get(ProxyPrefs.PROXY_PASS)));
		HttpURLConnection con = (HttpURLConnection) new URL(
				"http://www.google.com").openConnection(prefs
				.getSelectedProxy());
		con.addRequestProperty("Proxy-Authorization", "Basic "
				+ Base64.encode("llandini:leonardo".getBytes()));
		con.connect();
		con.getInputStream();
		con.disconnect();
	}

}
