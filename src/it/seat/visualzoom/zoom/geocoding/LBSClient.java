/*
 * Creato il 10-apr-2007
 */
package it.seat.visualzoom.zoom.geocoding;

import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.zoom.utils.ProxyPrefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Leo
 * 
 */
public class LBSClient {

	private URL endpoint;
	private ProxyPrefs proxyPrefs;

	/**
	 * @param endpoint
	 */
	public LBSClient(URL endpoint) {
		super();
		this.endpoint = endpoint;
		this.proxyPrefs = ProxyPrefs.getInstance();
	}

	public LBSResponse getLocations(String reg, String prov, String com,
			String fraz, String topo, String civ, String gst,
			String open_search, String tipo_search) throws GeocodingException {
		try {
			HttpURLConnection conn = (HttpURLConnection) endpoint
					.openConnection(proxyPrefs.getSelectedProxy());
			Log.getLogger().finer(
					"Proxy selected: " + proxyPrefs.getSelectedProxy());
			Log.getLogger().finer(
					"Proxy Auth String: " + proxyPrefs.getProxyAuthString());
			if (proxyPrefs.isAuthRequired()) {
				conn.setRequestProperty("Proxy-Authorization", proxyPrefs
						.getProxyAuthString());
			}
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			String query = "reg=" + (reg != null ? reg : "") + "&prov="
					+ (prov != null ? prov : "") + "&com="
					+ (com != null ? com : "") + "&fraz="
					+ (fraz != null ? fraz : "") + "&topo="
					+ (topo != null ? topo : "") + "&civ="
					+ (civ != null ? civ : "") + "&gst="
					+ (gst != null ? gst : "") + "&open_search="
					+ (open_search != null ? open_search : "")
					+ "&tipo_search="
					+ (tipo_search != null ? tipo_search : "");
			byte[] postData = query.getBytes();
			conn.setFixedLengthStreamingMode(postData.length);
			conn.getOutputStream().write(postData);
			conn.connect();
			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputStream is = conn.getInputStream();
			Document doc = parser.parse(is);
			is.close();
			return doc2Locations(doc, topo, civ);// passo anche il civico
													// perch� lbs a volte non me
													// lo da.
		} catch (IOException e) {
			throw new GeocodingException("Errore di connessione", e);
		} catch (ParserConfigurationException e) {
			throw new GeocodingException(
					"Impossibile istanziare il parser XML", e);
		} catch (SAXException e) {
			throw new GeocodingException(
					"Parsing non completato correttamente", e);
		}
	}

	public LBSResponse getLocations(String com) throws GeocodingException {
		return getLocations(null, null, com, null, null, null, null, null, null);
	}

	public LBSResponse getLocations(String prov, String com)
			throws GeocodingException {
		return getLocations(null, prov, com, null, null, null, null, null, null);
	}

	public Location getLocationByCode(String codCom, String codFraz)
			throws GeocodingException {
		LBSResponse result = getLocations(null, null, codCom, codFraz, null,
				null, null, null, null);
		if (result.getType() == LBSResponse.TYPE_OK)
			return result.getLocations()[0];
		else if (result.getType() == LBSResponse.TYPE_COMUNE_NON_UNIVOCO)
			throw new GeocodingException(
					"La ricerca per codice non � univoca !!!");
		else
			throw new GeocodingException("Comune non trovato");
	}

	public LBSResponse getLocations(String com, String address, String civ)
			throws GeocodingException {
		// usa la ricerca come nel visual, con il comune in open_search e
		// tipo_search=-1
		return getLocations(null, null, null, null, address, civ, null, com,
				"-1");
	}

	public LBSResponse getAddressList(String codCom, String codFraz,
			String address) throws GeocodingException {
		return getLocations(null, null, codCom, codFraz, address, null, null,
				null, null);
	}

	private LBSResponse doc2Locations(Document doc, String topo, String civico)
			throws NumberFormatException {
		Element root = doc.getDocumentElement();
		String resultCode = root.getElementsByTagName("outcod").item(0)
				.getTextContent();
		boolean ok = resultCode.equals("0");
		boolean moreCities = resultCode.equals("10");
		boolean moreAddress = resultCode.equals("2");
		if (ok)
			return LBSResponse.newResponseOK(parseResultOK(doc));
		else if (moreCities)
			return LBSResponse
					.newResponseComuneNonUnivoco(parseResultComuneNonUnivoco(doc));
		else if (moreAddress)
			return LBSResponse
					.newResponseIndirizzoNonUnivoco(parseResultIndirizzoNonUnivoco(
							doc, topo, civico));
		else
			return LBSResponse.newResponseNessunRisultato();
	}

