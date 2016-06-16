package com.maptool.lbs;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.maptool.common.Const;
import com.maptool.common.L;

/**
 * @see http://developer.baidu.com/map/index.php?title=lbscloud/api/geodata
 * @author mazhibin
 */
public class BaiduLBS {
	private static final String TAG = "BaiduLBS";
	protected static String ak =  Const.ak;
	protected static String sk = Const.sk;
	protected static boolean _debug = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BaiduLBS b = new BaiduLBS();
		// System.out.println(b.listTable(""));
		// log(b.tableDetail(100274));
		// log(b.createTable("尼玛", 1, 1));
		// log(b.updateTable(100274, 1, "马志彬"));
		// log(b.listColumn(99431));
		// log(b.listColumn(100274));
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "just soso");
//		jsonLog(b.createData(99431, "title", 109.494184d, 18.295093d, 1, params));
		// jsonLog(b.listData(99431,params));
//		 jsonLog(b.updateData(99431, 769962368, params));
//		jsonLog(b.deleteData(99431, 770853422, null));
		
//		jsonLog(b.nearbySearch(100400, 50000, "116.440166,39.910638"));
	}

	/**
	 * 创建表（create geotable）接口
	 * http://api.map.baidu.com/geodata/v3/geotable/create //POST请求
	 * 
	 * @param name
	 * @param geotype
	 * @param is_published
	 * @return
	 */
	public String createTable(String name, int geotype, int is_published) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (name != null && name.equals("") == false) {
			params.add(new BasicNameValuePair("name", name));
		} else {
			log("createTable：缺少name参数");
		}
		params.add(new BasicNameValuePair("geotype", "" + geotype));
		params.add(new BasicNameValuePair("is_published", "" + is_published));
		return _post("http://api.map.baidu.com", "/geodata/v3/geotable/create", params);
	}

	/**
	 * 创建数据（create poi）接口 http://api.map.baidu.com/geodata/v3/poi/create //
	 * POST请求
	 */
	public String createData(int geotable_id, String title, double latitude, double longitude, int coord_type, Map<String, String> customParams) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("geotable_id", "" + geotable_id));
		if (title != null && title.equals("") == false) {
			params.add(new BasicNameValuePair("title", title));
		}
		params.add(new BasicNameValuePair("latitude", "" + latitude));
		params.add(new BasicNameValuePair("longitude", "" + longitude));
		params.add(new BasicNameValuePair("coord_type", "" + coord_type));
		if (customParams != null) {
			for (Entry<String, String> entry : customParams.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		return _post("http://api.map.baidu.com", "/geodata/v3/poi/create", params);
	}

	/**
	 * 查询指定条件的数据（poi）列表接口 http://api.map.baidu.com/geodata/v3/poi/list // GET请求
	 */
	public String listData(int geotable_id, Map<String, String> condition) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("geotable_id", "" + geotable_id));
		if (condition != null) {
			for (Entry<String, String> entry : condition.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		return _get("http://api.map.baidu.com", "/geodata/v3/poi/list", params);
	}

	/**
	 * 修改数据（poi）接口 http://api.map.baidu.com/geodata/v3/poi/update // POST请求
	 */
	public String updateData(int geotable_id, int id, Map<String, String> custom) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("geotable_id", "" + geotable_id));
		params.add(new BasicNameValuePair("id", "" + id));
		if (custom != null) {
			for (Entry<String, String> entry : custom.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		return _post("http://api.map.baidu.com", "/geodata/v3/poi/update", params);
	}

	/**
	 * 删除数据（poi）接口（支持批量） http://api.map.baidu.com/geodata/v3/poi/delete //
	 * POST请求
	 */
	public String deleteData(int geotable_id, int id, Map<String, String> custom) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("geotable_id", "" + geotable_id));
		params.add(new BasicNameValuePair("id", "" + id));
		if (custom != null) {
			for (Entry<String, String> entry : custom.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		return _post("http://api.map.baidu.com", "/geodata/v3/poi/delete", params);
	}
	
	/**
	 * poi周边搜索
	 * http://api.map.baidu.com/geosearch/v3/nearby // GET请求
	 */
	public String nearbySearch(int geotable_id,int radius,double longitude,double latitude){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("geotable_id", "" + geotable_id));
		params.add(new BasicNameValuePair("radius", "" + radius));
		params.add(new BasicNameValuePair("location", longitude+","+latitude));
		
		return _get("http://api.map.baidu.com", "/geosearch/v3/nearby", params);
	}

	/**
	 * 查询表（list geotable）接口 http://api.map.baidu.com/geodata/v3/geotable/list //
	 * GET请求
	 * 
	 * @param name
	 * @return
	 */
	public String listTable(String name) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (name != null && name.equals("") == false) {
			params.add(new BasicNameValuePair("name", name));
		}
		String jsonStr = _get("http://api.map.baidu.com", "/geodata/v3/geotable/list", params);
		return jsonStr;
	}

	/**
	 * 查询指定id表（detail geotable）接口
	 * http://api.map.baidu.com/geodata/v3/geotable/detail // GET请求
	 * 
	 * @param id
	 * @return
	 */
	public String tableDetail(int id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", "" + id));
		String jsonStr = _get("http://api.map.baidu.com", "/geodata/v3/geotable/detail", params);
		return jsonStr;
	}

	/**
	 * 修改表名（update geotable）接口
	 * http://api.map.baidu.com/geodata/v3/geotable/update // POST请求
	 */
	public String updateTable(int id, int is_published, String name) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (name != null && name.equals("") == false) {
			params.add(new BasicNameValuePair("name", name));
		} else {
			log("createTable：缺少name参数");
		}
		params.add(new BasicNameValuePair("id", "" + id));
		params.add(new BasicNameValuePair("is_published", "" + is_published));
		return _post("http://api.map.baidu.com", "/geodata/v3/geotable/update", params);
	}

	/**
	 * 查询列（list column）接口 http://api.map.baidu.com/geodata/v3/column/list //
	 * GET请求
	 */
	public String listColumn(int geotable_id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("geotable_id", "" + geotable_id));
		String jsonStr = _get("http://api.map.baidu.com", "/geodata/v3/column/list", params);
		return jsonStr;
	}

	/**
	 * 
	 * @param baseUrl
	 *            不带问号，最后不带/
	 * @param params
	 * @return
	 */
	protected static String _get(String site, String baseUrl, List<NameValuePair> params) {
		params.add(new BasicNameValuePair("ak", ak));
		String sn = getSn(baseUrl, params);
		params.add(new BasicNameValuePair("sn", sn));

		String url = site + baseUrl + "?" + toQueryString(params);
		log("get url: " + url);
		log("get sn : " + sn);

		HttpGet httpGet = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = response.getEntity().getContent();
				String jsonStr = readString(in);
				return jsonStr;
			}
		} catch (ClientProtocolException e) {
			Log.e("mzb",e.getLocalizedMessage());
		} catch (IOException e) {
			Log.e("mzb",e.getLocalizedMessage());
		}
		return "";
	}

	protected static String _post(String site, String baseUrl, List<NameValuePair> params) {
		params.add(new BasicNameValuePair("ak", ak));
		String sn = getSn(baseUrl, params);
		params.add(new BasicNameValuePair("sn", sn));

		log("post sn: " + sn);

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost httpPost = new HttpPost(site + baseUrl);
			httpPost.setEntity(entity);

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = response.getEntity().getContent();
				String jsonStr = readString(in);
				return jsonStr;
			}
		} catch (UnsupportedEncodingException e) {
			L.e(e.getLocalizedMessage());
		} catch (ClientProtocolException e) {
			L.e(e.getLocalizedMessage());
		} catch (IOException e) {
			L.e(e.getLocalizedMessage());
		}
		return "";
	}

	protected static String getSn(String baseUrl, List<NameValuePair> params) {
		String paramsStr = toQueryString(params);
		String wholeStr = new String(baseUrl + "?" + paramsStr + sk);
		log("sn str: " + wholeStr);
		String tempStr = "";
		try {
			tempStr = URLEncoder.encode(wholeStr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			L.e(e.getLocalizedMessage());
		}
		return MD5(tempStr);

	}

	protected static String readString(InputStream in) throws IOException {
		byte[] data = new byte[1024];
		int length = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((length = in.read(data)) != -1) {
			bout.write(data, 0, length);
		}
		return new String(bout.toByteArray(), "UTF-8");
	}

	protected static String toQueryString(List<NameValuePair> params) {
		// 对参数按参数名的字母顺序排序 post的sn计算需要如此
		Collections.sort(params, new Comparator<NameValuePair>() {
			public int compare(NameValuePair o1, NameValuePair o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		StringBuffer sb = new StringBuffer();
		for (NameValuePair pair : params) {
			sb.append(pair.getName() + "=");
			try {
				sb.append(URLEncoder.encode(pair.getValue(), "UTF-8") + "&");
			} catch (UnsupportedEncodingException e) {
				L.e(e.getLocalizedMessage());
			}
		}
		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			L.e(e.getLocalizedMessage());
		}
		return null;
	}
	
	protected static JSONObject json(String jsonstr) throws JSONException{
		L.d("jsonStr: "+jsonstr);
		return new JSONObject(jsonstr);
	}

	protected static void log(String content) {
		if (_debug)
			Log.e(TAG, content);
	}

//	protected static void jsonLog(String jsonStr) {
//		if (_debug) {
//			JSONObject o = new JSONObject(jsonStr);
//			System.out.println(o);
//		}
//	}
}
