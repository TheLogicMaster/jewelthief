package at.therefactory.jewelthief.net;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.misc.Utils;
import at.therefactory.jewelthief.screens.MenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import static javax.crypto.Cipher.ENCRYPT_MODE;

public class HttpServer {
	private static final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
	private static final String wwwhost = Gdx.files.internal("crypt/wwwhost").readString();
	private static final String aeskey = Gdx.files.internal("crypt/aeskey").readString();
	private static final String sharedsecret = Gdx.files.internal("crypt/sharedsecret").readString();

	interface TokenCallback {
		void onToken(String token);
	}

	private static void generateToken (TokenCallback callback) {
		Net.HttpRequest request = requestBuilder.newRequest().method("GET").url("https://worldtimeapi.org/api/timezone/Europe/Berlin").build();
		Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse (Net.HttpResponse httpResponse) {
				try {
					String datetime = new JsonReader().parse(httpResponse.getResultAsString()).getString("datetime");
					String secret = sharedsecret + datetime.substring(8, 10) + '.' + datetime.substring(5, 7) + '.' + datetime.substring(0, 4);
//					callback.onToken(encrypt(secret).toLowerCase());
				callback.onToken(byteArrayToHexString(encrypt(secret.getBytes(), hexStringToByteArray(aeskey))).toLowerCase());
				} catch (Throwable e) {
					Gdx.app.log(HttpServer.class.getName(), "Failed to get date", e);
					callback.onToken(null);
				}
			}

			@Override
			public void failed (Throwable t) {
				Gdx.app.log(HttpServer.class.getName(), "Failed to get date", t);
				callback.onToken(null);
			}

			@Override
			public void cancelled () {
				Gdx.app.log(HttpServer.class.getName(), "Failed to get date");
				callback.onToken(null);
			}
		});
	}

	/*JNI
	#include "plusaes.hpp"
	 */

	private static native byte[] encrypt(byte[] data, byte[] key); /*
		JAVA_ARRAY encrypted = (JAVA_ARRAY)__NEW_ARRAY_JAVA_BYTE(threadStateData, plusaes::get_padded_encrypted_size(37));
		plusaes::encrypt_ecb((unsigned char*)data, 37, (unsigned char*)key, 16, (unsigned char*)encrypted->data, encrypted->length, true);
		return (JAVA_OBJECT)encrypted;
	*/

