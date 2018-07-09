package br.com.pucminas.projeto.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.pucminas.projeto.model.Aluno;
import br.com.pucminas.projeto.service.CadastroAlunoService;

//@RestController
//@RequestMapping("/alunosrest")
public class AlunoRestController {

	@Autowired
	private CadastroAlunoService cadastroAlunoService;

	@GetMapping
	public ResponseEntity<?> listar() {
		List<Aluno> alunoList = cadastroAlunoService.findAll();
		return !alunoList.isEmpty() ? ResponseEntity.ok(alunoList) : ResponseEntity.noContent().build();
	}

}
