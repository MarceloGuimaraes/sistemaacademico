package br.com.pucminas.projeto.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SyncMec {

	private static final Logger LOGGER = Logger.getLogger(SyncMec.class);
	
	public void enviarDadosMec(String jsonObject, String url_sync_mec) {

		HttpClient httpClient = HttpClientBuilder.create().build();

		try {
			HttpPost httpPost = new HttpPost(url_sync_mec);
			httpPost.setEntity(new StringEntity(jsonObject, ContentType.APPLICATION_JSON));

			System.out.println("Executando a requisicao " + httpPost.getRequestLine());

			// Create a custom response handler
			ResponseHandler<String> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Status nao esperados: " + status);
				}
			};
			String responseBody = httpClient.execute(httpPost, responseHandler);
			System.out.println("----------------------------------------");
			System.out.println(responseBody);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Não foi possível enviar os dados para o servidor MEC", e);
		}
	}

	/*
	 * private void enviarDadosMec(String alunoJson) {
	 * 
	 * 
	 * try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
	 * 
	 * 
	 * StringEntity entity = new StringEntity(alunoJson,
	 * ContentType.APPLICATION_JSON);
	 * 
	 * // HttpClient httpClient = HttpClientBuilder.create().build(); HttpPost
	 * request = new HttpPost(CONS_URL_MEC); request.setEntity(entity);
	 * 
	 * HttpResponse response = httpclient.execute(request);
	 * 
	 * 
	 * } catch (IOException e) {
	 * LOGGER.error("Não foi possível enviar os dados para o servidor MEC", e); }
	 * 
	 * }
	 */

}