//	private static String encrypt(String string) {
//		try {
//			SecretKeySpec secretKeySpec = new SecretKeySpec(hexStringToByteArray(aeskey), "AES");
//			Cipher cipher = Cipher.getInstance("AES");
//			cipher.init(ENCRYPT_MODE, secretKeySpec, cipher.getParameters());
//			return byteArrayToHexString(cipher.doFinal(string.getBytes()));
//		} catch (Exception e) {
//			return null;
//		}
//	}

	private static byte[] hexStringToByteArray(String string) {
		byte[] byArray = new byte[string.length() / 2];
		for (int i = 0; i < byArray.length; i++) {
			int n = i * 2;
			byArray[i] = (byte)Integer.parseInt(string.substring(n, n + 2), 16);
		}
		return byArray;
	}

	private static String byteArrayToHexString(byte[] byArray) {
		StringBuilder stringBuilder = new StringBuilder(byArray.length * 2);
		for (byte b : byArray) {
			int n2 = b & 0xFF;
			if (n2 < 16)
				stringBuilder.append('0');
			stringBuilder.append(Integer.toHexString(n2));
		}
		return stringBuilder.toString().toUpperCase();
	}

	public static void changeName(String id, final String username) {
		generateToken(token -> {
			Net.HttpRequest httpRequest = requestBuilder.newRequest().method("GET").url(wwwhost).content("action=changeName&user_id=" + id + "&user_name=" + username + "&token=" + token).build();
			Gdx.app.log(HttpServer.class.getName(), httpRequest.getUrl() + "?" + httpRequest.getContent());
			Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
				int numTries = 0;

				@Override
				public void cancelled() {
					Gdx.app.log(HttpServer.class.getName(), "cancelled");
					numTries++;
					if (this.numTries < 3) {
						Gdx.app.log(HttpServer.class.getName(), "http request failed, now trying again");
						Gdx.net.sendHttpRequest(httpRequest, this);
					}
				}

				@Override
				public void failed(Throwable throwable) {
					Gdx.app.log(HttpServer.class.getName(), throwable.getMessage());
					numTries++;
					if (numTries < 3) {
						Gdx.app.log(HttpServer.class.getName(), "http request failed, now trying again");
						Gdx.net.sendHttpRequest(httpRequest, this);
						return;
					}
					JewelThief.getInstance().toast(JewelThief.getInstance().getBundle().get("request_failed"), true);
				}

				@Override
				public void handleHttpResponse(Net.HttpResponse response) {
					this.numTries = 3;
					String content = response.getResultAsString();
					if (content.trim().length() == 0) {
						JewelThief.getInstance().getPreferences().putString("playername", username);
						JewelThief.getInstance().getPreferences().flush();
						return;
					}
					if (content.length() > 0)
						Gdx.app.log(HttpServer.class.getName(), content);
					JewelThief.getInstance().toast("Something went wrong :(", true);
				}
			});
		});
	}

	public static void fetchHighscores(final MenuScreen menuScreen, final String id, String username, int jewels, int seconds) {
		generateToken(token -> {
			final I18NBundle i18NBundle = JewelThief.getInstance().getBundle();
			Net.HttpRequest httpRequest = requestBuilder.newRequest().method("GET").url(wwwhost).content("action=fetchHighscores&user_id=" + id + "&user_name=" + username /*"Username"*/ + "&num_jewels=" + jewels + "&num_seconds=" + seconds + "&token=" + token).build();
			Gdx.app.log(HttpServer.class.getName(), httpRequest.getUrl() + "?" + httpRequest.getContent());
			menuScreen.setFetchingHighscores(true);
			Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
				int numTries = 0;

				@Override
				public void cancelled() {
					numTries++;
					if (numTries < 3) {
						Gdx.app.log(HttpServer.class.getName(), "http request failed, now trying again");
						Gdx.net.sendHttpRequest(httpRequest, this);
					} else
						JewelThief.getInstance().toast("Fetching highscores cancelled, please try again", true);
					menuScreen.setFetchingHighscores(false);
					menuScreen.setMyRank(-1);
				}

				@Override
				public void failed(Throwable serializable) {
					Gdx.app.log(HttpServer.class.getName(), serializable.getMessage());
					numTries++;
					if (numTries < 3) {
						Gdx.app.log(HttpServer.class.getName(), "http request failed, now trying again");
						Gdx.net.sendHttpRequest(httpRequest, this);
					} else {
						String requestFailed = i18NBundle.get("request_failed");
						JewelThief.getInstance().toast(requestFailed, true);
						ArrayList<String> highScores = new ArrayList<>();
						if (JewelThief.getInstance().getPreferences().contains("cachedHighscores")) {
							highScores.addAll(Arrays.asList(JewelThief.getInstance().getPreferences().getString("cachedHighscores").split("\n")));
							highScores.add("");
						}
						highScores.add("(".concat(requestFailed).concat(")"));
						highScores.add("");
						highScores.add(Utils.getBestScoreString());
						menuScreen.setHighscores(highScores.toArray(new String[0]));
					}
					menuScreen.setFetchingHighscores(false);
				}

				@Override
				public void handleHttpResponse(Net.HttpResponse response) {
					try {
						JsonValue responseData = new JsonReader().parse(response.getResultAsString());
						menuScreen.setMyRank(-1);
						ArrayList<String> highScores = new ArrayList<>();
						StringBuilder cache = new StringBuilder();
						for (int i = 0; i < responseData.size; i++) {
							JsonValue entry = responseData.get(i);
							highScores.add(
								entry.getString("user_name") + " with " + entry.getString("score") + " points (" + entry.getString("num_jewels")
									+ " jewels in " + Utils.secondsToTimeString(Integer.parseInt(entry.getString("num_seconds"))) + ")\n"
							);
							cache.append(highScores.get(highScores.size() - 1));
							if (id.equals(entry.getString("user_id")))
								menuScreen.setMyRank(i);
						}
						if (menuScreen.getMyRank() == -1)
							highScores.add(Utils.getBestScoreString());
						menuScreen.setHighscores(highScores.toArray(new String[0]));
						JewelThief.getInstance().getPreferences().putString("cachedHighscores", cache.toString()).flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					menuScreen.setFetchingHighscores(false);
				}
			});
		});
	}

	public static void submitHighscores(String id, String username, int jewels, int seconds) {
		generateToken(token -> {
			final Net.HttpRequest httpRequest = requestBuilder.newRequest().method("GET").url(wwwhost).content("action=submitHighscore&user_id=" + id + "&user_name=" + username + "&num_jewels=" + jewels + "&num_seconds=" + seconds + "&token=" + token).build();
			Gdx.app.log(HttpServer.class.getName(), httpRequest.getUrl() + "?" + httpRequest.getContent());
			Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener(){
				int numTries = 0;

				@Override
				public void cancelled() {
					Gdx.app.log(HttpServer.class.getName(), "cancelled");
					numTries++;
					if (numTries < 3) {
						Gdx.app.log(HttpServer.class.getName(), "http request failed, now trying again");
						Gdx.net.sendHttpRequest(httpRequest, this);
					}
				}

				@Override
				public void failed(Throwable throwable) {
					Gdx.app.log(HttpServer.class.getName(), throwable.getMessage());
					numTries++;
					if (numTries < 3) {
						Gdx.app.log(HttpServer.class.getName(), "http request failed, now trying again");
						Gdx.net.sendHttpRequest(httpRequest, this);
					}
				}

				@Override
				public void handleHttpResponse(Net.HttpResponse response) {
					numTries = 3;
					String content = response.getResultAsString().trim();
					if (content.length() > 0)
						Gdx.app.log(HttpServer.class.getName(), content);
				}
			});
		});
	}
}
