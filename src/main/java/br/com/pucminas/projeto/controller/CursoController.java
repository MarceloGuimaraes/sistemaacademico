package br.com.pucminas.projeto.controller;


import java.util.List;

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

import br.com.pucminas.projeto.model.Curso;
import br.com.pucminas.projeto.repository.filter.CursoFilter;
import br.com.pucminas.projeto.service.CursoServices;
import br.com.pucminas.projeto.service.SyncMec;

@Controller
@RequestMapping("/cursos")
public class CursoController {
	
	private static final Logger LOGGER = Logger.getLogger(CursoController.class);

	private static final String CADASTRO_VIEW = "CadastroCurso";

	private static final String SYNC_MEC = "SincronismoMec";

	private static final String CONS_URL_MEC = "https://mockmec.herokuapp.com/meccursosync";
	
	@Autowired
	private CursoServices cursoServices;
	
	@Autowired
	private SyncMec syncMec;

	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView("CadastroCurso");
		mv.addObject("cursos", new Curso()); 
		return mv;
	}
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") CursoFilter filtro) {
		List<Curso> todosCursos = cursoServices.filtrar(filtro);
		
		ModelAndView mv = new ModelAndView("Cursos");
		mv.addObject("cursos", todosCursos);
		return mv;
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@ModelAttribute("cursos") @Validated Curso cursos, Errors errors,
			RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			return CADASTRO_VIEW;
		}

		try {
			cursoServices.salvar(cursos);
			attributes.addFlashAttribute("mensagem", "Curso salvo com sucesso!");
			return "redirect:/cursos";
		} catch (IllegalArgumentException e) {

			return CADASTRO_VIEW;
		}
	}
	
	
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Curso curso) {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject("cursos", curso);
		return mv;
	}
	
	
	@RequestMapping(value = "{codigo}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes attributes) {
		cursoServices.excluir(codigo);

		attributes.addFlashAttribute("mensagem", "Curso excluído com sucesso!");
		return "redirect:/cursos";
	}
	
	
	@RequestMapping("/sync")
	public ModelAndView sync() {

		ModelAndView mv = new ModelAndView(SYNC_MEC);

		String cursoListJson = cursoJson();
		mv.addObject("cursoListJson", cursoListJson);
		return mv;
	}
	
	
		private String cursoJson() {

		String cursoJson = null;

		List<Curso> cursoList = cursoServices.findAll();

		if (!cursoList.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();

			for (Curso curso : cursoList) {

				if (!curso.getSyncMec()) {

					try {

						cursoJson = new String(mapper.writeValueAsString(curso));
						
						syncMec.enviarDadosMec(cursoJson, CONS_URL_MEC);
						
						curso.setSyncMec(true);
						cursoServices.salvar(curso);

					} catch (JsonProcessingException e) {
						LOGGER.error("Não foi possível converter os dados para JSON", e);
					}
				}

			}
		}

		return cursoJson;

	}
	
}
