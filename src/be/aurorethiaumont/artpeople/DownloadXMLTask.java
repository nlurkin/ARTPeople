package be.aurorethiaumont.artpeople;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadXMLTask extends AsyncTask<String, Void, List<Contact>>{

	private static final String SOAP_ACTION = "http://ART_WEB_Services/ExportContacts";
	private static final String METHOD_NAME = "ExportContacts";
	private static final String NAMESPACE = "http://ART_WEB_Services/";
	private static final String URL = "http://www.aurore-thiaumont.be/ART_Web_Services.asmx";
	private static final String LOCAL_FILENAME = "artContacts.xml";

	DataFetchedListener mListener;
	Activity parent;
	int error = R.string.err_unknown;

	public interface DataFetchedListener{
		public void newDataFetched(List<Contact> l);
		public void errorWhileFetchingData(int err);
	}

	public void onPreExecute(){
		mListener = (DataFetchedListener)parent;
	}

	@Override
	protected List<Contact> doInBackground(String... urls){
		try {
			if(urls[0].contains("http")){
				return loadXmlFromNetwork(urls[1], urls[2]);
			}
			else{
				return loadXmlFromLocal(urls[0]);
			}
		} catch (XmlPullParserException e) {
			error = R.string.err_xmlparsing;
		} catch (FileNotFoundException e) {
			error = R.string.err_file_not_found;
		} catch (ARTConnectionRefused e) {
			error = R.string.err_wrong_credentials;
		} catch (NetworkErrorException e) {
			error = R.string.err_network_unreachable;
		} catch (IOException e) {
			error = R.string.err_io_exception;
		}
		return null;
	}

	protected void onPostExecute(List<Contact> l){
		if(l != null){
			mListener.newDataFetched(l);
		}
		else{
			mListener.errorWhileFetchingData(error);
		}
	}

	private List<Contact> loadXmlFromLocal(String fileName) throws XmlPullParserException, FileNotFoundException, IOException{
		FileInputStream str;
		artXMLParser parser;

		parser = new artXMLParser();
		str = parent.openFileInput(fileName);
		if(str != null) return parser.parse(str);
		else return null;
	}

	private List<Contact> loadXmlFromNetwork(String login, String password) throws XmlPullParserException, IOException, NetworkErrorException, ARTConnectionRefused {
		InputStream stream = null;
		// Instantiate the parser
		artXMLParser parser = new artXMLParser();
		List<Contact> entries = null;

		try {
			stream = sendCredentials(login, password);
			if(stream != null){
				entries = parser.parse(stream);
				writeDataToXML(LOCAL_FILENAME, entries);
			}
		} finally {
			if (stream != null) {
				stream.close();
			} 
		}

		return entries;
	}

	public InputStream sendCredentials(String login, String password) throws NetworkErrorException, ARTConnectionRefused{
		InputStream is = null;
		SoapObject request;
		SoapSerializationEnvelope envelope;
		HttpTransportSE androidHttpTransport;
		String res;

		try {
			request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("login",login);
			request.addProperty("password",password);

			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet=true;
			envelope.setOutputSoapObject(request);

			androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(SOAP_ACTION, envelope);

			Log.d("DEBUG", ((SoapPrimitive)envelope.getResponse()).toString());
			res = ((SoapPrimitive)envelope.getResponse()).toString();
			if(res.contains("ConnectionRefused")) throw new ARTConnectionRefused();
			is = new ByteArrayInputStream(res.getBytes("UTF-8"));
		} catch (IOException e) {
			throw new NetworkErrorException();
		} catch (XmlPullParserException e) {
			throw new NetworkErrorException();
		}

		return is;
	}

	public void writeDataToXML(String fileName, List<Contact> contactList) throws FileNotFoundException, IOException{
		FileOutputStream outputStream = null;

		try{
			outputStream = parent.openFileOutput(fileName, Context.MODE_PRIVATE);
			
			outputStream.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes());
			outputStream.write("<ContactList>".getBytes());
			for(Contact c : contactList){
				outputStream.write("<Contact>".getBytes());
				outputStream.write(("<Name>" + c.name + "</Name>").getBytes());
				outputStream.write(("<FirstName>" + c.first_Name + "</FirstName>").getBytes());
				for(String phone : c.phone){
					outputStream.write(("<Phone>" + phone + "</Phone>").getBytes());
				}
				for(String mail : c.email){
					outputStream.write(("<Email>" + mail + "</Email>").getBytes());
				}
				outputStream.write("</Contact>".getBytes());
			}
			outputStream.write("</ContactList>".getBytes());
		} finally {
			if(outputStream != null){
				outputStream.close();
			}
		}
	}

}
