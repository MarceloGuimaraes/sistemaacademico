package br.com.pucminas.projeto.controller;


import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pucminas.projeto.model.Aluno;
import br.com.pucminas.projeto.repository.filter.AlunoFilter;
import br.com.pucminas.projeto.resource.AlunoRestController;
import br.com.pucminas.projeto.service.CadastroAlunoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {
	
	private static final Logger LOGGER = Logger.getLogger(AlunoController.class);
	
	
	private static final String CADASTRO_VIEW = "CadastroAluno";
	private static final String SYNC_MEC = "SyncAlunosMec";


	private static final String CONS_URL_MEC = "http://portal.mec.gov.br";
	

	@Autowired
	private CadastroAlunoService cadastroAlunoService;
	
	
	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject("alunos", new Aluno());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@ModelAttribute("alunos")@Validated Aluno alunos, Errors errors, RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			return CADASTRO_VIEW;
		}

		try {
			cadastroAlunoService.salvar(alunos);
			attributes.addFlashAttribute("mensagem", "Aluno salvo com sucesso!");
			return "redirect:/alunos";
		} catch (IllegalArgumentException e) {
			
			return CADASTRO_VIEW;
		}
	}
	
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") AlunoFilter filtro) {
		List<Aluno> todosAlunos = cadastroAlunoService.filtrar(filtro);
		
		ModelAndView mv = new ModelAndView("PesquisaAlunos");
		mv.addObject("alunos", todosAlunos);
		return mv;
	}
		
	
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Aluno aluno) {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW); 
		mv.addObject("alunos",aluno);
		return mv;
	}
	
	@RequestMapping(value="{codigo}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes attributes) {
		cadastroAlunoService.excluir(codigo);
		
		attributes.addFlashAttribute("mensagem", "Aluno excluído com sucesso!");
		return "redirect:/alunos";
	}
	
	@RequestMapping("/sync")
	public ModelAndView sync() {
		
		ModelAndView mv = new ModelAndView(SYNC_MEC);
		
		String alunoListJson = alunoJson(); 
		mv.addObject("alunoListJson", alunoListJson);
		return mv;
	}
	
	
	private String alunoJson() {
		
		String alunoJson = null;
		
		List<Aluno> alunoList = cadastroAlunoService.findAll();
		
		if(!alunoList.isEmpty() ) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				alunoJson = new String(mapper.writeValueAsString(alunoList));
				//enviarDadosMec(alunoJson);
				
			} catch (JsonProcessingException e) {
				LOGGER.error("Não foi possível converter os dados para JSON", e);
			}
		}
		
		return  alunoJson;
		
	}
	
	
private void enviarDadosMec(String alunoJson) {
		
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(CONS_URL_MEC);
            httpPost.setEntity(new StringEntity(alunoJson,
	                ContentType.APPLICATION_JSON));

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
            String responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	LOGGER.error("Não foi possível enviar os dados para o servidor MEC", e);
		}
    }
	
	
/*	private void enviarDadosMec(String alunoJson) {
		
		
	    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
		
	    	
	        StringEntity entity = new StringEntity(alunoJson,
	                ContentType.APPLICATION_JSON);

	       // HttpClient httpClient = HttpClientBuilder.create().build();
	        HttpPost request = new HttpPost(CONS_URL_MEC);
	        request.setEntity(entity);

	        HttpResponse response = httpclient.execute(request);
        
	        
	    } catch (IOException e) {
        	LOGGER.error("Não foi possível enviar os dados para o servidor MEC", e);
		}
	        
	}*/

}
