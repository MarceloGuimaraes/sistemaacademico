package br.com.pucminas.projeto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sync")
public class SyncMecController {

	private static final String SYNC_MEC = "SyncAlunosMec";

	@RequestMapping(value = "/alunos", method = RequestMethod.POST)
	public String alunos() {
		return "redirect:/alunos";
	}

	@RequestMapping("/sync")
	public ModelAndView sync() {
		ModelAndView mv = new ModelAndView(SYNC_MEC);
		return mv;
	}

}
