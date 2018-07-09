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

import br.com.pucminas.projeto.model.Aluno;
import br.com.pucminas.projeto.repository.filter.AlunoFilter;
import br.com.pucminas.projeto.service.CadastroAlunoService;
import br.com.pucminas.projeto.service.SyncMec;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

	private static final Logger LOGGER = Logger.getLogger(AlunoController.class);

	private static final String CADASTRO_VIEW = "CadastroAluno";
	private static final String SYNC_MEC = "SyncAlunosMec";

	private static final String CONS_URL_MEC = "https://mockmec.herokuapp.com/mecalunosync";

	@Autowired
	private CadastroAlunoService cadastroAlunoService;

	@Autowired
	private SyncMec syncMec;

	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject("alunos", new Aluno());
		return mv;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@ModelAttribute("alunos") @Validated Aluno alunos, Errors errors,
			RedirectAttributes attributes) {
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
		mv.addObject("alunos", aluno);
		return mv;
	}

	@RequestMapping(value = "{codigo}", method = RequestMethod.DELETE)
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

		if (!alunoList.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();

			for (Aluno aluno : alunoList) {

				if (!aluno.getSyncMec()) {

					try {

						alunoJson = new String(mapper.writeValueAsString(aluno));
						
						syncMec.enviarDadosMec(alunoJson, CONS_URL_MEC);
						
						aluno.setSyncMec(true);
						cadastroAlunoService.salvar(aluno);

					} catch (JsonProcessingException e) {
						LOGGER.error("Não foi possível converter os dados para JSON", e);
					}
				}

			}
		}

		return alunoJson;

	}

}