	private Location[] parseResultOK(Document xml) {
		Element root = xml.getDocumentElement();
		// Codici
		Element addrCodes = (Element) root.getElementsByTagName("Addr_Codes")
				.item(0);
		String codCom = addrCodes.getElementsByTagName("cod_comune").item(0)
				.getTextContent();
		NodeList codFrazList = addrCodes.getElementsByTagName("cod_frazione");
		String codFraz = "";
		if (codFrazList != null && codFrazList.getLength() > 0) {
			codFraz = codFrazList.item(0).getTextContent();
		}
		// Nomi e coordinate
		Element addrCenter = (Element) root.getElementsByTagName("Addr_Center")
				.item(0);
		String ics = addrCenter.getElementsByTagName("ics").item(0)
				.getTextContent();
		String ipsilon = addrCenter.getElementsByTagName("ipsilon").item(0)
				.getTextContent();
		String prov = addrCenter.getElementsByTagName("prov").item(0)
				.getTextContent();
		String com = addrCenter.getElementsByTagName("com").item(0)
				.getTextContent();
		String fraz = "";
		NodeList frazList = addrCenter.getElementsByTagName("fraz");
		if (frazList != null && frazList.getLength() > 0) {
			fraz = frazList.item(0).getTextContent();
		}
		String addr = "";
		NodeList addrList = addrCenter.getElementsByTagName("topo");
		if (addrList != null && addrList.getLength() > 0) {
			addr = addrList.item(0).getTextContent();
		} else {
			// non c'� il tag topo, cerco c_topo
			addrList = addrCenter.getElementsByTagName("c_topo");
			if (addrList != null && addrList.getLength() > 0) {
				addr = addrList.item(0).getTextContent();
			}
		}
		String civ = "";

		NodeList civList = addrCenter.getElementsByTagName("civico");
		if (civList != null && civList.getLength() > 0) {
			civ = civList.item(0).getTextContent();
		} else {
			// non c'� il tag civico, cerco c_civico
			civList = addrCenter.getElementsByTagName("c_civico");
			if (civList != null && civList.getLength() > 0) {
				civ = civList.item(0).getTextContent();
			}
		}
		float lat = Float.parseFloat(ipsilon);
		float lon = Float.parseFloat(ics);
		Location loc = new Location(com, fraz, prov, codCom, codFraz, addr,
				civ, lat, lon);
		return new Location[] { loc };
	}

	private Location[] parseResultComuneNonUnivoco(Document xml) {
		Element root = xml.getDocumentElement();
		Element addrChoice = (Element) root.getElementsByTagName("Addr_Choice")
				.item(0);
		NodeList topoAddrU = addrChoice.getElementsByTagName("Topo_Addr_U");
		Location[] list = new Location[topoAddrU.getLength()];
		for (int i = 0; i < topoAddrU.getLength(); i++) {
			Element addr = (Element) topoAddrU.item(i);
			String codCom = addr.getElementsByTagName("cod_com").item(0)
					.getTextContent();
			NodeList codFrazList = addr.getElementsByTagName("cod_fraz");
			String codFraz = "";
			if (codFrazList != null && codFrazList.getLength() > 0) {
				codFraz = codFrazList.item(0).getTextContent();
			}
			String com = addr.getElementsByTagName("com").item(0)
					.getTextContent();
			String fraz = "";
			NodeList frazList = addr.getElementsByTagName("fraz");
			if (frazList != null && frazList.getLength() > 0) {
				fraz = frazList.item(0).getTextContent();
			}
			NodeList addrList = addr.getElementsByTagName("c_topo");
			String address = "";
			if (addrList != null && addrList.getLength() > 0) {
				address = addrList.item(0).getTextContent();
			}
			String civ = "";
			NodeList civList = addr.getElementsByTagName("c_civico");
			if (civList != null && civList.getLength() > 0) {
				civ = civList.item(0).getTextContent();
			}
			String prov = addr.getElementsByTagName("prov").item(0)
					.getTextContent();
			Location loc = new Location(com, fraz, prov, codCom, codFraz,
					address, civ);
			list[i] = loc;
		}
		return list;
	}

	private Location[] parseResultIndirizzoNonUnivoco(Document xml,
			String topo, String civico) {
		Element root = xml.getDocumentElement();
		Element addrCodes = (Element) root.getElementsByTagName("Addr_Codes")
				.item(0);
		String codCom = addrCodes.getElementsByTagName("cod_comune").item(0)
				.getTextContent();
		NodeList codFrazList = addrCodes.getElementsByTagName("cod_frazione");
		String codFraz = "";
		if (codFrazList != null && codFrazList.getLength() > 0) {
			codFraz = codFrazList.item(0).getTextContent();
		}
		Element addrCenter = (Element) root.getElementsByTagName("Addr_Center")
				.item(0);
		// String ics =
		// addrCenter.getElementsByTagName("ics").item(0).getTextContent();
		// String ipsilon =
		// addrCenter.getElementsByTagName("ipsilon").item(0).getTextContent();
		String prov = addrCenter.getElementsByTagName("prov").item(0)
				.getTextContent();
		String com = addrCenter.getElementsByTagName("com").item(0)
				.getTextContent();
		String fraz = "";
		NodeList frazList = addrCenter.getElementsByTagName("fraz");
		if (frazList != null && frazList.getLength() > 0) {
			fraz = frazList.item(0).getTextContent();
		}
		Element addrChoice = (Element) root.getElementsByTagName("Addr_Choice")
				.item(0);
		NodeList topoAddrU = addrChoice.getElementsByTagName("Topo_Addr_U");
		Location[] list = new Location[topoAddrU.getLength()];
		for (int i = 0; i < topoAddrU.getLength(); i++) {
			NodeList addrList = ((Element) topoAddrU.item(i))
					.getElementsByTagName("toponimo");
			String address = topo;
			if (addrList != null && addrList.getLength() > 0) {
				address = addrList.item(0).getTextContent();
			}
			String civ = civico;
			NodeList civList = addrChoice.getElementsByTagName("civico");
			if (civList != null && civList.getLength() > 0) {
				civ = civList.item(0).getTextContent();
			}
			Location loc = new Location(com, fraz, prov, codCom, codFraz,
					address, civ);
			list[i] = loc;
		}
		return list;
	}

	public static void main(String[] args) throws MalformedURLException,
			GeocodingException {
		LBSClient lbs = new LBSClient(new URL(
				"http://lbs.tuttocitta.it/WS_TCOL.asmx/Addr_Map_U"));
		Location[] loc = lbs.getLocations(null, "cassolnovo").getLocations();
		for (int i = 0; i < loc.length; i++) {
			System.out.println(loc[i]);
		}
		System.out.println("--------------------------------------");
		System.out.println(lbs.getLocationByCode("59060", "6274"));

	}
}
